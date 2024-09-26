package uk.gov.companieshouse.efs.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import uk.gov.companieshouse.efs.web.model.company.CompanyDetail;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
public interface ConfirmationController {

    /**
     * Get request for the submission confirmation when company auth required.
     *
     * @param id            the submission id
     * @param companyNumber the company number
     * @param model         the confirmation page model
     * @param request       contains the chs session id
     * @param session       the HTTP session
     * @param sessionStatus the session status; to be closed to finish the user's session journey
     * @return view name
     */
    @GetMapping("{id}/company/{companyNumber}/confirmation")
    String getConfirmation(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(CompanyDetailControllerImpl.ATTRIBUTE_NAME)
            CompanyDetail companyDetailAttribute, Model model, HttpServletRequest request,
        HttpSession session, SessionStatus sessionStatus);
}