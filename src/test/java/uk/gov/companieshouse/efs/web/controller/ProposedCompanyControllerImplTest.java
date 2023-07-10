package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.efs.web.model.ProposedCompanyModel;

@ExtendWith(MockitoExtension.class)
class ProposedCompanyControllerImplTest extends BaseControllerImplTest {
    private ProposedCompanyController testController;

    @Mock
    private ProposedCompanyModel proposedCompanyAttribute;

    @BeforeEach
    void setUp() {
        setUpHeaders();
        testController = new ProposedCompanyControllerImpl(logger, sessionService, apiClientService,
            proposedCompanyAttribute);
        ((ProposedCompanyControllerImpl) testController).setChsUrl(CHS_URL);
        // turn on feature flag
        ReflectionTestUtils.setField(testController, "registrationsEnabled", true);
    }

    @Test
    void getProposedCompanyAttribute() {
        assertThat(((ProposedCompanyControllerImpl) testController).getProposedCompanyAttribute(),
            is(sameInstance(proposedCompanyAttribute)));
    }

    @Test
    void getViewName() {
        MatcherAssert.assertThat(((ProposedCompanyControllerImpl) testController).getViewName(),
            is(ViewConstants.PROPOSED_COMPANY.asView()));
    }

    @Test
    void prepareWhenFeatureDisabled() {
        ReflectionTestUtils.setField(testController, "registrationsEnabled", false);

        final String result =
            testController.prepare(SUBMISSION_ID, proposedCompanyAttribute, model, request);

        assertThat(result, is(ViewConstants.MISSING.asView()));
    }

    @Test
    void prepareWhenFeatureEnabled() {
        when(proposedCompanyAttribute.getName()).thenReturn(COMPANY_NAME);
        final String result =
            testController.prepare(SUBMISSION_ID, proposedCompanyAttribute, model, request);

        verify(proposedCompanyAttribute).setSubmissionId(SUBMISSION_ID);
        verify(proposedCompanyAttribute).setNumber(
            ProposedCompanyControllerImpl.TEMP_COMPANY_NUMBER);
        verify(proposedCompanyAttribute).setName(COMPANY_NAME);
        assertThat(result, is(ViewConstants.PROPOSED_COMPANY.asView()));
    }

    @Test
    void processWhenFeatureDisabled() {
        ReflectionTestUtils.setField(testController, "registrationsEnabled", false);

        final String result =
            testController.process(SUBMISSION_ID, proposedCompanyAttribute, bindingResult, model,
                request, session);

        assertThat(result, is(ViewConstants.MISSING.asView()));
    }

    @Test
    void processWhenFeatureEnabledAndBindingErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        final String result =
            testController.process(SUBMISSION_ID, proposedCompanyAttribute, bindingResult, model,
                request, session);

        verify(model).addAttribute(TEMPLATE_NAME, ViewConstants.PROPOSED_COMPANY.asView());
        assertThat(result, is(ViewConstants.PROPOSED_COMPANY.asView()));
    }

    @Test
    void processWhenFeatureEnabledAndNameRequiredNull() {
        when(bindingResult.hasErrors()).thenReturn(true);

        final String result =
            testController.process(SUBMISSION_ID, proposedCompanyAttribute, bindingResult, model,
                request, session);

        verify(model).addAttribute(TEMPLATE_NAME, ViewConstants.PROPOSED_COMPANY.asView());
        assertThat(result, is(ViewConstants.PROPOSED_COMPANY.asView()));
    }

    @Test
    void processWhenFeatureEnabledAndNameRequiredFalse() {
        when(proposedCompanyAttribute.getNameRequired()).thenReturn(Boolean.FALSE);
        when(proposedCompanyAttribute.getNumber()).thenReturn(
            ProposedCompanyControllerImpl.TEMP_COMPANY_NUMBER);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
                getSubmissionOkResponse(submission));

        final String result =
            testController.process(SUBMISSION_ID, proposedCompanyAttribute, bindingResult, model,
                request, session);

        verify(proposedCompanyAttribute).setName(null);
        assertThat(result, is(ViewConstants.REGISTRATIONS_INFO.asRedirectUri(CHS_URL, SUBMISSION_ID,
            ProposedCompanyControllerImpl.TEMP_COMPANY_NUMBER)));
    }

    @Test
    void processWhenFeatureEnabledAndNameRequiredTrue() {
        when(proposedCompanyAttribute.getNameRequired()).thenReturn(Boolean.TRUE);
        when(proposedCompanyAttribute.getNumber()).thenReturn(
            ProposedCompanyControllerImpl.TEMP_COMPANY_NUMBER);
        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
                getSubmissionOkResponse(submission));

        final String result =
            testController.process(SUBMISSION_ID, proposedCompanyAttribute, bindingResult, model,
                request, session);


        verify(proposedCompanyAttribute).setName("");
        assertThat(result, is(ViewConstants.CATEGORY_SELECTION.asRedirectUri(CHS_URL, SUBMISSION_ID,
            ProposedCompanyControllerImpl.TEMP_COMPANY_NUMBER, "INC")));
    }
}