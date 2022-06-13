package uk.gov.companieshouse.efs.web.controller;

import static uk.gov.companieshouse.efs.web.controller.CompanyDetailControllerImpl.ATTRIBUTE_NAME;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;
import uk.gov.companieshouse.efs.web.model.ReviewSelectionModel;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public interface ReviewSelectionController {

    /**
     * Get request for the Review Selection screen
     *
     * @param id                      submission id
     * @param companyNumber           company number
     * @param reviewSelectedAttribute confirmation or not of the document selection
     * @param formTemplateAttribute   form details
     * @param model                   review selection model
     * @param servletRequest          contains the chs session id
     * @return view name
     */
    @GetMapping(value = {"{id}/company/{companyNumber}/review-selection"})
    String reviewSelection(@PathVariable String id, @PathVariable String companyNumber,
                           @ModelAttribute(ATTRIBUTE_NAME) ReviewSelectionModel reviewSelectedAttribute,
                           FormTemplateModel formTemplateAttribute, Model model, HttpServletRequest servletRequest);

    /**
     * Post request for the Review Selection screen
     *
     * @param id                        submission id
     * @param companyNumber             company number
     * @param reviewSelectedAttribute   confirmation or not of the document selection
     * @param binding                   the MVC binding result
     * @param categoryTemplateAttribute category selected details
     * @param formTemplateAttribute     form selected details
     * @param model                     review selection model
     * @param servletRequest            contains the chs session id
     * @param session                   the HTTP session
     * @return view name of next page
     */
    @PostMapping(value = {"{id}/company/{companyNumber}/review-selection"}, params = {"action=submit"})
    String postReviewSelection(@PathVariable String id, @PathVariable String companyNumber,
                               @ModelAttribute(ATTRIBUTE_NAME) ReviewSelectionModel reviewSelectedAttribute,
                               BindingResult binding, @ModelAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME)
                               CategoryTemplateModel categoryTemplateAttribute,
                               @ModelAttribute(FormTemplateControllerImpl.ATTRIBUTE_NAME)
                               FormTemplateModel formTemplateAttribute, Model model, ServletRequest servletRequest,
                               final HttpSession session);
}