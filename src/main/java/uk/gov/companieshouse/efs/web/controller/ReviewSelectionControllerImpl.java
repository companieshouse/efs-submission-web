package uk.gov.companieshouse.efs.web.controller;

import static uk.gov.companieshouse.efs.web.controller.ReviewSelectionControllerImpl.ATTRIBUTE_NAME;

import java.util.Objects;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.model.ReviewSelectionModel;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
@SessionAttributes(
        {ATTRIBUTE_NAME, FormTemplateControllerImpl.ATTRIBUTE_NAME, CategoryTemplateControllerImpl.ATTRIBUTE_NAME})
@SuppressWarnings("squid:S3753")
public class ReviewSelectionControllerImpl extends BaseControllerImpl implements ReviewSelectionController {

    /**
     * Define the model name for this action.
     */
    public static final String ATTRIBUTE_NAME = "reviewSelection";

    private ReviewSelectionModel reviewSelectionAttribute;

    /**
     * Constructor used by child controllers.
     */
    @Autowired
    public ReviewSelectionControllerImpl(final Logger logger, final SessionService sessionService,
                                         final ApiClientService apiClientService,
                                         final ReviewSelectionModel reviewSelectionAttribute,
                                         final FormTemplateService formTemplateService,
                                         final CategoryTemplateService categoryTemplateService) {
        super(logger, sessionService, apiClientService, formTemplateService, categoryTemplateService);
        this.reviewSelectionAttribute = reviewSelectionAttribute;
    }

    @ModelAttribute(ATTRIBUTE_NAME)
    public ReviewSelectionModel getReviewSelectionAttribute() {
        return reviewSelectionAttribute;
    }

    @Override
    public String getViewName() {
        return ViewConstants.REVIEW_SELECTION_LIQ13.asView();
    }

    @Override
    public String reviewSelection(@PathVariable String id, @PathVariable String companyNumber,
                                  @ModelAttribute(ATTRIBUTE_NAME) ReviewSelectionModel reviewSelectionAttribute,
                                  @ModelAttribute(FormTemplateControllerImpl.ATTRIBUTE_NAME)
                                  FormTemplateModel formTemplateAttribute, Model model,
                                  HttpServletRequest servletRequest) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
        if (!ALLOWED_STATUSES.contains(submissionApi.getStatus())) {
            return ViewConstants.GONE.asView();
        }

        reviewSelectionAttribute.setSubmissionId(submissionApi.getId());
        reviewSelectionAttribute.setConfirmed("");
        addTrackingAttributeToModel(model);

        return ViewConstants.REVIEW_SELECTION_LIQ13.asView();
    }

    @Override
    public String postReviewSelection(@PathVariable String id, @PathVariable String companyNumber,
                                      @Valid @ModelAttribute(ATTRIBUTE_NAME)
                                      ReviewSelectionModel reviewSelectionAttribute, BindingResult binding,
                                      @ModelAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME)
                                      CategoryTemplateModel categoryTemplateAttribute,
                                      @ModelAttribute(FormTemplateControllerImpl.ATTRIBUTE_NAME)
                                      FormTemplateModel formTemplateAttribute, Model model,
                                      ServletRequest servletRequest, HttpSession session) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));

        if (!verifySubmission(submissionApi)) {
            return ViewConstants.ERROR.asView();
        }
        if (binding.hasErrors()) {
            addTrackingAttributeToModel(model);
            return ViewConstants.REVIEW_SELECTION_LIQ13.asView();
        }

        String redirectUri = ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(chsUrl, id, companyNumber);

        if (Objects.equals(reviewSelectionAttribute.getConfirmed(), "N")) {
            // Remove preselection of form type on Document selection screen
            formTemplateAttribute.setFormType("");
            return ViewConstants.DOCUMENT_SELECTION.asRedirectUri(chsUrl, id, companyNumber,
                    categoryTemplateAttribute.getCategoryType());
        }
        return redirectUri;
    }
}