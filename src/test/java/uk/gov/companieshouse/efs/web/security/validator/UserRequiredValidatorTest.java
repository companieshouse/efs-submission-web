package uk.gov.companieshouse.efs.web.security.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTypeConstants;
import uk.gov.companieshouse.efs.web.categorytemplates.service.api.CategoryTemplateService;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRequiredValidatorTest {
    public static final String FORM_CATEGORY = "FORM_CATEGORY";
    public static final String EMAIL = "demo@ch.gov.uk";
    @Mock
    ValidatorResourceProvider resourceProvider;

    static final String COMPANY_NUMBER = "00006400";

    @Mock
    SignInInfo signInInfo;

    @Mock
    UserProfile userProfile;

    @Mock
    CategoryTemplateService categoryTemplateService;

    UserRequiredValidator testUserValidator;

    @Mock
    FormTemplateApi form;

    @Mock
    ApiClientService apiClientService;

    @Mock
    private ApiResponse<Boolean> apiResp;

    @BeforeEach
    void setUp() {
        testUserValidator = new UserRequiredValidator(resourceProvider, categoryTemplateService);
    }

    @Test
    void isNotOnAllowListWhenNotInsolvency() {
        expectTopLevelCategory(CategoryTypeConstants.ARTICLES);

        assertTrue(testUserValidator.isValid());

        // Fails before checking if on allow list
        verify(resourceProvider, never()).getApiClientService();
    }

    @Test
    void notValidWhenNotOnAllowList() {
        expectTopLevelCategory(CategoryTypeConstants.INSOLVENCY);
        expectEmail();

        when(resourceProvider.getApiClientService()).thenReturn(apiClientService);

        assertTrue(testUserValidator.requiresAuth());

        verify(apiClientService).isOnAllowList(EMAIL);
    }

    @Test
    void validWhenOnAllowList() {
        expectTopLevelCategory(CategoryTypeConstants.INSOLVENCY);
        expectEmail();

        when(resourceProvider.getApiClientService()).thenReturn(apiClientService);
        when(apiClientService.isOnAllowList(EMAIL)).thenReturn(apiResp);
        when(apiResp.getData()).thenReturn(true);

        assertFalse(testUserValidator.requiresAuth());
    }

    @Test
    void notValidWhenNoSignInInfo() {
        expectTopLevelCategory(CategoryTypeConstants.INSOLVENCY);

        when(resourceProvider.getApiClientService()).thenReturn(apiClientService);
        when(resourceProvider.getSignInInfo()).thenReturn(Optional.empty());

        testUserValidator.isValid();

        verify(apiClientService, never()).isOnAllowList(anyString());
    }

    @Test
    void notAuthorisedWhenNoCompanyNumber() {
        when(resourceProvider.getCompanyNumber()).thenReturn(Optional.empty());

        assertTrue(testUserValidator.isValid());

        verify(resourceProvider, never()).getSignInInfo();
    }

    @Test
    void notAuthorisedWhenInvalidScope() {
        expectScopes(Collections.singletonList("INVALID_SCOPE"));
        when(resourceProvider.getCompanyNumber()).thenReturn(Optional.of(COMPANY_NUMBER));

        boolean requiresAuth = testUserValidator.requiresAuth();

        assertTrue(requiresAuth);
        verify(resourceProvider, times(2)).getSignInInfo();
    }

    @ParameterizedTest(name = "Fine grained scopes={0}")
    @ValueSource(booleans = { false, true })
    void authorisedWhenScopeValid(final boolean fineGrainedScopes) {
        final String scope = "/company/" + COMPANY_NUMBER + (fineGrainedScopes ? "/admin.write-full" : "");

        if (fineGrainedScopes) {
            ReflectionTestUtils.setField(testUserValidator, "useFineGrainedScope", "1");
        }
        expectScopes(Collections.singletonList(scope));
        when(resourceProvider.getCompanyNumber()).thenReturn(Optional.of(COMPANY_NUMBER));

        boolean requiresAuth = testUserValidator.requiresAuth();

        assertFalse(requiresAuth);
        verify(resourceProvider, times(2)).getSignInInfo();
    }

    @Test
    void authorisedWhenPreviouslyAuthorised() {
        when(resourceProvider.getSignInInfo()).thenReturn(Optional.of(signInInfo));
        when(resourceProvider.getCompanyNumber()).thenReturn(Optional.of(COMPANY_NUMBER));
        when(signInInfo.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        boolean requiresAuth = testUserValidator.requiresAuth();

        assertFalse(requiresAuth);
        verify(resourceProvider).getSignInInfo();
    }

    @Test
    void authorisedWhenTwoScopes() {
        List<String> scopes = Arrays.asList(
                "/company/" + COMPANY_NUMBER + "/admin.write-full",
                "https://account.companieshouse.gov.uk/user.write-full");

        ReflectionTestUtils.setField(testUserValidator, "useFineGrainedScope", "1");
        expectScopes(scopes);
        when(resourceProvider.getCompanyNumber()).thenReturn(Optional.of(COMPANY_NUMBER));

        boolean requiresAuth = testUserValidator.requiresAuth();

        assertFalse(requiresAuth);
        verify(resourceProvider, times(2)).getSignInInfo();
    }

    private void expectUserProfile() {
        when(resourceProvider.getSignInInfo()).thenReturn(Optional.of(signInInfo));
        when(signInInfo.getUserProfile()).thenReturn(userProfile);
    }

    void expectScopes(List<String> scopes) {
        expectUserProfile();
        when(userProfile.getScope()).thenReturn(String.join(" ", scopes));
    }

    void expectEmail() {
        expectUserProfile();
        when(userProfile.getEmail()).thenReturn(UserRequiredValidatorTest.EMAIL);
    }

    void expectTopLevelCategory(CategoryTypeConstants tlc) {
        when(resourceProvider.getForm()).thenReturn(Optional.of(form));
        when(form.getFormCategory()).thenReturn(FORM_CATEGORY);
        when(categoryTemplateService.getTopLevelCategory(FORM_CATEGORY)).thenReturn(tlc);
    }


}
