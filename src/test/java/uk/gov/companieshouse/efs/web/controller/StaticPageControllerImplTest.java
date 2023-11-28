package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.maintenance.MaintenanceCheckApi;
import uk.gov.companieshouse.api.model.efs.maintenance.ServiceStatus;


@ExtendWith(MockitoExtension.class)
class StaticPageControllerImplTest extends BaseControllerImplTest {

    protected static final String COMPANY_SEARCH_REDIRECT =
        UrlBasedViewResolver.REDIRECT_URL_PREFIX + CHS_URL
            + "/company-lookup/search";
    private StaticPageController testController;

    @Mock
    private RedirectAttributes attributes;

    @Mock
    private ApiResponse<MaintenanceCheckApi> maintenanceCheckApiApiResponse;
    @Mock
    private MaintenanceCheckApi maintenanceCheckApi;

    @BeforeEach
    protected void setUp() {
        setUpHeaders();
        testController = new StaticPageControllerImpl(logger, apiClientService);
        ((StaticPageControllerImpl) testController).setChsUrl(CHS_URL);
        ReflectionTestUtils.setField(testController, "registrationsEnabled", false);
    }

    @Test
    void startPageWhenApiServiceUpAndNoMaintenanceOngoing() {
        when(apiClientService.getMaintenanceCheck()).thenReturn(maintenanceCheckApiApiResponse);
        when(maintenanceCheckApiApiResponse.getStatusCode()).thenReturn(HttpStatus.OK.value());
        when(maintenanceCheckApiApiResponse.getData()).thenReturn(maintenanceCheckApi);
        when(maintenanceCheckApi.getStatus()).thenReturn(ServiceStatus.UP);
        assertThat(testController.start(categoryTemplateAttribute, model, attributes, servletRequest, sessionStatus),
                is(ViewConstants.START.asView()));
    }

    @Test
    void startPageWhenMaintenanceOngoing() {
        when(apiClientService.getMaintenanceCheck()).thenReturn(maintenanceCheckApiApiResponse);
        when(maintenanceCheckApiApiResponse.getStatusCode()).thenReturn(HttpStatus.OK.value());
        when(maintenanceCheckApiApiResponse.getData()).thenReturn(maintenanceCheckApi);
        when(maintenanceCheckApi.getStatus()).thenReturn(ServiceStatus.OUT_OF_SERVICE);
        when(maintenanceCheckApi.getMaintenanceEnd()).thenReturn("2023-11-14T17:04:00Z");
        assertThat(testController.start(categoryTemplateAttribute, model, attributes, servletRequest, sessionStatus),
                is(ViewConstants.UNAVAILABLE.asRedirectUri(CHS_URL)));
    }

    @Test
    void startPageWhenMaintenanceCheckFailsWithException() {
        when(apiClientService.getMaintenanceCheck()).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found"));
        assertThat(testController.start(categoryTemplateAttribute, model, attributes, servletRequest, sessionStatus),
                is(ViewConstants.START.asView()));
    }

    @Test
    void startPageWhenMaintenanceCheckFailsWithStatusCode() {
        when(apiClientService.getMaintenanceCheck()).thenReturn(maintenanceCheckApiApiResponse);
        when(maintenanceCheckApiApiResponse.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
        assertThat(testController.start(categoryTemplateAttribute, model, attributes, servletRequest, sessionStatus),
            is(ViewConstants.START.asView()));
    }

    @Test
    void unavailablePageWhenDirectedFromStart() {
        assertThat(testController.serviceUnavailable(model, servletRequest, "dummyDate"), is(ViewConstants.UNAVAILABLE.asView()));
    }

    @Test
    void unavailablePageWhenRefreshed() {
        assertThat(testController.serviceUnavailable(model, servletRequest, null), is(ViewConstants.START.asRedirectUri(CHS_URL)));
    }

    @Test
    void guidancePage() {
        assertThat(testController.guidance(model, servletRequest), is(ViewConstants.GUIDANCE.asView()));
    }

    @Test
    void insolvencyGuidancePage() {
        assertThat(testController.insolvencyGuidance(model, servletRequest), is(ViewConstants.INSOLVENCY_GUIDANCE.asView()));
    }

    @Test
    void accessibilityStatementPage() {
        assertThat(testController.accessibilityStatement(model, servletRequest),
            is(ViewConstants.ACCESSIBILITY.asView()));
    }

    @Test
    void companyLookupWhenFeatureDisabled() {
        final String view = testController.companyLookup(SUBMISSION_ID, model, servletRequest, attributes);

        verify(attributes)
            .addAttribute("forward", "/efs-submission/" + SUBMISSION_ID + "/company/{companyNumber}/details");
        assertThat(view, is(COMPANY_SEARCH_REDIRECT));
    }
    
    @Test
    void companyLookupWhenFeatureEnabled() {
        ReflectionTestUtils.setField(testController, "registrationsEnabled", true);

        final String view = testController.companyLookup(SUBMISSION_ID, model, servletRequest, attributes);

        verify(attributes)
            .addAttribute("forward", "/efs-submission/" + SUBMISSION_ID + "/company/{companyNumber}/details");
        assertThat(view, is(COMPANY_SEARCH_REDIRECT + "?noCompanyOption=1"));
    }

}
