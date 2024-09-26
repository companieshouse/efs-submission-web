package uk.gov.companieshouse.efs.web.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

class Sh19TemplateModelTest {

    private Sh19TemplateModel testSh19TemplateModel;
    private FormTemplateApi formTemplateApi;

    @BeforeEach
    void setUp() {
        formTemplateApi = new FormTemplateApi("SH19", "SH19", "SH",
            "£10", true, true, null, false, null);
        testSh19TemplateModel = new Sh19TemplateModel(formTemplateApi);
    }

    @Test
    void constructor() {
        assertThat(testSh19TemplateModel.getDetails(), is(equalTo(formTemplateApi)));
        assertThat(testSh19TemplateModel.getFormTemplateList(), is(empty()));
    }

    @Test
    void testToString() {

        String toString = testSh19TemplateModel.toString();
        assertThat(toString, containsString("formCategory=SH"));
        assertThat(toString, containsString("formName=SH19"));
        assertThat(toString, containsString("formType=SH19"));
        assertThat(toString, containsString("isAuthenticationRequired=true"));
        assertThat(toString, containsString("fesDocType=<null>"));
        assertThat(toString, containsString("isFesEnabled=true"));
        assertThat(toString, containsString("messageTexts=<null>"));
        assertThat(toString, containsString("paymentCharge=£10"));
        assertThat(toString, containsString("sameDay=false"));
        assertThat(toString, allOf(containsString("formTemplateList="),
            containsString("FormTemplateListApi")));
    }
}