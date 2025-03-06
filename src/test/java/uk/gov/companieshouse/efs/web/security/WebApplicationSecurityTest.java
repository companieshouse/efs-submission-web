package uk.gov.companieshouse.efs.web.security;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.github.stefanbirkner.systemlambda.Statement;
import uk.gov.companieshouse.auth.filter.HijackFilter;
import uk.gov.companieshouse.auth.filter.UserAuthFilter;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.session.handler.SessionHandler;


@ExtendWith(MockitoExtension.class)
class WebApplicationSecurityTest {

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private FormTemplateService formTemplateService;

    @Mock
    private CategoryTemplateService categoryTemplateService;

    @Mock
    private EnvironmentReader environmentReader;

    @Mock
    private HttpSecurity httpSecurity;

    @InjectMocks
    private WebApplicationSecurity webApplicationSecurity;

    static final String randomEncryptionKey = "3T3L6iAEFscijkJZnOK0bYu/pH9jZeJqC1j59ZROKu8=";

    @Test
    void securityFilterChainTest() throws Exception {

        HttpSecurity httpSecurityMock = mock(HttpSecurity.class);
        when(httpSecurity.securityMatcher(any(String[].class))).thenReturn(httpSecurityMock);
        when(httpSecurityMock.authorizeHttpRequests(any())).thenReturn(httpSecurityMock);

        webApplicationSecurity.securityFilterChain(httpSecurity);

        verify(httpSecurity).securityMatcher(any(String[].class));
        verify(httpSecurityMock).authorizeHttpRequests(any());
        verify(httpSecurityMock).build();
    }

    @Test
    void withoutCompanyAuthFilterChainTest() throws Exception {

        HttpSecurity httpSecurityMock = mock(HttpSecurity.class);
        when(httpSecurity.securityMatcher(any(String[].class))).thenReturn(httpSecurityMock);
        when(httpSecurityMock.addFilterBefore(any(), any())).thenReturn(httpSecurityMock);

        withLoggingAuthFilterEnvironment(() -> webApplicationSecurity.withoutCompanyAuthFilterChain(httpSecurity));

        verify(httpSecurity).securityMatcher("/efs-submission/*/company/*/details",
                "/efs-submission/*/company/*/category-selection",
                "/efs-submission/*/company/*/document-selection");
        verify(httpSecurityMock).addFilterBefore(any(HijackFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).addFilterBefore(any(UserAuthFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).build();
    }

    @Test
    void companyAuthFilterChainTest() throws Exception {

        HttpSecurity httpSecurityMock = mock(HttpSecurity.class);
        when(httpSecurity.securityMatcher(any(String[].class))).thenReturn(httpSecurityMock);
        when(httpSecurityMock.addFilterBefore(any(), any())).thenReturn(httpSecurityMock);

        withLoggingAuthFilterEnvironment(() -> webApplicationSecurity.companyAuthFilterChain(httpSecurity));

        verify(httpSecurity).securityMatcher("/efs-submission/*/company/**");
        verify(httpSecurityMock).addFilterBefore(any(HijackFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).addFilterBefore(any(UserAuthFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).addFilterBefore(any(CompanyAuthFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).build();
    }

    @Test
    void efsWebResourceFilterChainTest() throws Exception {

        HttpSecurity httpSecurityMock = mock(HttpSecurity.class);
        when(httpSecurity.securityMatcher(any(String[].class))).thenReturn(httpSecurityMock);
        when(httpSecurityMock.addFilterBefore(any(), any())).thenReturn(httpSecurityMock);

        withLoggingAuthFilterEnvironment(() -> webApplicationSecurity.efsWebResourceFilterChain(httpSecurity));

        verify(httpSecurity).securityMatcher("/efs-submission/**");
        verify(httpSecurityMock).addFilterBefore(any(HijackFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).addFilterBefore(any(UserAuthFilter.class), eq(BasicAuthenticationFilter.class
        ));
        verify(httpSecurityMock).build();
    }

    void withLoggingAuthFilterEnvironment(Statement callback) {
        try {
            withEnvironmentVariable("OAUTH2_REQUEST_KEY", randomEncryptionKey)
                    .and("OAUTH2_AUTH_URI", "oauth2_auth_uri")
                    .and("OAUTH2_CLIENT_ID", "oauth_client_id")
                    .and("OAUTH2_REDIRECT_URI", "oauth2_redirect_uri")
                    .and("COOKIE_SECRET", "cookie_secret")
                    .and("USE_FINE_GRAIN_SCOPES_MODEL", "user_fine_grained_scope")
                    .and("COOKIE_NAME", "a")
                    .and("COOKIE_DOMAIN", "a")
                    .and("COOKIE_SECURE_ONLY", "a")
                    .execute(callback);
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating logging auth filter environment", e);
        }
    }
}