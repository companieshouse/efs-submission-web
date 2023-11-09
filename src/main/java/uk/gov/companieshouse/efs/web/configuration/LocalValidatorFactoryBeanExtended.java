package uk.gov.companieshouse.efs.web.configuration;

import javax.annotation.Nonnull;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.MessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

/**
 * This class is an interim workaround for calling setValidationMessageSource() see
 * <a href="https://github.com/spring-projects/spring-framework/issues/15099">...</a>
 * <p>
 * In versions of hibernate 4.3.x and above,
 * org.hibernate.validator.resourceloading.ResourceBundleLocator is deprecated and replaced by
 * org.hibernate.validator.spi.resourceloading.ResourceBundleLocator. However, this now causes an
 * exception to be thrown when calling setValidationMessageSource() on a LocalValidatorFactoryBean
 * instance until spring 4.0 is stable/in use.
 */

public class LocalValidatorFactoryBeanExtended
        extends org.springframework.validation.beanvalidation.LocalValidatorFactoryBean {

    private static class HibernateValidatorDelegate {

        public static MessageInterpolator buildMessageInterpolator(MessageSource messageSource) {
            return new ResourceBundleMessageInterpolator(
                    new MessageSourceResourceBundleLocator(messageSource));
        }
    }

    @Override
    public void setValidationMessageSource(@Nonnull MessageSource messageSource) {
        setMessageInterpolator(HibernateValidatorDelegate.buildMessageInterpolator(messageSource));
    }

}
