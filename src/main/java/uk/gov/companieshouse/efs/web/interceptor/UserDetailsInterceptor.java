package uk.gov.companieshouse.efs.web.interceptor;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.efs.web.service.session.SessionService;

/**
 * Adds the user profile information to the {@link ModelAndView}.
 */
@Component
public class UserDetailsInterceptor implements HandlerInterceptor {

    private static final String USER_EMAIL = "userEmail";

    private static final String SIGN_IN_KEY = "signin_info";
    private static final String USER_PROFILE_KEY = "user_profile";
    private static final String EMAIL_KEY = "email";

    private SessionService sessionService;

    /**
     * Constructor for the UserDetailsInterceptor.
     *
     * @param sessionService handles session data
     */
    @Autowired
    public UserDetailsInterceptor(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        @Nullable ModelAndView modelAndView) {

        if (modelAndView != null &&
                modelAndView.getViewName() != null &&
                ("GET".equalsIgnoreCase(request.getMethod()) ||
                        ("POST".equalsIgnoreCase(request.getMethod()) &&
                                !StringUtils.startsWith(modelAndView.getViewName(),
                                        UrlBasedViewResolver.REDIRECT_URL_PREFIX)))) {

            Map<String, Object> sessionData = sessionService.getSessionDataFromContext();
            Map<String, Object> signInInfo = (Map<String, Object>) sessionData.get(SIGN_IN_KEY);

            if (signInInfo != null) {
                Map<String, Object> userProfile = (Map<String, Object>) signInInfo.get(USER_PROFILE_KEY);
                modelAndView.addObject(USER_EMAIL, userProfile.get(EMAIL_KEY));
            }
        }
    }
}
