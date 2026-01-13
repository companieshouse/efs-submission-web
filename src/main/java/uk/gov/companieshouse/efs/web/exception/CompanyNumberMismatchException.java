package uk.gov.companieshouse.efs.web.exception;

public class CompanyNumberMismatchException extends RuntimeException {
    public CompanyNumberMismatchException() {
        super("Company number in request URL does not match company number in submission");
    }
}
