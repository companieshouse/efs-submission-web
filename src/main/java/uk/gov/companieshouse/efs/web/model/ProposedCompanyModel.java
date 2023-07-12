package uk.gov.companieshouse.efs.web.model;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import uk.gov.companieshouse.api.model.efs.submissions.CompanyApi;

@Component
@SessionScope
public class ProposedCompanyModel {

    private String submissionId;
    private Boolean nameRequired;
    private CompanyApi details;

    @SuppressWarnings("squid:S2637") // nameRequired initially null by design
    public ProposedCompanyModel() {
        this.details = new CompanyApi();
    }

    @SuppressWarnings("squid:S2637") // nameRequired initially null by design
    public ProposedCompanyModel(final CompanyApi details) {
        this.details = details;
    }

    public ProposedCompanyModel(final CompanyApi details, final Boolean nameRequired) {
        this.details = details;
        this.nameRequired = nameRequired;
    }

    public ProposedCompanyModel(final ProposedCompanyModel other) {
        this.submissionId = other.getSubmissionId();
        this.details = other.getDetails();
        this.nameRequired = other.getNameRequired();
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }

    public CompanyApi getDetails() {
        return details;
    }

    public void setDetails(final CompanyApi details) {
        this.details = details;
    }

    public String getName() {
       return details.getCompanyName();
    }

    public void setName(final String name) {
        details.setCompanyName(name);
    }

    public String getNumber() {
        return details.getCompanyNumber();
    }
    
    public void setNumber(final String number) {
        details.setCompanyNumber(number);
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
            getNameRequired(), that.getNameRequired()) && Objects.equals(getDetails(),
            that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubmissionId(), getNameRequired(), getDetails());
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
