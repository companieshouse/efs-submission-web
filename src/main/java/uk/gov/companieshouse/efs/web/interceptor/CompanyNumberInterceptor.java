package uk.gov.companieshouse.efs.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.efs.web.exception.CompanyNumberMismatchException;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;

@Component
public class CompanyNumberInterceptor implements HandlerInterceptor {

    private final ApiClientService apiClientService;

    @Value("${company.details.page.url}")
    private String companyDetailsPageUrl;

    @Autowired
    public CompanyNumberInterceptor(final ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
        @NonNull final Object handler) {
        final String requestURI = request.getRequestURI();
        final UriTemplate template = new UriTemplate(companyDetailsPageUrl);

        Optional.of(template.match(requestURI))
            .filter(pathVariables -> pathVariables.containsKey("id") && pathVariables.containsKey("companyNumber"))
            .ifPresent(pathVariables -> {
                final String id = pathVariables.get("id");
                final String companyNumber = pathVariables.get("companyNumber");
                final SubmissionApi submissionApi = Objects.requireNonNull(apiClientService.getSubmission(id).getData());
                final boolean numberMatchesSubmission = Optional.ofNullable(submissionApi.getCompany())
                    .map(company -> Objects.equals(company.getCompanyNumber(), companyNumber))
                    .orElse(false);

                if (!numberMatchesSubmission) {
                    throw new CompanyNumberMismatchException();
                }
            });

        return true;
    }
}
