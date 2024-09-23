package uk.gov.companieshouse.efs.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.gov.companieshouse.auth.filter.CompanyAuthFilter;
import uk.gov.companieshouse.auth.filter.HijackFilter;
import uk.gov.companieshouse.auth.filter.UserAuthFilter;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.session.handler.SessionHandler;

/**
 * Customises web security.
 */
@EnableWebSecurity
public class WebApplicationSecurity {
    @Value("${chs.signout.redirect.path}")
    private String signoutRedirectPath;
    private ApiClientService apiClientService;
    private FormTemplateService formTemplateService;
    private CategoryTemplateService categoryTemplateService;
    private EnvironmentReader environmentReader;

    /**
     * Constructor.
     *
     * @param apiClientService              apiClient service
     * @param formTemplateService           formTemplate service
     * @param categoryTemplateService       categoryTemplate service
     */
    @Autowired
    public WebApplicationSecurity(
        final ApiClientService apiClientService, FormTemplateService formTemplateService,
        final CategoryTemplateService categoryTemplateService, final EnvironmentReader environmentReader) {
        this.apiClientService = apiClientService;
        this.formTemplateService = formTemplateService;
        this.categoryTemplateService = categoryTemplateService;
        this.environmentReader = environmentReader;
    }

    /**
     * static nested class for root level security.
     */
    /**
     * static nested class for root level security.
     */
    @Configuration
    @Order(1)
    public static class RootLevelSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http.securityMatcher("/efs-submission").build();
        }
    }

    /**
     * static nested class for start page security.
     */
    @Configuration
    @Order(2)
    public static class StartPageSecurityConfig {
        private final String startPageUrl;

        public StartPageSecurityConfig(@Value("${start.page.url}") final String startPageUrl) {
            this.startPageUrl = startPageUrl;
        }

        @Bean
        public SecurityFilterChain startPageSecurityChain(HttpSecurity http) throws Exception {
            return http.securityMatcher(startPageUrl).build();
        }
    }

    /**
     * static nested class for accessibility statement page security.
     */
    @Configuration
    @Order(3)
    public static class AccessibilityStatementPageSecurityConfig {
        private final String accessibilityStatementPageUrl;

        public AccessibilityStatementPageSecurityConfig(
            @Value("${accessibility.statement.page.url}") final String accessibilityStatementPageUrl) {
            this.accessibilityStatementPageUrl = accessibilityStatementPageUrl;
        }

        @Bean
        public SecurityFilterChain accessibilityStatementPageSecurityChain(HttpSecurity http) throws Exception {
            return http.securityMatcher(accessibilityStatementPageUrl).build();
        }
    }

    /**
     * static nested class for guidance page security.
     */
    @Configuration
    @Order(4)
    public static class GuidancePageSecurityConfig {
        private final String guidancePageUrl;

        public GuidancePageSecurityConfig(@Value("${guidance.page.url}") final String guidancePageUrl) {
            this.guidancePageUrl = guidancePageUrl;
        }

        @Bean
        public SecurityFilterChain guidancePageSecurityChain(HttpSecurity http) throws Exception {
            return http.securityMatcher(guidancePageUrl).build();
        }
    }

    /**
     * static nested class for insolvency guidance page security.
     */
    @Configuration
    @Order(5)
    public static class InsolvencyGuidancePageSecurityConfig {
        private String insolvencyGuidancePageUrl;

        public InsolvencyGuidancePageSecurityConfig(
            @Value("${insolvency.guidance.page.url}") final String insolvencyGuidancePageUrl) {
            this.insolvencyGuidancePageUrl = insolvencyGuidancePageUrl;
        }

        @Bean
        public SecurityFilterChain insolvencyGuidancePageSecurityChain(HttpSecurity http) throws Exception {
            return http.securityMatcher(insolvencyGuidancePageUrl).build();
        }
    }

    /**
     * static nested class for service unavailable page security.
     */
    @Configuration
    @Order(6)
    public static class ServiceUnavailablePageSecurityConfig {
        private String serviceUnavailablePageUrl;

        public ServiceUnavailablePageSecurityConfig(
                @Value("${service.unavailable.page.url}") final String serviceUnavailablePageUrl) {
            this.serviceUnavailablePageUrl = serviceUnavailablePageUrl;
        }

        @Bean
        public SecurityFilterChain serviceUnavailablePageSecurityChain(HttpSecurity http) throws Exception {
            return http.securityMatcher(serviceUnavailablePageUrl).build();
        }
    }

    @Configuration
    @Order(7)
    public class CompanyAuthFilterSecurityConfig {

        @Bean
        public SecurityFilterChain companyAuthFilterChain(HttpSecurity http) throws Exception {
//            final LoggingAuthFilter authFilter = new LoggingAuthFilter(signoutRedirectPath);
//            final CompanyAuthFilter companyAuthFilter =
//                new CompanyAuthFilter();

            return http.securityMatcher("/efs-submission/*/company/**")
                    .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                    .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                    .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class)
                    .addFilterBefore(new CompanyAuthFilter(), BasicAuthenticationFilter.class).build();
        }
//        @Override
//        protected void configure(HttpSecurity http) {
//            final LoggingAuthFilter authFilter = new LoggingAuthFilter(signoutRedirectPath);
//            final CompanyAuthFilter companyAuthFilter =
//                new CompanyAuthFilter(environmentReader, apiClientService, formTemplateService,
//                    categoryTemplateService);
//
//            http.antMatcher("/efs-submission/*/company/**")
//                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
//                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
//                .addFilterBefore(authFilter, BasicAuthenticationFilter.class)
//                .addFilterBefore(companyAuthFilter, BasicAuthenticationFilter.class);
//        }
    }

    /**
     * static nested class for resource level security.
     */
    @Configuration
    @Order(8)
    public class EfsWebResourceFilterConfig {

        @Bean
        public SecurityFilterChain efsWebResourceFilterChain(HttpSecurity http) throws Exception {
//            final LoggingAuthFilter authFilter = new LoggingAuthFilter(signoutRedirectPath);

            return http.securityMatcher("/efs-submission/**")
                    .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                    .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                    .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class).build();
        }

//        @Override
//        protected void configure(final HttpSecurity http) {
//            final LoggingAuthFilter authFilter = new LoggingAuthFilter(signoutRedirectPath);
//
//            http.antMatcher("/efs-submission/**")
//                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
//                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
//                .addFilterBefore(authFilter, BasicAuthenticationFilter.class);
//        }
    }
}
