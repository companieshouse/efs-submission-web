package uk.gov.companieshouse.efs.web.controller;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
public class RegistrationsInfoControllerImpl extends BaseControllerImpl implements RegistrationsInfoController {
    @Value("${registrations.enabled:false}")
    private boolean registrationsEnabled;



    /**
     * Constructor used by child controllers.
     *
     */
    public RegistrationsInfoControllerImpl(final Logger logger, final SessionService sessionService,
        final ApiClientService apiClientService, final FormTemplateService formTemplateService,
        final CategoryTemplateService categoryTemplateService) {
        super(logger, sessionService, apiClientService, formTemplateService,
            categoryTemplateService);
    }

    @Override
    public String getViewName() {
        return ViewConstants.REGISTRATIONS_INFO.asView();
    }

    @Override
    public String registrationsInfo(@PathVariable String id, @PathVariable String companyNumber,
        CategoryTemplateModel categoryTemplateAttribute, Model model, HttpServletRequest servletRequest) {

        if (registrationsEnabled) {
            final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
            model.addAttribute("submissionId", submissionApi.getId());

            if (submissionApi.getStatus() != SubmissionStatus.OPEN) {
                return ViewConstants.GONE.asView();
            }

            addTrackingAttributeToModel(model);

            return ViewConstants.REGISTRATIONS_INFO.asView();
        }
        else {
            return ViewConstants.MISSING.asView();
        }
    }

}