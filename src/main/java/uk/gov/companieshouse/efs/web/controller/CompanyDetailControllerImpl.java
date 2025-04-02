package uk.gov.companieshouse.efs.web.controller;

import static uk.gov.companieshouse.efs.web.controller.CompanyDetailControllerImpl.ATTRIBUTE_NAME;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.submissions.CompanyApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.company.CompanyService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;

@Controller
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public class CompanyDetailControllerImpl extends BaseControllerImpl implements CompanyDetailController {
    @Value("${registrations.enabled:false}")
    private boolean registrationsEnabled;

    @Autowired
    @Qualifier("prefix-block-list")
    private List<String> prefixBlockList;

    private final CompanyService companyService;
    private final CompanyDetail companyDetailAttribute;

    /**
     * Define the model name for this action.
     */
    public static final String ATTRIBUTE_NAME = "companyDetail";

    /**
     * Constructor.
     *
     * @param companyService         calls CompanyProfile API
     * @param sessionService         the Session service
     * @param apiClientService       the API Client Service
     * @param logger                 the CH logger
     * @param companyDetailAttribute the model attribute
     */
    @Autowired
    public CompanyDetailControllerImpl(final CompanyService companyService, final SessionService sessionService,
        final ApiClientService apiClientService, final Logger logger, final CompanyDetail companyDetailAttribute) {
        super(logger, sessionService, apiClientService);
        this.companyService = companyService;
        this.companyDetailAttribute = companyDetailAttribute;
    }

    @ModelAttribute(ATTRIBUTE_NAME)
    public CompanyDetail getCompanyDetailAttribute() {
        return companyDetailAttribute;
    }

    @Override
    public String getViewName() {
        return ViewConstants.COMPANY_DETAIL.asView();
    }

    @Override
    public String getCompanyDetail(final String id, final String companyNumber,
        final CompanyDetail companyDetailAttribute, final Model model, final HttpServletRequest request) {

        if (StringUtils.equals(companyNumber, "noCompany")) {
            return registrationsEnabled ? ViewConstants.PROPOSED_COMPANY.asRedirectUri(chsUrl,
                id, "noCompany") : ViewConstants.MISSING.asView();
        }
        companyDetailAttribute.setSubmissionId(id);
        companyService.getCompanyDetail(companyDetailAttribute, companyNumber);
        addTrackingAttributeToModel(model);

        final boolean hasBlockedPrefix = Optional.ofNullable(prefixBlockList)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .anyMatch(companyNumber::startsWith);

        if (hasBlockedPrefix) {
            return ViewConstants.RESTRICTED_COMPANY_TYPE.asRedirectUri(chsUrl, id,
                companyNumber);
        }

        return ViewConstants.COMPANY_DETAIL.asView();
    }

    @Override
    public String postCompanyDetail(final String id, final String companyNumber,
        final CompanyDetail companyDetailAttribute, final Model model, final HttpServletRequest request) {

        //Temporary log for troubleshooting 2/4/2025
        logger.info("Session CompanyDetail: " + companyDetailAttribute);

            final ApiResponse<SubmissionResponseApi> response = apiClientService.putCompany(id,
                new CompanyApi(companyDetailAttribute.getCompanyNumber(), companyDetailAttribute.getCompanyName()));

            logApiResponse(response, "", MessageFormat.format("PUT /efs-submission-api/submission/{0}/company", id));


        return ViewConstants.CATEGORY_SELECTION.asRedirectUri(chsUrl, id, companyNumber);
    }

    @Override
    public String restrictedCompanyType(final String id, final String companyNumber,
        final CompanyDetail companyDetailAttribute, final Model model, final HttpServletRequest request) {

        companyDetailAttribute.setSubmissionId(id);
        addTrackingAttributeToModel(model);

        return ViewConstants.RESTRICTED_COMPANY_TYPE.asView();
    }
}
