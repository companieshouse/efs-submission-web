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
@Configuration
public class WebApplicationSecurity {

    private final String signoutRedirectPath;
    private final String startPageUrl;
    private final String accessibilityStatementPageUrl;
    private final String guidancePageUrl;
    private final String insolvencyGuidancePageUrl;
    private final String serviceUnavailablePageUrl;
    private ApiClientService apiClientService;
    private FormTemplateService formTemplateService;
    private CategoryTemplateService categoryTemplateService;
    private EnvironmentReader environmentReader;

    /**
     * Constructor.
     *
     * @param apiClientService        apiClient service
     * @param formTemplateService     formTemplate service
     * @param categoryTemplateService categoryTemplate service
     */
    @Autowired
    public WebApplicationSecurity(
            final ApiClientService apiClientService, FormTemplateService formTemplateService,
            final CategoryTemplateService categoryTemplateService, final EnvironmentReader environmentReader,
            @Value("${chs.signout.redirect.path}") String signoutRedirectPath,
            @Value("${start.page.url}") String startPageUrl,
            @Value("${accessibility.statement.page.url}") String accessibilityStatementPageUrl,
            @Value("${guidance.page.url}") String guidancePageUrl,
            @Value("${insolvency.guidance.page.url}") String insolvencyGuidancePageUrl,
            @Value("${service.unavailable.page.url}") String serviceUnavailablePageUrl) {
        this.signoutRedirectPath = signoutRedirectPath;
        this.startPageUrl = startPageUrl;
        this.accessibilityStatementPageUrl = accessibilityStatementPageUrl;
        this.guidancePageUrl = guidancePageUrl;
        this.insolvencyGuidancePageUrl = insolvencyGuidancePageUrl;
        this.serviceUnavailablePageUrl = serviceUnavailablePageUrl;
        this.apiClientService = apiClientService;
        this.formTemplateService = formTemplateService;
        this.categoryTemplateService = categoryTemplateService;
        this.environmentReader = environmentReader;
    }

    @Order(1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/efs-submission",
                "/efs-submission-web/**",
                signoutRedirectPath,
                startPageUrl,
                accessibilityStatementPageUrl,
                guidancePageUrl,
                insolvencyGuidancePageUrl,
                serviceUnavailablePageUrl)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll())
                .build();
    }

    @Order(2)
    @Bean
    public SecurityFilterChain withoutCompanyAuthFilterChain(HttpSecurity http) throws Exception {

        return http.securityMatcher("/efs-submission/*/company/*/details",
                                "/efs-submission/*/company/*/category-selection",
                                "/efs-submission/*/company/*/document-selection")
                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class).build();
    }
    
    @Order(3)
    @Bean
    public SecurityFilterChain companyAuthFilterChain(HttpSecurity http) throws Exception {

        final CompanyAuthFilter companyAuthFilter =
                new CompanyAuthFilter(environmentReader, apiClientService, formTemplateService,
                        categoryTemplateService);
        return http.securityMatcher("/efs-submission/*/company/**")
                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(companyAuthFilter, BasicAuthenticationFilter.class).build();
    }

    /**
     * static nested class for resource level security.
     */
    @Order(4)
    @Bean
    public SecurityFilterChain efsWebResourceFilterChain(HttpSecurity http) throws Exception {

        return http.securityMatcher("/efs-submission/**")
                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class)
                .addFilterBefore(new HijackFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new UserAuthFilter(), BasicAuthenticationFilter.class).build();
    }
}