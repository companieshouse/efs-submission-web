package uk.gov.companieshouse.efs.web.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.efs.submissions.CompanyApi;

class ProposedCompanyModelTest {

    @Test
    void getNameReturnsNullWhenNameRequiredIsFalse() {
        ProposedCompanyModel model = new ProposedCompanyModel();
        model.setNameRequired(false);

        assertThat(model.getName(), is(nullValue()));
    }

    @Test
    void getNameReturnsCompanyNameWhenNameRequiredIsTrue() {
        CompanyApi details = new CompanyApi();
        details.setCompanyName("Test Company");
        ProposedCompanyModel model = new ProposedCompanyModel(details, true);

        assertThat(model.getName(), is("Test Company"));
    }

    @Test
    void getNumberReturnsCompanyNumber() {
        CompanyApi details = new CompanyApi();
        details.setCompanyNumber("12345678");
        ProposedCompanyModel model = new ProposedCompanyModel(details);

        assertThat(model.getNumber(), is("12345678"));
    }

    @Test
    void setNameUpdatesCompanyName() {
        CompanyApi details = new CompanyApi();
        ProposedCompanyModel model = new ProposedCompanyModel(details);

        model.setName("Updated Company");
        assertThat(details.getCompanyName(), is("Updated Company"));
    }

    @Test
    void setNumberUpdatesCompanyNumber() {
        CompanyApi details = new CompanyApi();
        ProposedCompanyModel model = new ProposedCompanyModel(details);

        model.setNumber("87654321");
        assertThat(details.getCompanyNumber(), is("87654321"));
    }

    @Test
    void equalsReturnsTrueForEqualObjects() {
        CompanyApi details = new CompanyApi();
        ProposedCompanyModel model1 = new ProposedCompanyModel(details, true);
        model1.setSubmissionId("submission1");

        ProposedCompanyModel model2 = new ProposedCompanyModel(details, true);
        model2.setSubmissionId("submission1");

        assertThat(model1.equals(model2), is(true));
    }

    @Test
    void equalsReturnsFalseForDifferentSubmissionIds() {
        CompanyApi details = new CompanyApi();
        ProposedCompanyModel model1 = new ProposedCompanyModel(details, true);
        model1.setSubmissionId("submission1");

        ProposedCompanyModel model2 = new ProposedCompanyModel(details, true);
        model2.setSubmissionId("submission2");

        assertThat(model1.equals(model2), is(false));
    }

    @Test
    void hashCodeIsConsistentWithEquals() {
        CompanyApi details = new CompanyApi();
        ProposedCompanyModel model1 = new ProposedCompanyModel(details, true);
        model1.setSubmissionId("submission1");

        ProposedCompanyModel model2 = new ProposedCompanyModel(details, true);
        model2.setSubmissionId("submission1");

        assertThat(model1.hashCode(), is(model2.hashCode()));
    }

}