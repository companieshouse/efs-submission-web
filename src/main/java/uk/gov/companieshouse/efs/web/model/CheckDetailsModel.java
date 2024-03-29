package uk.gov.companieshouse.efs.web.model;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import uk.gov.companieshouse.api.model.efs.submissions.FileDetailApi;

@Component
@SessionScope
public class CheckDetailsModel {

    private String submissionId;
    private String companyName;
    private String companyNumber;
    private String documentTypeDescription;
    private List<FileDetailApi> documentUploadedList;
    private String paymentCharge;
    private Boolean confirmAuthorised;
    private String formType;

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getDocumentTypeDescription() {
        return documentTypeDescription;
    }

    public void setDocumentTypeDescription(final String documentTypeDescription) {
        this.documentTypeDescription = documentTypeDescription;
    }

    public List<FileDetailApi> getDocumentUploadedList() {
        return documentUploadedList;
    }

    public void setDocumentUploadedList(final List<FileDetailApi> documentUploadedList) {
        this.documentUploadedList = documentUploadedList;
    }

    public String getPaymentCharge() {
        return paymentCharge;
    }

    public void setPaymentCharge(final String paymentCharge) {
        this.paymentCharge = paymentCharge;
    }

    public Boolean getConfirmAuthorised() {
        return confirmAuthorised;
    }

    public void setConfirmAuthorised(final Boolean confirmAuthorised) {
        this.confirmAuthorised = confirmAuthorised;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CheckDetailsModel that = (CheckDetailsModel) o;
        return Objects.equals(getSubmissionId(), that.getSubmissionId()) && Objects
            .equals(getCompanyName(), that.getCompanyName()) && Objects
            .equals(getCompanyNumber(), that.getCompanyNumber()) && Objects
            .equals(getDocumentTypeDescription(), that.getDocumentTypeDescription()) && Objects
            .equals(getDocumentUploadedList(), that.getDocumentUploadedList()) && Objects
            .equals(getPaymentCharge(), that.getPaymentCharge()) && Objects
            .equals(getConfirmAuthorised(), that.getConfirmAuthorised()) && Objects
                .equals(getFormType(), that.getFormType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubmissionId(), getCompanyName(), getCompanyNumber(),
                getDocumentTypeDescription(), getDocumentUploadedList(), getPaymentCharge(),
                getConfirmAuthorised(), getFormType());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("submissionId", submissionId)
                .append("companyName", companyName)
                .append("companyNumber", companyNumber)
                .append("documentTypeDescription", documentTypeDescription)
                .append("documentUploadedList", documentUploadedList)
                .append("paymentCharge", paymentCharge)
                .append("confirmAuthorised", confirmAuthorised)
                .append("formType", getFormType()).toString();
    }
}
