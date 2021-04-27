package uk.gov.companieshouse.efs.web.controller;

import static uk.gov.companieshouse.efs.web.controller.ProposedCompanyControllerImpl.ATTRIBUTE_NAME;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.efs.submissions.CompanyApi;
import uk.gov.companieshouse.efs.web.model.ProposedCompanyModel;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
@SessionAttributes(ATTRIBUTE_NAME)
public class ProposedCompanyControllerImpl extends BaseControllerImpl implements ProposedCompanyController {
    @Value("${registrations.enabled:false}")
    private boolean registrationsEnabled;

    /**
     * Define the model name for this action.
     */
    public static final String ATTRIBUTE_NAME = "proposedCompany";
    public static final String TEMP_COMPANY_NUMBER = "99999999";
    
    private ProposedCompanyModel proposedCompanyAttribute;

    @Autowired
    public ProposedCompanyControllerImpl(final Logger logger, final SessionService sessionService,
        final ApiClientService apiClientService, final ProposedCompanyModel proposedCompanyAttribute) {
        super(logger, sessionService, apiClientService);

        this.proposedCompanyAttribute = proposedCompanyAttribute;
    }

    @ModelAttribute(ATTRIBUTE_NAME)
    public ProposedCompanyModel getProposedCompanyAttribute() {
        return proposedCompanyAttribute;
    }

    @Override
    public String getViewName() {
        return ViewConstants.PROPOSED_COMPANY.asView();
    }

    @Override
    public String prepare(@PathVariable final String id,
        @ModelAttribute(ATTRIBUTE_NAME) ProposedCompanyModel proposedCompany, Model model,
        HttpServletRequest request) {

        if (!registrationsEnabled) {
            return ViewConstants.MISSING.asView();
        }
        // Assign our previously saved response to our model.
        proposedCompany.setSubmissionId(id);
        proposedCompany.setDetails(
            new CompanyApi(TEMP_COMPANY_NUMBER, proposedCompanyAttribute.getName()));
        proposedCompany.setNameRequired(null);

        return getViewName();
    }

    @Override
    public String process(@PathVariable final String id,
        @Valid @ModelAttribute(ATTRIBUTE_NAME) ProposedCompanyModel proposedCompany,
        BindingResult binding, Model model, HttpServletRequest request, HttpSession session) {

        if (!registrationsEnabled) {
            return ViewConstants.MISSING.asView();
        }
        if (binding.hasErrors()) {
            addTrackingAttributeToModel(model);
            return getViewName();
        }

        // Update our persistent model with the latest response.
        resetNameRequiredIfNotUsed(proposedCompany);

        return ViewConstants.CATEGORY_SELECTION.asRedirectUri(chsUrl, id, proposedCompany.getNumber());
    }

    private void resetNameRequiredIfNotUsed(final ProposedCompanyModel proposedCompany) {
        final Boolean nameRequired = proposedCompany.getNameRequired();
        final String proposedName = Boolean.TRUE.equals(nameRequired) ? StringUtils.defaultIfBlank(
            proposedCompany.getName(), "") : null;

        proposedCompany.setName(proposedName);
    }

}