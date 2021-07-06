package uk.gov.companieshouse.efs.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.submissions.FormTypeApi;
import uk.gov.companieshouse.api.model.efs.submissions.PresenterApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionStatus;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Sh19DeliveryControllerTest extends BaseControllerImplTest{

    private Sh19DeliveryController testController;

    final static String PRESENTER_EMAIL = "test@email.com";

    public static final FormTemplateApi FORM_TEMPLATE_1 =
        new FormTemplateApi("SH19", "Test01", "SH", "100", true, true, null, false,
            null);

    public static final FormTemplateApi FORM_TEMPLATE_2 =
        new FormTemplateApi("SH19_SAMEDAY", "Test02", "SH", "100", true, true, null, true,
            null);

    public static final List<FormTemplateApi> FORM_TEMPLATE_LIST =
        Arrays.asList(FORM_TEMPLATE_1, FORM_TEMPLATE_2);

    @BeforeEach
    protected void setUp() {
        testController = new Sh19DeliveryControllerImpl(logger, sessionService, apiClientService,
            formTemplateService, categoryTemplateService, sh19TemplateAttribute);

        ((Sh19DeliveryControllerImpl) testController).setChsUrl(CHS_URL);
    }

    @Test
    void getSH19TemplateAttribute() {
        assertThat(((Sh19DeliveryControllerImpl) testController).getSh19TemplateAttribute(),
            is(sameInstance(sh19TemplateAttribute)));
    }

    @Test
    void getViewName() {
        assertThat(((Sh19DeliveryControllerImpl) testController).getViewName(),
            is(ViewConstants.SH19_DELIVERY.asView()));
    }

    @Test
    void sh19TemplateWhenSubmissionNotOpen() {

        final SubmissionApi submission = createSubmission(SubmissionStatus.SUBMITTED);

        when(apiClientService.getSubmission(SUBMISSION_ID)).thenReturn(
            getSubmissionOkResponse(submission));

        final String result = testController.sh19Delivery(SUBMISSION_ID, COMPANY_NUMBER,
            sh19TemplateAttribute, model, servletRequest);

        verifyNoInteractions(formTemplateService, sh19TemplateAttribute, model);
        assertThat(result, is(ViewConstants.GONE.asView()));
    }

    @ParameterizedTest
    @ValueSource(strings={"SH19","SH19_SAMEDAY",""})
    void formTemplateWhenSubmissionOpenAndNoneSelected(String selectedForm) {

        final SubmissionApi submission = createSubmission(SubmissionStatus.OPEN);
        PresenterApi presenter = new PresenterApi();
        presenter.setEmail(PRESENTER_EMAIL);
        submission.setPresenter(presenter);

        expectOpenSubmission(submission);

        if (!selectedForm.isEmpty()) {
            when(sh19TemplateAttribute.getFormType()).thenReturn(selectedForm);
        }

        String template = testController.sh19Delivery(SUBMISSION_ID, COMPANY_NUMBER,
            sh19TemplateAttribute, model, servletRequest);

        verify(sh19TemplateAttribute).setFormTemplateList(FORM_TEMPLATE_LIST);
        verify(sh19TemplateAttribute).setSubmissionId(SUBMISSION_ID);
        if (selectedForm.isEmpty()) {
            verify(sh19TemplateAttribute).setDetails(isA(FormTemplateApi.class));
        }
        verify(model).addAttribute(TEMPLATE_NAME, ViewConstants.SH19_DELIVERY.asView());

        assertThat(template, is(ViewConstants.SH19_DELIVERY.asView()));
    }

    @Test
    void postSh19TemplateWhenSelectionMissing() {
        when(bindingResult.hasErrors()).thenReturn(true);

        final String result = testController.postSh19Delivery(SUBMISSION_ID, COMPANY_NUMBER,
            sh19TemplateAttribute, bindingResult, model, servletRequest);

        verify(model).addAttribute(TEMPLATE_NAME, ViewConstants.SH19_DELIVERY.asView());
        assertThat(result, is(ViewConstants.SH19_DELIVERY.asView()));
    }

    @Test
    void postFormTemplate() {

        when(sh19TemplateAttribute.getFormTemplateList()).thenReturn(FORM_TEMPLATE_LIST);
        when(sh19TemplateAttribute.getFormType()).thenReturn(FORM_TEMPLATE_1.getFormType());
        when(apiClientService.putFormType(SUBMISSION_ID, new FormTypeApi(FORM_TEMPLATE_1.getFormType()))).
            thenReturn(new ApiResponse(200, getHeaders(), new SubmissionResponseApi(FORM_TEMPLATE_1.getFormType())));

        final String result = testController.postSh19Delivery(SUBMISSION_ID, COMPANY_NUMBER,
            sh19TemplateAttribute, bindingResult, model, servletRequest);

        verify(bindingResult).hasErrors();
        verifyNoMoreInteractions(bindingResult, apiClientService);

        assertThat(result, is(ViewConstants.CHECK_DETAILS
            .asRedirectUri(CHS_URL, SUBMISSION_ID, COMPANY_NUMBER)));
    }

    private void expectOpenSubmission(final SubmissionApi submission) {
        when(apiClientService.getSubmission(SUBMISSION_ID))
            .thenReturn(getSubmissionOkResponse(submission));

        ApiResponse<FormTemplateApi> sh19 = new ApiResponse<>(200, getHeaders(), FORM_TEMPLATE_1);
        ApiResponse<FormTemplateApi> sh19Sameday = new ApiResponse<>(200, getHeaders(), FORM_TEMPLATE_2);

        when(formTemplateService.getFormTemplate(anyString()))
            .thenReturn(sh19, sh19Sameday);

        when(sh19TemplateAttribute.getFormTemplateList()).thenReturn(FORM_TEMPLATE_LIST);
    }
}