package uk.gov.companieshouse.efs.web.model;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateListApi;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;
import uk.gov.companieshouse.efs.web.formtemplates.validator.NotBlankFormTemplate;

import java.util.List;
import java.util.Objects;

/**
 * Model class representing on-screen fields. Delegates to {@link FormTemplateApi} for details.
 */
@Component
@Qualifier("sh19Template")
@SessionScope
public class Sh19TemplateModel extends FormTemplateModel {
    
    public Sh19TemplateModel() {
        this(new FormTemplateApi());
    }

    /**
     * Constructor that sets the {@link FormTemplateApi} on the {@link Sh19TemplateModel}.
     *
     * @param details the {@link FormTemplateApi}
     */
    public Sh19TemplateModel(FormTemplateApi details) {
        setDetails(details);
        setFormTemplateList(new FormTemplateListApi());
    }
}
