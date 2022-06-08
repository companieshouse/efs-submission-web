package uk.gov.companieshouse.efs.web.controller;

import java.util.Objects;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
@SessionAttributes({FormTemplateControllerImpl.ATTRIBUTE_NAME, CategoryTemplateControllerImpl.ATTRIBUTE_NAME})
public class ReviewSelectionControllerImpl extends BaseControllerImpl implements ReviewSelectionController {

    /**
     * Constructor used by child controllers.
     */
    public ReviewSelectionControllerImpl(final Logger logger, final SessionService sessionService,
                                         final ApiClientService apiClientService,
                                         final FormTemplateService formTemplateService,
                                         final CategoryTemplateService categoryTemplateService) {
        super(logger, sessionService, apiClientService, formTemplateService, categoryTemplateService);
    }

    @Override
    public String getViewName() {
        return ViewConstants.REVIEW_SELECTION_LIQ13.asView();
    }

    @Override
    public String reviewSelection(@PathVariable String id, @PathVariable String companyNumber, Model model,
                                  HttpServletRequest servletRequest) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
        if (!ALLOWED_STATUSES.contains(submissionApi.getStatus())) {
            return ViewConstants.GONE.asView();
        }

        addTrackingAttributeToModel(model);

        return ViewConstants.REVIEW_SELECTION_LIQ13.asView();
    }

    @Override
    public String postReviewSelection(@PathVariable String id, @PathVariable String companyNumber,
                                      CategoryTemplateModel categoryTemplateAttribute, BindingResult binding,
                                      Model model, ServletRequest servletRequest, HttpSession session) {

        return ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(chsUrl, id, companyNumber);
    }
}