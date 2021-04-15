package uk.gov.companieshouse.efs.web.categorytemplates.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.efs.categorytemplates.CategoryTemplateApi;

@ExtendWith(MockitoExtension.class)
class NotBlankCategoryTemplateValidatorTest {

    private NotBlankCategoryTemplateValidator testValidator;

    @Mock
    NotBlankCategoryTemplate constraint;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        testValidator = new NotBlankCategoryTemplateValidator();
        testValidator.initialize(constraint);
    }

    @Test
    void isValidWhenCategoryTemplateNull() {

        boolean valid = testValidator.isValid(null, context);
        assertThat(valid, is(true));
    }

    @Test
    void isValidWhenValuesNull() {

        boolean valid = testValidator.isValid(new CategoryTemplateApi(), context);
        assertThat(valid, is(false));
    }

    @Test
    void isNotValidWhenValuesBlank() {

        boolean valid = testValidator.isValid(new CategoryTemplateApi("", "", "", "", ""), context);
        assertThat(valid, is(false));
    }

    @Test
    void isValidWhenValuesNotBlank() {

        boolean valid = testValidator.isValid(
                new CategoryTemplateApi("CC01", "FILE", "Test01", "CC02", "CC01"), context);
        assertThat(valid, is(true));
    }


}