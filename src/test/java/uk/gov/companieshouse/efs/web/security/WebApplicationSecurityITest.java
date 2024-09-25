package uk.gov.companieshouse.efs.web.security;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.efs.web.controller.StaticPageControllerImpl;
import uk.gov.companieshouse.efs.web.util.IntegrationTestHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Import({WebApplicationSecurity.class, StaticPageControllerImpl.class})
public class WebApplicationSecurityITest {

    private static Map<String, String> storedEnvironment;
    public static SystemLambda.WithEnvironmentVariables springEnvironment;

    @BeforeAll
    static void setUpSpringEnvironment() {
        storedEnvironment = new HashMap<>(System.getenv());
        springEnvironment = IntegrationTestHelper.withSpringEnvironment()
                .and("LOGGING_LEVEL", "DEBUG");

        ReflectionTestUtils.invokeMethod(springEnvironment, "setEnvironmentVariables");
    }


        @Autowired
        protected MockMvc mockMvc;

        @Value("${start.page.url}")
        private String startPageUrl;
        @Value("${accessibility.statement.page.url}")
        private String accessibilityStatementPageUrl;
        @Value("${guidance.page.url}")
        private String guidancePageUrl;
        @Value("${insolvency.guidance.page.url}")
        private String insolvencyGuidancePageUrl;
        @Value("${service.unavailable.page.url}")
        private String serviceUnavailablePageUrl;
        @Value("${chs.signout.redirect.path}")
        private String signoutRedirectPath;

        @Test
        void securityFilterChainTest() throws Exception {


            mockMvc.perform(get(startPageUrl)).andDo(print())
                    .andExpect(status().isOk());

            mockMvc.perform(get(accessibilityStatementPageUrl))
                    .andExpect(status().isOk());

            mockMvc.perform(get(guidancePageUrl))
                    .andExpect(status().isOk());

            mockMvc.perform(get(insolvencyGuidancePageUrl))
                    .andExpect(status().isOk());

        }
}
