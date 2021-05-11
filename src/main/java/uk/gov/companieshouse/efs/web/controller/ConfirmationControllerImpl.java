package uk.gov.companieshouse.efs.web.controller;

import java.util.EnumSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionFormApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTypeConstants;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
@SessionAttributes(
    {FormTemplateControllerImpl.ATTRIBUTE_NAME, CategoryTemplateControllerImpl.ATTRIBUTE_NAME,
        CompanyDetailControllerImpl.ATTRIBUTE_NAME})
public class ConfirmationControllerImpl extends BaseControllerImpl implements ConfirmationController {

    /**
     * Constructor used by child controllers.
     *
     * @param logger the CH logger
     */
    @Autowired
    public ConfirmationControllerImpl(final Logger logger, SessionService sessionService,
                                      ApiClientService apiClientService, FormTemplateService formTemplateService,
                                      CategoryTemplateService categoryTemplateService) {
        super(logger, sessionService, apiClientService);
        this.formTemplateService = formTemplateService;
        this.categoryTemplateService = categoryTemplateService;

    }

    @Override
    public String getViewName() {
        return ViewConstants.CONFIRMATION.asView();
    }

    @Override
    public String getConfirmation(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(CompanyDetailControllerImpl.ATTRIBUTE_NAME)
            CompanyDetail companyDetailAttribute, Model model, HttpServletRequest request,
        HttpSession session, SessionStatus sessionStatus) {

        final SubmissionApi submission = getSubmission(id);
        final SubmissionStatus submissionStatus = submission.getStatus();
        final EnumSet<SubmissionStatus> allowedStatuses =
            EnumSet.of(SubmissionStatus.OPEN, SubmissionStatus.PAYMENT_RECEIVED,
                SubmissionStatus.PAYMENT_FAILED);

        if (!allowedStatuses.contains(submissionStatus)) {
            return ViewConstants.GONE.asView();
        }

        final ApiResponse<SubmissionResponseApi> response = apiClientService.putSubmissionCompleted(id);

        logApiResponse(response, id, "PUT /efs-submission-api/submission/" + id);
        model.addAttribute("confirmationRef", submission.getConfirmationReference());
        model.addAttribute("newSubmissionUri",
            ViewConstants.NEW_SUBMISSION.asUriForCompany(chsUrl, submission.getCompany().getCompanyNumber()));
        // repopulate companyDetail
        companyDetailAttribute.setCompanyName(submission.getCompany().getCompanyName());
        companyDetailAttribute.setCompanyNumber(submission.getCompany().getCompanyNumber());

        SubmissionFormApi submissionFormApi = submission.getSubmissionForm();

        ApiResponse<FormTemplateApi> formTemplateApi =
                formTemplateService.getFormTemplate(submissionFormApi.getFormType());
        String formCategory = formTemplateApi.getData().getFormCategory();
        CategoryTypeConstants topLevelCategory = categoryTemplateService.getTopLevelCategory(formCategory);

        boolean isRegPowers = topLevelCategory.equals(CategoryTypeConstants.REGISTRAR_POWERS);
        model.addAttribute("registrarsPowers", isRegPowers);

        addTrackingAttributeToModel(model);
        sessionStatus.setComplete();

        return ViewConstants.CONFIRMATION.asView();
    }
}
