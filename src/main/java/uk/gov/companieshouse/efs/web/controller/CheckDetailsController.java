package uk.gov.companieshouse.efs.web.controller;

import static uk.gov.companieshouse.efs.web.controller.CheckDetailsControllerImpl.ATTRIBUTE_NAME;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import uk.gov.companieshouse.efs.web.model.CheckDetailsModel;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
public interface CheckDetailsController {

    /**
     * Get request for the check your details.
     *
     * @param id            the submission id
     * @param companyNumber the company number
     * @param checkDetailsAttribute the checkDetails model
     * @param model         the checkDetails page model
     * @param request       contains the chs session id
     * @param session       the HTTP session
     * @param sessionStatus the session status; to be closed to finish the user's session journey
     * @return view name
     */
    @GetMapping("{id}/company/{companyNumber}/check-your-details")
    @CrossOrigin(origins = {"http://chs.local", "http://account.chs.local"})
    String checkDetails(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(ATTRIBUTE_NAME) final CheckDetailsModel checkDetailsAttribute, Model model, HttpServletRequest request,
        HttpSession session, SessionStatus sessionStatus);

    @PostMapping(value = {"{id}/company/{companyNumber}/check-your-details"}, params = {"action=submit"})
    String postCheckDetails(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(ATTRIBUTE_NAME) final CheckDetailsModel checkDetailsAttribute, BindingResult binding, Model model,
        HttpServletRequest request);
}