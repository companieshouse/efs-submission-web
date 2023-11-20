package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;
import uk.gov.companieshouse.efs.web.model.ReviewSelectionModel;

@ExtendWith(MockitoExtension.class)
class ReviewSelectionControllerImplTest extends BaseControllerImplTest {

    private ReviewSelectionController testController;
    @Mock
    private ReviewSelectionModel reviewSelectionModel;

    @BeforeEach
    void setup() {
        setUpHeaders();
        testController =
                new ReviewSelectionControllerImpl(logger, sessionService, apiClientService, reviewSelectionModel,
                        formTemplateService, categoryTemplateService);
        ((ReviewSelectionControllerImpl) testController).setChsUrl(CHS_URL);
    }

    @Test
    void getViewName() {
        assertThat(((ReviewSelectionControllerImpl) testController).getViewName(),
                is(ViewConstants.REVIEW_SELECTION_LIQ13.asView()));
    }

    @Test
    void getReviewSelectedAttribute() {
        assertThat(((ReviewSelectionControllerImpl) testController).getReviewSelectionAttribute(),
                is(sameInstance(reviewSelectionModel)));
    }

    @Test
    void getReviewSelection() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.OPEN);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        final String result = testController.reviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel,
                formTemplateAttribute, model, servletRequest);

        assertThat(result, is(ViewConstants.REVIEW_SELECTION_LIQ13.asView()));
    }

    @Test
    void getReviewSelectionWhenSubmissionIsNotOpen() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        final String result = testController.reviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel,
                formTemplateAttribute, model, servletRequest);

        assertThat(result, is(ViewConstants.GONE.asView()));
    }

    @Test
    void postReviewSelectionWhenYes() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        Map<String, Object> sessionContextData = new HashMap<>();
        sessionContextData.put(ORIGINAL_SUBMISSION_ID, SUBMISSION_ID);

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionContextData);
        when(sessionService.getUserEmail()).thenReturn("demo@ch");

        final String result =
                testController.postReviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel, bindingResult,
                        categoryTemplateAttribute, formTemplateAttribute, model, servletRequest, session);

        assertThat(result, is(ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(CHS_URL, SUBMISSION_ID, COMPANY_NUMBER)));
    }

    @Test
    void postReviewSelectionWhenNo() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        Map<String, Object> sessionContextData = new HashMap<>();
        sessionContextData.put(ORIGINAL_SUBMISSION_ID, SUBMISSION_ID);

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionContextData);
        when(sessionService.getUserEmail()).thenReturn("demo@ch");
        when(reviewSelectionModel.getConfirmed()).thenReturn("N");

        final String result =
                testController.postReviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel, bindingResult,
                        categoryTemplateAttribute, formTemplateAttribute, model, servletRequest, session);

        assertThat(result,
                is(ViewConstants.DOCUMENT_SELECTION.asRedirectUri(CHS_URL, SUBMISSION_ID, COMPANY_NUMBER, "")));
    }

    @Test
    void postReviewSelectionWhenError() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        Map<String, Object> sessionContextData = new HashMap<>();
        sessionContextData.put(ORIGINAL_SUBMISSION_ID, SUBMISSION_ID);

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionContextData);
        when(sessionService.getUserEmail()).thenReturn("xx");

        final String result =
                testController.postReviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel, bindingResult,
                        categoryTemplateAttribute, formTemplateAttribute, model, servletRequest, session);

        assertThat(result, is(ViewConstants.ERROR.asView()));
    }

    @Test
    void postReviewSelectionValidation() {
        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(getSubmissionOkResponse(submission));

        Map<String, Object> sessionContextData = new HashMap<>();
        sessionContextData.put(ORIGINAL_SUBMISSION_ID, SUBMISSION_ID);

        when(sessionService.getSessionDataFromContext()).thenReturn(sessionContextData);
        when(sessionService.getUserEmail()).thenReturn("demo@ch");
        when(bindingResult.hasErrors()).thenReturn(true);

        final String result =
                testController.postReviewSelection(SUBMISSION_ID, COMPANY_NUMBER, reviewSelectionModel, bindingResult,
                        categoryTemplateAttribute, formTemplateAttribute, model, servletRequest, session);

        assertThat(result, is(ViewConstants.REVIEW_SELECTION_LIQ13.asView()));
    }
}