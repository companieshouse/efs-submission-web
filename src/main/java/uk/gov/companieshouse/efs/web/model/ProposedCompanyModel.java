package uk.gov.companieshouse.efs.web.model;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class ProposedCompanyModel {

    private String submissionId;
    private String name;
    @NotNull(message = "{proposedCompany.blank.error}")
    private Boolean nameRequired;

    public ProposedCompanyModel() {
        this.name = "";
    }

    /**
     * Constructor sets the required value from the {@link ProposedCompanyModel}
     *
     * @param original the {@link ProposedCompanyModel}
     */
    public ProposedCompanyModel(final ProposedCompanyModel original) {
        this.submissionId = original.getSubmissionId();
        this.name = original.getName();
        this.nameRequired = original.getNameRequired();
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }

    @NotBlank(message = "{proposedCompany.error}")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getNameRequired() {
        return nameRequired;
    }

    public void setNameRequired(final Boolean nameRequired) {
        this.nameRequired = nameRequired;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProposedCompanyModel that = (ProposedCompanyModel) o;
        return Objects.equals(getSubmissionId(), that.getSubmissionId()) && Objects.equals(
            getName(), that.getName()) && Objects.equals(getNameRequired(),
            that.getNameRequired());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubmissionId(), getName(), getNameRequired());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
