package uk.gov.companieshouse.efs.web.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateListApi;
import uk.gov.companieshouse.api.model.efs.submissions.FormTypeApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.model.Sh19TemplateModel;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

import static uk.gov.companieshouse.efs.web.controller.CheckDetailsControllerImpl.ATTRIBUTE_NAME;

@Controller
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public class Sh19DeliveryControllerImpl extends BaseControllerImpl implements Sh19DeliveryController {

    public static final String SH_19 = "SH19";
    public static final String SH_19_SAMEDAY = "SH19_SAMEDAY";
    private Sh19TemplateModel sh19TemplateAttribute;

    /**
     * Define the model name for this action.
     */
    public static final String ATTRIBUTE_NAME = "sh19Template";

    /**
     * Constructor used by child controllers.
     *
     */
    @Autowired
    public Sh19DeliveryControllerImpl(final Logger logger, final SessionService sessionService,
                                      final ApiClientService apiClientService, final FormTemplateService formTemplateService,
                                      final CategoryTemplateService categoryTemplateService,
                                      @Qualifier(ATTRIBUTE_NAME) final Sh19TemplateModel sh19TemplateAttribute) {
        super(logger, sessionService, apiClientService, formTemplateService,
            categoryTemplateService);
        this.sh19TemplateAttribute = sh19TemplateAttribute;
    }

    @ModelAttribute(ATTRIBUTE_NAME)
    public Sh19TemplateModel getSh19TemplateAttribute() {
        return sh19TemplateAttribute;
    }

    @Override
    public String getViewName() {
        return ViewConstants.SH19_DELIVERY.asView();
    }

    @Override
    public String sh19Delivery(@PathVariable String id, @PathVariable String companyNumber,
           @ModelAttribute(ATTRIBUTE_NAME) Sh19TemplateModel sh19TemplateAttribute,
           final Model model, HttpServletRequest servletRequest) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));

        if (!ALLOWED_STATUSES.contains(submissionApi.getStatus())) {
            return ViewConstants.GONE.asView();
        }

        List<FormTemplateApi> sh19FormList = new FormTemplateListApi();

        // refresh form template list from repository
        ApiResponse<FormTemplateApi> formResponse = formTemplateService.getFormTemplate(SH_19);
        sh19FormList.add(formResponse.getData());
        formResponse = formTemplateService.getFormTemplate(SH_19_SAMEDAY);
        sh19FormList.add(formResponse.getData());

        sh19TemplateAttribute.setFormTemplateList(sh19FormList);

        sh19TemplateAttribute.setSubmissionId(submissionApi.getId());

        final Optional<FormTemplateApi> selectedForm = sh19TemplateAttribute.getFormTemplateList().stream()
            .filter(f -> f.getFormType().equals(sh19TemplateAttribute.getFormType()))
            .findFirst();

        if (!selectedForm.isPresent()) {
            sh19TemplateAttribute.setDetails(new FormTemplateApi());
        }

        addTrackingAttributeToModel(model);

        return ViewConstants.SH19_DELIVERY.asView();
    }

    @Override
    @PostMapping(value = {"{id}/company/{companyNumber}/sh19-delivery"},
        params = {"action=submit"})
    public String postSh19Delivery(@PathVariable String id, @PathVariable String companyNumber,
                                   @Valid @ModelAttribute(ATTRIBUTE_NAME) Sh19TemplateModel sh19TemplateAttribute,
                                   BindingResult binding, Model model, ServletRequest servletRequest) {

        if (binding.hasErrors()) {
            addTrackingAttributeToModel(model);

            return ViewConstants.SH19_DELIVERY.asView();
        }

        final String selectedFormType = sh19TemplateAttribute.getFormType();
        final FormTemplateApi selectedFormTemplate = getSelectedFormTemplate(selectedFormType);

        Objects.requireNonNull(selectedFormTemplate);
        // Call the API layer to persist the form type.
        ApiResponse<SubmissionResponseApi> submissionResponse = apiClientService.putFormType(id,
            new FormTypeApi(selectedFormType));
        logApiResponse(submissionResponse, id, "Form type: " + submissionResponse.getData().getId());

        sh19TemplateAttribute.setDetails(selectedFormTemplate);

        return ViewConstants.CHECK_DETAILS.asRedirectUri(chsUrl, id, companyNumber);
    }

    private FormTemplateApi getSelectedFormTemplate(final String selectedFormType) {
        return sh19TemplateAttribute.getFormTemplateList().stream().filter(
            c -> c.getFormType().equals(selectedFormType)).findFirst().orElse(null);
    }
}