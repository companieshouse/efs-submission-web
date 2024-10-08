package uk.gov.companieshouse.efs.web.interceptor;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.efs.web.service.session.SessionService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserDetailsInterceptorTest {

    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String EMAIL_KEY = "email";
    private static final String MODEL_EMAIL_KEY = "userEmail";
    private static final String USER_EMAIL = "email";
    private static final String REDIRECT_URL = "redirect:test";
    private static final String NON_REDIRECT_URL = "test";

    @Mock
    private SessionService sessionService;

    @Mock
    private ModelAndView modelAndView;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private UserDetailsInterceptor interceptor;
    private Map<String, Object> sessionData;

    @BeforeEach
    public void setUp() {
        sessionData = new HashMap<>();
        when(sessionService.getSessionDataFromContext()).thenReturn(sessionData);

        Map<String, Object> signInInfo = new HashMap<>();
        sessionData.put(SIGN_IN_KEY, signInInfo);

        Map<String, Object> userProfile = new HashMap<>();
        signInInfo.put(USER_PROFILE_KEY, userProfile);

        userProfile.put(EMAIL_KEY, USER_EMAIL);
    }

    @Test
    @DisplayName("Verify the email is not added to the session when a user is not logged in for a HTTP GET")
    void testEmailNotAddedToSessionWhenNotSignedInForGet() {
        sessionData.put(SIGN_IN_KEY, null);
        when(request.getMethod()).thenReturn("GET");
        when(modelAndView.getViewName()).thenReturn(NON_REDIRECT_URL);

        interceptor.postHandle(request, response, handler, modelAndView);
        verify(modelAndView).getViewName();
        verifyNoMoreInteractions(modelAndView);
    }

    @ParameterizedTest(name = "HTTP {0}: Verify the email is added to the session when a user is logged in")
    @ValueSource(strings = {"GET","POST"})
    void testEmailAddedToSessionForGet(String httpMethod) throws Exception {
        when(request.getMethod()).thenReturn(httpMethod);
        when(modelAndView.getViewName()).thenReturn(NON_REDIRECT_URL);

        interceptor.postHandle(request, response, handler, modelAndView);
        verify(modelAndView).addObject(MODEL_EMAIL_KEY, USER_EMAIL);
    }

    @ParameterizedTest(name = "HTTP {0}: Verify the email is not added to the session when no details are provided")
    @ValueSource(strings = {"GET","POST","PUT"})
    void testEmailNotAddedToSessionWithEmptyModelForGet(String httpMethod) {
        when(request.getMethod()).thenReturn(httpMethod);
        when(modelAndView.getViewName()).thenReturn(NON_REDIRECT_URL);

        interceptor.postHandle(request, response, handler, null);
        verify(modelAndView, never()).addObject(MODEL_EMAIL_KEY, USER_EMAIL);
    }

    @Test
    @DisplayName("Verify the email is not added when a user is logged in for a redirect HTTP POST")
    void testEmailNotAddedToSessionForRedirectPost() {
        when(request.getMethod()).thenReturn("POST");
        when(modelAndView.getViewName()).thenReturn(REDIRECT_URL);

        interceptor.postHandle(request, response, handler, modelAndView);
        verify(modelAndView, never()).addObject(MODEL_EMAIL_KEY, USER_EMAIL);
    }

    @ParameterizedTest
    @ValueSource(strings = {NON_REDIRECT_URL,REDIRECT_URL})
    @DisplayName("Verify the email is not added to the session for a HTTP PUT")
    void testEmailNotAddedToSessionForPut(String url) {
        when(request.getMethod()).thenReturn("PUT");
        when(modelAndView.getViewName()).thenReturn(url);

        interceptor.postHandle(request, response, handler, modelAndView);
        verify(modelAndView, never()).addObject(MODEL_EMAIL_KEY, USER_EMAIL);
    }

    @ParameterizedTest(name = "HTTP {0}: Verify the email is not added to the session when no details are provided")
    @ValueSource(strings = {"POST","PUT"})
    void testViewNameNotNull(String httpMethod) {
        when(request.getMethod()).thenReturn(httpMethod);
        when(modelAndView.getViewName()).thenReturn(null);

        interceptor.postHandle(request, response, handler, modelAndView);
        verify(modelAndView, never()).addObject(MODEL_EMAIL_KEY, USER_EMAIL);
    }

}
