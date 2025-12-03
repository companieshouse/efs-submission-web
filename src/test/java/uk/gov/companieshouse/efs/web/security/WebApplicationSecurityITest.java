package uk.gov.companieshouse.efs.web.security;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.stefanbirkner.systemlambda.SystemLambda;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.companieshouse.efs.web.configuration.SpringWebConfig;
import uk.gov.companieshouse.efs.web.controller.StaticPageControllerImpl;
import uk.gov.companieshouse.efs.web.util.IntegrationTestHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Import({WebApplicationSecurity.class, StaticPageControllerImpl.class, SpringWebConfig.class})
@Testcontainers
class WebApplicationSecurityITest {

    public static SystemLambda.WithEnvironmentVariables springEnvironment;

    @Container
    private static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine").withExposedPorts(6379);

    @BeforeAll
    static void setUpSpringEnvironment() {
        springEnvironment = IntegrationTestHelper.withSpringEnvironment()
                .and("LOGGING_LEVEL", "DEBUG")
                .and("ACCESSIBILITY_SUPPORT_URL", "test")
                .and("FILE_TRANSFER_API_KEY", "test")
                .and("FILE_TRANSFER_API_URL", "test")
                .and("CACHE_POOL_SIZE", "8")
                .and("CACHE_SERVER", "localhost:" + redis.getFirstMappedPort());

        ReflectionTestUtils.invokeMethod(springEnvironment, "setEnvironmentVariables");
        redis.start();
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

            mockMvc.perform(get(serviceUnavailablePageUrl))
                    .andExpect(status().isFound());

            mockMvc.perform(get(signoutRedirectPath))
                    .andExpect(status().isOk());
        }
}
