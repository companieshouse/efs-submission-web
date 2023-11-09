package uk.gov.companieshouse.efs.web.configuration;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class LocalValidatorFactoryBeanExtendedTest {

    private LocalValidatorFactoryBean testBean;
    private ResourceBundleMessageSource messageSource;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void setUp() {
        testBean = new LocalValidatorFactoryBeanExtended();

        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setFallbackToSystemLocale(true);

        validatorFactory = buildDefaultValidatorFactory();
    }

    @Test
    void localValidatorFactoryBeanExtended() {
        assertThat(testBean, notNullValue());
        assertThat(testBean, instanceOf(LocalValidatorFactoryBean.class));
    }

    @Test
    void setValidationMessageSource() throws Exception {
        final Field field = testBean.getClass().getSuperclass().getDeclaredField("validatorFactory");
        field.setAccessible(true);
        field.set(testBean, validatorFactory);

        testBean.setValidationMessageSource(messageSource);

        assertThat(testBean.getMessageInterpolator(), notNullValue());
        assertThat(testBean.getMessageInterpolator(), instanceOf(ResourceBundleMessageInterpolator.class));

    }
}