package uk.gov.companieshouse.efs.web.model;

import java.util.Objects;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
//@SessionScope
public class ReviewSelectionModel {

    private String submissionId;

    private String companyNumber;

    private String confirmed;

    /**
     * No argument Constructor.
     */
    public ReviewSelectionModel() {
        this.confirmed = "";
    }

    /**
     * Constructor sets the application id and required value from the {@link ReviewSelectionModel}.
     *
     * @param original the {@link ReviewSelectionModel}
     */
    public ReviewSelectionModel(final ReviewSelectionModel original) {
        this.submissionId = original.getSubmissionId();
        this.confirmed = original.getConfirmed();
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(final String companyNumber) {
        this.companyNumber = companyNumber;
    }

    @NotBlank(message = "{reviewSelect.confirm.error}")
    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final ReviewSelectionModel that = (ReviewSelectionModel) o;
        return Objects.equals(confirmed, that.confirmed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confirmed);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("confirmed", confirmed).toString();
    }
}
