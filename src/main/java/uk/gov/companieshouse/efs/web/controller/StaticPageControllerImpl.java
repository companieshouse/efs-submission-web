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

    @Override
    public String start(@ModelAttribute CategoryTemplateModel categoryTemplateAttribute, Model model,
                        RedirectAttributes redirectAttributes, ServletRequest servletRequest,
                        SessionStatus sessionStatus) {
        final ApiResponse<MaintenanceCheckApi> response;

        try {
            response = apiClientService.getMaintenanceCheck();

            final HttpStatus status = HttpStatus.valueOf(response.getStatusCode());

            logger.debug(String.format("Maintenance check response status: %s", status));
        } catch (ResponseStatusException e) {
            logger.error(String.format("Maintenance check response status: %s", e.getStatus()), e);

            throw e;
        }
        if (response.getData().getStatus().equals(ServiceStatus.OUT_OF_SERVICE)) {
            final DateTimeFormatter displayDateFormat = DateTimeFormatter.ofPattern(
                "h:mm a 'on' EEEE d MMMM yyyy");
            final String maintenanceEnd = response.getData().getMaintenanceEnd();
            final Instant parsed = Instant.parse(maintenanceEnd);
            LocalDateTime localEndTime = LocalDateTime.ofInstant(parsed, ZoneId.systemDefault());
            redirectAttributes.addFlashAttribute("date", displayDateFormat.format(localEndTime));

            return ViewConstants.UNAVAILABLE.asRedirectUri(chsUrl);
        }

        sessionStatus.setComplete(); // invalidate the user's previous session if they have signed out
        model.addAttribute(TEMPLATE_NAME, ViewConstants.START.asView());

        return ViewConstants.START.asView();
    }

    @Override
    public String guidance(Model model, ServletRequest servletRequest) {
        model.addAttribute(TEMPLATE_NAME, ViewConstants.GUIDANCE.asView());

        return ViewConstants.GUIDANCE.asView();
    }

    @Override
    public String insolvencyGuidance(Model model, ServletRequest servletRequest) {
        model.addAttribute(TEMPLATE_NAME, ViewConstants.INSOLVENCY_GUIDANCE.asView());

        return ViewConstants.INSOLVENCY_GUIDANCE.asView();
    }

    @Override
    public String accessibilityStatement(Model model, ServletRequest servletRequest) {

        model.addAttribute(TEMPLATE_NAME, ViewConstants.ACCESSIBILITY.asView());

        return ViewConstants.ACCESSIBILITY.asView();
    }

    @Override
    public String serviceUnavailable(Model model, ServletRequest servletRequest,
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