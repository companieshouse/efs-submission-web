package uk.gov.companieshouse.efs.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.submissions.CompanyApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;
import uk.gov.companieshouse.efs.web.service.company.CompanyService;

import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class CompanyDetailControllerImplTest extends BaseControllerImplTest {
    private CompanyDetailController testController;

    @Mock
    private CompanyService companyService;
    @Mock
    private CompanyDetail companyDetailAttribute;

    private final List<String> prefixBlockList = Arrays.asList("OE");
    private static final String OVERSEAS_ENTITY_COMPANY_NUMBER = "OE123456";

    @BeforeEach
    public void setup() {
        setUpHeaders();
        testController = new CompanyDetailControllerImpl(companyService, sessionService, apiClientService, logger,
                companyDetailAttribute);
        ((CompanyDetailControllerImpl) testController).setChsUrl(CHS_URL);

        ReflectionTestUtils.setField(testController, "prefixBlockList", prefixBlockList);

        mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler(logger))
                .build();
    }

    @Test
    void getViewName() {
        assertThat(((CompanyDetailControllerImpl) testController).getViewName(),
                is(ViewConstants.COMPANY_DETAIL.asView()));

    }

    @Test
    void getCompanyDetailAttribute() {
        assertThat(((CompanyDetailControllerImpl) testController).getCompanyDetailAttribute(),
            is(sameInstance(companyDetailAttribute)));
    }

    @Test
    void getCompanyDetailWhenFeatureDisabled() {
        final String viewName = testController
            .getCompanyDetail(SUBMISSION_ID, COMPANY_NUMBER, companyDetailAttribute, model, servletRequest);

        verify(companyService).getCompanyDetail(companyDetailAttribute, COMPANY_NUMBER);
        assertThat(viewName, is(ViewConstants.COMPANY_DETAIL.asView()));
    }

    @Test
    void getCompanyDetailWhenFeatureEnabled() {
        ReflectionTestUtils.setField(testController, "registrationsEnabled", true);

        final String viewName = testController
            .getCompanyDetail(SUBMISSION_ID, COMPANY_NUMBER, companyDetailAttribute, model, servletRequest);

        verify(companyService).getCompanyDetail(companyDetailAttribute, COMPANY_NUMBER);
        assertThat(viewName, is(ViewConstants.COMPANY_DETAIL.asView()));
    }

    @Test
    void getCompanyDetailWhenFeatureDisabledNoCompany() {
        final String viewName = testController
            .getCompanyDetail(SUBMISSION_ID, "noCompany", companyDetailAttribute, model, servletRequest);

        verifyNoInteractions(companyService);
        assertThat(viewName, is(ViewConstants.MISSING.asView()));
    }

    @Test
    void getCompanyDetailWhenFeatureEnabledNoCompany() {
        ReflectionTestUtils.setField(testController, "registrationsEnabled", true);

        final String viewName =
            testController.getCompanyDetail(SUBMISSION_ID, "noCompany", companyDetailAttribute,
                model, servletRequest);

        verifyNoInteractions(companyService);
        assertThat(viewName,
            is(ViewConstants.PROPOSED_COMPANY.asRedirectUri(CHS_URL, SUBMISSION_ID, "noCompany")));
    }

    @Test
    void getCompanyDetailWhenRuntimeError() throws Exception {
        doThrow(new RuntimeException("dummy exception")).when(companyService)
                .getCompanyDetail(companyDetailAttribute, COMPANY_NUMBER);

        String url = String.format("/efs-submission/%s/company/%s/details", SUBMISSION_ID, COMPANY_NUMBER);
        mockMvc.perform(get(url))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name(ViewConstants.ERROR.asView()))
                .andReturn();
    }

    @Test
    void postCompanyDetail() {
        when(companyDetailAttribute.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(companyDetailAttribute.getCompanyName()).thenReturn(COMPANY_NAME);
        when(apiClientService.putCompany(eq(SUBMISSION_ID), refEq(new CompanyApi(COMPANY_NUMBER, COMPANY_NAME))))
            .thenReturn(new ApiResponse<>(200, getHeaders(), new SubmissionResponseApi()));

        final String viewName = testController
            .postCompanyDetail(SUBMISSION_ID, COMPANY_NUMBER, companyDetailAttribute, model, servletRequest);

        assertThat(viewName,
            is(ViewConstants.CATEGORY_SELECTION.asRedirectUri(CHS_URL, SUBMISSION_ID, COMPANY_NUMBER)));
    }

    @Test
    void postCompanyDetailWhenNumberNull() throws Exception {
        when(apiClientService.putCompany(eq(SUBMISSION_ID), refEq(new CompanyApi())))
                .thenThrow(new RuntimeException("company number is null"));

        String url = String.format("/efs-submission/%s/company/%s/details?action=submit", SUBMISSION_ID, COMPANY_NUMBER);
        mockMvc.perform(post(url))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name(ViewConstants.ERROR.asView()))
                .andReturn();
    }

    @Test
    void getCompanyDetailWhenOverseasEntityCompany() throws Exception {
        final String viewName = testController
                .getCompanyDetail(SUBMISSION_ID, OVERSEAS_ENTITY_COMPANY_NUMBER, companyDetailAttribute, model, servletRequest);

        verify(companyService).getCompanyDetail(companyDetailAttribute, OVERSEAS_ENTITY_COMPANY_NUMBER);
        assertThat(viewName,
                is(ViewConstants.RESTRICTED_COMPANY_TYPE.asRedirectUri(CHS_URL, SUBMISSION_ID, OVERSEAS_ENTITY_COMPANY_NUMBER)));

    }

    @Test
    void getCompanyDetailWhenOverseasEntityNullBlockList()throws Exception {
        ReflectionTestUtils.setField(testController, "prefixBlockList", null);
        final String viewName = testController
                .getCompanyDetail(SUBMISSION_ID, OVERSEAS_ENTITY_COMPANY_NUMBER, companyDetailAttribute, model, servletRequest);

        verify(companyService).getCompanyDetail(companyDetailAttribute, OVERSEAS_ENTITY_COMPANY_NUMBER);
        assertThat(viewName, is(ViewConstants.COMPANY_DETAIL.asView()));
    }

}