package uk.gov.companieshouse.efs.web.formtemplates.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;

@ExtendWith(MockitoExtension.class)
class NotBlankFormTemplateValidatorTest {

    private NotBlankFormTemplateValidator testValidator;

    @Mock
    NotBlankFormTemplate constraint;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        testValidator = new NotBlankFormTemplateValidator();
        testValidator.initialize(constraint);
    }

    @Test
    void isValidWhenFormTemplateNull() {

        boolean valid = testValidator.isValid(null, context);
        assertThat(valid, is(true));
    }

    @Test
    void isValidWhenValuesNull() {

        boolean valid = testValidator.isValid(new FormTemplateApi(), context);
        assertThat(valid, is(false));
    }

    @Test
    void isNotValidWhenValuesBlank() {

        boolean valid = testValidator.isValid(
            new FormTemplateApi("", "", "", "", false, false, null, false, null), context);
        assertThat(valid, is(false));
    }

    @Test
    void isValidWhenValuesNotBlank() {

        boolean valid = testValidator.isValid(
            new FormTemplateApi("CC01", "Test01", "CC02", "CC01", true, true, null, false, null),
            context);
        assertThat(valid, is(true));
    }


}