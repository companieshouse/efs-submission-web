package uk.gov.companieshouse.efs.web.controller;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.ServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.maintenance.MaintenanceCheckApi;
import uk.gov.companieshouse.api.model.efs.maintenance.ServiceStatus;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.logging.Logger;

/**
 * Handles the HTTP requests for the web application.
 */
@Controller
@RequestMapping(BaseControllerImpl.SERVICE_URI)
@SessionAttributes(CategoryTemplateControllerImpl.ATTRIBUTE_NAME)
public class StaticPageControllerImpl extends BaseControllerImpl implements StaticPageController {
    public static final String COMPANY_LOOKUP_SEARCH = "/company-lookup/search";
    @Value("${registrations.enabled:false}")
    private boolean registrationsEnabled;

    /**
     * Constructor.
     *
     * @param logger           the CH logger
     */
    @Autowired
    public StaticPageControllerImpl(final Logger logger, final ApiClientService apiClientService) {
        super(logger, apiClientService);
    }

    /**
     * Handles the start page request. Checks for service maintenance and renders the start view.
     *
     * @param categoryTemplateAttribute the model attribute
     * @param model the UI model
     * @param redirectAttributes redirect attributes for flash scope
     * @param servletRequest the servlet request
     * @param sessionStatus the session status
     * @return the view name
     */
    @Override
    public String start(@ModelAttribute final CategoryTemplateModel categoryTemplateAttribute, final Model model,
                        final RedirectAttributes redirectAttributes, final ServletRequest servletRequest,
                        final SessionStatus sessionStatus) {
        if (!isServiceAvailable(redirectAttributes)) {
            return ViewConstants.UNAVAILABLE.asRedirectUri(chsUrl);
        }
        // Clear any previous session attributes to ensure a fresh start page
        sessionStatus.setComplete();
        model.addAttribute(TEMPLATE_NAME, ViewConstants.START.asView());
        return ViewConstants.START.asView();
    }

    /**
     * Checks if the service is available (i.e. not under planned maintenance).
     * If unavailable, adds the maintenance end date to redirect attributes to inform the visitor when they may retry.
     *
     * @param redirectAttributes redirect attributes for flash scope
     * @return true if service is available, false otherwise
     */
    private boolean isServiceAvailable(final RedirectAttributes redirectAttributes) {
        try {
            final ApiResponse<MaintenanceCheckApi> response = apiClientService.getMaintenanceCheck();
            final HttpStatus status = HttpStatus.valueOf(response.getStatusCode());

            logger.debug(String.format("Maintenance check response status: %s", status));
            if (response.getData().getStatus().equals(ServiceStatus.OUT_OF_SERVICE)) {
                final DateTimeFormatter displayDateFormat = DateTimeFormatter.ofPattern("h:mm a 'on' EEEE d MMMM yyyy");
                final Instant parsed = Instant.parse(response.getData().getMaintenanceEnd());
                final LocalDateTime localEndTime = LocalDateTime.ofInstant(parsed, ZoneId.systemDefault());
                redirectAttributes.addFlashAttribute("date", displayDateFormat.format(localEndTime));

                return false;
            }
        } catch (final ResponseStatusException e) {
            // Log the error but do not block the visitor from accessing the start page if maintenance check fails
            logger.error(String.format("Maintenance check response status: %s", e.getStatusCode()), e);
        }
        return true;
    }

    @Override
    public String guidance(final Model model, final ServletRequest servletRequest) {
        model.addAttribute(TEMPLATE_NAME, ViewConstants.GUIDANCE.asView());

        return ViewConstants.GUIDANCE.asView();
    }

    @Override
    public String insolvencyGuidance(final Model model, final ServletRequest servletRequest) {
        model.addAttribute(TEMPLATE_NAME, ViewConstants.INSOLVENCY_GUIDANCE.asView());

        return ViewConstants.INSOLVENCY_GUIDANCE.asView();
    }

    @Override
    public String accessibilityStatement(final Model model, final ServletRequest servletRequest) {

        model.addAttribute(TEMPLATE_NAME, ViewConstants.ACCESSIBILITY.asView());

        return ViewConstants.ACCESSIBILITY.asView();
    }

    @Override
    public String serviceUnavailable(final Model model, final ServletRequest servletRequest,
                                     @ModelAttribute("date") final String date) {

        if (StringUtils.isBlank(date)) {
            return ViewConstants.START.asRedirectUri(chsUrl);
        }
        model.addAttribute(TEMPLATE_NAME, ViewConstants.UNAVAILABLE.asView());

        return ViewConstants.UNAVAILABLE.asView();
    }

    @Override
    public String companyLookup(final String id, final Model model, final ServletRequest servletRequest,
        final RedirectAttributes attributes) {
        attributes.addAttribute("forward", String.format("/efs-submission/%s/company/{companyNumber}/details", id));

        return MessageFormat.format("{0}{1}{2}{3}", UrlBasedViewResolver.REDIRECT_URL_PREFIX,
            chsUrl, COMPANY_LOOKUP_SEARCH, registrationsEnabled ? "?noCompanyOption=1" : "");
    }

}