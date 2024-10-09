package uk.gov.companieshouse.efs.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.companieshouse.efs.web.controller.CompanyDetailControllerImpl;
import uk.gov.companieshouse.efs.web.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.efs.web.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.company.CompanyService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.logging.Logger;


@TestPropertySource(value = "/application-test.properties")
@WebMvcTest(value = CompanyDetailControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class InterceptorTest {

    @MockBean
    private UserDetailsInterceptor userDetailsInterceptor;
    @MockBean
    private LoggingInterceptor loggingInterceptor;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private SessionService sessionService;
    @MockBean
    private ApiClientService apiClientService;
    @MockBean
    private Logger logger;
    @MockBean
    private CompanyDetail companyDetail;
    @MockBean
    private SecureRandom secureRandom;

    @Test
    void testInterceptorsArePresent() throws Exception {
        MvcResult result = mockMvc.perform(get("/new-submission")).andDo(print()).andReturn();
        assertNotNull(Arrays.stream(Objects.requireNonNull(result.getInterceptors()))
                .filter(handlerInterceptor -> handlerInterceptor instanceof LoggingInterceptor)
                .findFirst());
        assertNotNull(Arrays.stream(Objects.requireNonNull(result.getInterceptors()))
                .filter(handlerInterceptor -> handlerInterceptor instanceof UserDetailsInterceptor)
                .findFirst());
    }
}
