package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;

@ExtendWith(MockitoExtension.class)
class RegistrationsInfoControllerImplTest extends BaseControllerImplTest {

    private RegistrationsInfoController testController;

    @BeforeEach
    public void setup() {
        setUpHeaders();
        testController = new RegistrationsInfoControllerImpl(logger, sessionService, apiClientService,
            formTemplateService, categoryTemplateService);
        ((RegistrationsInfoControllerImpl) testController).setChsUrl(CHS_URL);
        // turn on feature flag
        ReflectionTestUtils.setField(testController, "registrationsEnabled", true);

    }

    @Test
    void getViewName() {
        assertThat(((RegistrationsInfoControllerImpl) testController).getViewName(),
            is(ViewConstants.REGISTRATIONS_INFO.asView()));
    }

    @Test
    void getRegistrationsInfoWhenFeatureDisabled() {

        ReflectionTestUtils.setField(testController, "registrationsEnabled", false);

        final String result = testController.registrationsInfo(SUBMISSION_ID, COMPANY_NUMBER, categoryTemplateAttribute,
            model, servletRequest);

        assertThat(result, is(ViewConstants.MISSING.asView()));
    }

    @Test
    void getRegistrationsInfo() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.OPEN);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
            getSubmissionOkResponse(submission));

        final String result = testController.registrationsInfo(SUBMISSION_ID, COMPANY_NUMBER, categoryTemplateAttribute,
            model, servletRequest);

        assertThat(result, is(ViewConstants.REGISTRATIONS_INFO.asView()));
    }

    @Test
    void getRegistrationsInfoWhenSubmissionIsNotOpen() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
            getSubmissionOkResponse(submission));

        final String result = testController.registrationsInfo(SUBMISSION_ID, COMPANY_NUMBER, categoryTemplateAttribute,
            model, servletRequest);

        assertThat(result, is(ViewConstants.GONE.asView()));
    }

}