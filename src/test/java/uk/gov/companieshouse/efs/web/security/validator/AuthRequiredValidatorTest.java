package uk.gov.companieshouse.efs.web.security.validator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthRequiredValidatorTest {
    private TestAuthRequiredValidator testValidator;

    @Mock
    private Validator<HttpServletRequest> nextValidator;

    @Mock
    HttpServletRequest request;

    @Mock
    ValidatorResourceProvider provider;

    @Test
    void validateWhenHasNextValidator() {
        testValidator = new TestAuthRequiredValidator(true);
        testValidator.setNext(nextValidator);
        testValidator.validate(request);

        verify(nextValidator).validate(request);
    }

    @Test
    void dontCallNextValidatorWhenValidationFails() {
        testValidator = new TestAuthRequiredValidator(false);
        testValidator.setNext(nextValidator);
        testValidator.validate(request);

        verify(nextValidator, never()).validate(request);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void validateWhenNoNextValidator(boolean isValid) {
        testValidator = new TestAuthRequiredValidator(isValid);

        assertEquals(isValid, testValidator.validate(request));
    }

    @Test
    void setNextAppendsToLast() {
        Validator<HttpServletRequest> validator1 = spy(new TestAuthRequiredValidator(true));
        Validator<HttpServletRequest> validator2 = spy(new TestAuthRequiredValidator(true));

        testValidator = new TestAuthRequiredValidator(true);
        Validator<HttpServletRequest> tv = testValidator
                .setNext(validator1)
                .setNext(validator2);


        assertThat(tv, sameInstance(testValidator));

        tv.validate(request);

        verify(validator1).validate(request);
        verify(validator1).setNext(validator2);
        verify(validator2).validate(request);
    }

    @Test
    void inputNotSetWhenResourceProverAlreadyHasIt() {
        testValidator = new TestAuthRequiredValidator(true);
        when(provider.getInput()).thenReturn(request);

        testValidator.validate(request);

        verify(provider, never()).setInput(request);
    }

    @Test
    void inputNotSetWhenResourceProviderNull() {
        testValidator = new TestAuthRequiredValidator(true);
        testValidator.resourceProvider = null;

        testValidator.validate(request);
        verify(provider, never()).setInput(request);
    }


    class TestAuthRequiredValidator extends AuthRequiredValidator {
        private final boolean returns;


        public TestAuthRequiredValidator(boolean returns) {
            super(provider);
            this.returns = returns;
        }

        @Override
        public boolean requiresAuth() {
            return returns;
        }
    }
}