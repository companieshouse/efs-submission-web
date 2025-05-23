package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;

@ExtendWith(MockitoExtension.class)
class ResolutionsInfoControllerImplTest extends BaseControllerImplTest {

    public static final FormTemplateApi RESOLUTIONS_FORM =
        new FormTemplateApi("RESOLUTIONS", "Resolutions", "RESOLUTIONS", "", true, true, null,
            false, null);

    private ResolutionsInfoController testController;

    @BeforeEach
    void setup() {
        setUpHeaders();
        testController = new ResolutionsInfoControllerImpl(logger, sessionService, apiClientService,
            formTemplateService, categoryTemplateService);
        ((ResolutionsInfoControllerImpl) testController).setChsUrl(CHS_URL);
    }

    @Test
    void getViewName() {
        assertThat(((ResolutionsInfoControllerImpl) testController).getViewName(),
            is(ViewConstants.RESOLUTIONS_INFO.asView()));
    }

    @Test
    void getResolutionsInfo() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.OPEN);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
            getSubmissionOkResponse(submission));

        final String result = testController.resolutionsInfo(SUBMISSION_ID, COMPANY_NUMBER, formTemplateAttribute,
                model, servletRequest);

        assertThat(result, is(ViewConstants.RESOLUTIONS_INFO.asView()));
    }

    @Test
    void getResolutionsInfoWhenSubmissionIsNotOpen() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
            getSubmissionOkResponse(submission));

        final String result = testController.resolutionsInfo(SUBMISSION_ID, COMPANY_NUMBER, formTemplateAttribute,
                model, servletRequest);

        assertThat(result, is(ViewConstants.GONE.asView()));
    }

    @Test
    void getResolutionsInfoWhenSubmissionApiCompanyIsNull() {
        final SubmissionApi submission = createSubmissionNullCompany();

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
                getSubmissionOkResponse(submission));
        when(submission.getCompany()).thenReturn(null);

        final String result = testController.resolutionsInfo(SUBMISSION_ID, COMPANY_NUMBER, formTemplateAttribute,
                model, servletRequest);

        assertThat(result, is(ViewConstants.GONE.asView()));
    }

    @Test
    void postResolutionsInfo() {
        final FormTemplateApi formTemplateApi = new FormTemplateApi();
        final FormTemplateApi resolutionsForm = new FormTemplateApi(RESOLUTIONS_FORM);

        formTemplateApi.setFormType("RESOLUTIONS");

        final String result = testController.postResolutionsInfo(SUBMISSION_ID, COMPANY_NUMBER, formTemplateAttribute,
                bindingResult, model, servletRequest, session);

        assertThat(formTemplateApi.getFormType(), is(resolutionsForm.getFormType()));
        assertThat(result, is(ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(CHS_URL, SUBMISSION_ID, COMPANY_NUMBER)));
    }
}