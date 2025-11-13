package uk.gov.companieshouse.efs.web.service.company.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.efs.web.exception.ServiceException;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.company.CompanyService;
import uk.gov.companieshouse.efs.web.transformer.CompanyDetailTransformer;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    private CompanyService testService;
    private static final String COMPANY_NUMBER = "12345678";
    @Mock
    private Logger logger;
    @Mock
    private ApiClientService apiClientService;
    @Mock
    private InternalApiClient privateApiClient;
    @Mock
    private CompanyResourceHandler companyResourceHandler;
    @Mock
    private CompanyGet companyGet;
    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;
    @Mock
    private CompanyDetailTransformer companyDetailTransformer;
    @Mock
    private CompanyDetail companyDetailAttribute;

    @BeforeEach
    protected void setUp() throws ApiErrorResponseException, URIValidationException {
        testService = new CompanyServiceImpl(apiClientService, companyDetailTransformer, logger);
        when(apiClientService.getApiClient()).thenReturn(privateApiClient);
        when(privateApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);
    }

    @Test
    void getCompanyProfile() {
        testService.getCompanyProfile(COMPANY_NUMBER);
        verify(apiResponse).getData();
    }

    @Test
    void getCompanyProfileReturnsCompanyProfileApi() {
        CompanyProfileApi expectedProfile = new CompanyProfileApi();
        when(apiResponse.getData()).thenReturn(expectedProfile);

        CompanyProfileApi result = testService.getCompanyProfile(COMPANY_NUMBER);

        assertThat(result, is(expectedProfile));
    }

    @Test
    void getCompanyProfileThrowsServiceExceptionOnApiErrorResponseException() throws Exception {
        HttpResponseException.Builder builder = new HttpResponseException.Builder(400, "API error", new HttpHeaders());
        when(companyGet.execute()).thenThrow(new ApiErrorResponseException(builder));

        ServiceException exception = assertThrows(ServiceException.class, () -> testService.getCompanyProfile(COMPANY_NUMBER));

        assertThat(exception.getMessage(), is("Error retrieving company profile"));
    }

    @Test
    void getCompanyProfileThrowsServiceExceptionOnURIValidationException() throws Exception {
        when(companyGet.execute()).thenThrow(new URIValidationException("Invalid URI"));

        ServiceException exception = assertThrows(ServiceException.class, () -> testService.getCompanyProfile(COMPANY_NUMBER));

        assertThat(exception.getMessage(), is("Invalid URI for company resource"));
    }

    @Test
    void getCompanyDetail() {
        CompanyProfileApi profile = new CompanyProfileApi();
        when(apiResponse.getData()).thenReturn(profile);

        testService.getCompanyDetail(companyDetailAttribute, COMPANY_NUMBER);

        verify(companyDetailTransformer).getCompanyDetail(companyDetailAttribute, profile);
        verifyNoMoreInteractions(companyDetailAttribute);
    }
}