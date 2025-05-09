package uk.gov.companieshouse.efs.web.controller;

import java.util.Objects;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

import static uk.gov.companieshouse.efs.web.controller.CompanyDetailControllerImpl.ATTRIBUTE_NAME;

@Controller
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public class ResolutionsInfoControllerImpl extends BaseControllerImpl implements ResolutionsInfoController {


    /**
     * Constructor used by child controllers.
     *
     */
    public ResolutionsInfoControllerImpl(final Logger logger, final SessionService sessionService,
        final ApiClientService apiClientService, final FormTemplateService formTemplateService,
        final CategoryTemplateService categoryTemplateService) {
        super(logger, sessionService, apiClientService, formTemplateService,
            categoryTemplateService);
    }

    @Override
    public String getViewName() {
        return ViewConstants.RESOLUTIONS_INFO.asView();
    }

    @Override
    public String resolutionsInfo(@PathVariable String id, @PathVariable String companyNumber, FormTemplateModel formTemplateAttribute, Model model, HttpServletRequest servletRequest) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
        formTemplateAttribute.setSubmissionId(submissionApi.getId());

        if (submissionApi.getCompany() != null) {
            formTemplateAttribute.setCompanyNumber(submissionApi.getCompany().getCompanyNumber());
        }

        if (!ALLOWED_STATUSES.contains(submissionApi.getStatus())) {
            return ViewConstants.GONE.asView();
        }

        addTrackingAttributeToModel(model);

        return ViewConstants.RESOLUTIONS_INFO.asView();
    }

    @Override
    public String postResolutionsInfo(@PathVariable String id, @PathVariable String companyNumber,
        FormTemplateModel formTemplateModel, BindingResult binding, Model model, ServletRequest servletRequest, HttpSession session) {

        return ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(chsUrl, id, companyNumber);
    }
}