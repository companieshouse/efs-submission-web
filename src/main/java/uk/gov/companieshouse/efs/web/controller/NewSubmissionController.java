package uk.gov.companieshouse.efs.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
public interface NewSubmissionController {
    /**
     * Endpoint for new submissions (no company number yet).
     *
     * @param companyDetailAttribute the company selection details see {@link CompanyDetail}
     * @param sessionStatus          the MVC session status
     * @param request                the MVC servlet request
     * @return the view to show
     */
    @GetMapping("/new-submission")
    String newSubmission(
        @ModelAttribute(CompanyDetailControllerImpl.ATTRIBUTE_NAME) CompanyDetail companyDetailAttribute,
        SessionStatus sessionStatus, HttpServletRequest request, RedirectAttributes attributes);

    /**
     * Endpoint for new submissions (given company number).
     *
     * @param companyNumber the company number to apply
     * @param companyDetailAttribute the company selection details see {@link CompanyDetail}
     * @param sessionStatus          the MVC session status
     * @param request                the MVC servlet request
     * @return the view to show
     */
    @GetMapping("/company/{companyNumber}/new-submission")
    String newSubmissionForCompany(@PathVariable String companyNumber,
        @ModelAttribute(CompanyDetailControllerImpl.ATTRIBUTE_NAME) CompanyDetail companyDetailAttribute,
        SessionStatus sessionStatus, HttpServletRequest request, RedirectAttributes attributes);
}
