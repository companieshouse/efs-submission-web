package uk.gov.companieshouse.efs.web.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public interface ResolutionsInfoController {

    /**
     * Get request for the resolutions info page.
     *
     * @param formTemplateAttribute the form template details see {@link FormTemplateModel}
     * @param model                     the MVC model
     * @param servletRequest            contains the chs session id
     * @return the view name
     */
    @GetMapping(value = {"{id}/company/{companyNumber}/resolutions-info"})
    String resolutionsInfo(@PathVariable String id, @PathVariable String companyNumber,
                           @ModelAttribute(FormTemplateControllerImpl.ATTRIBUTE_NAME) FormTemplateModel formTemplateAttribute, Model model, HttpServletRequest servletRequest);

    @PostMapping(value = {"{id}/company/{companyNumber}/resolutions-info"}, params = {"action=submit"})
    String postResolutionsInfo(@PathVariable String id, @PathVariable String companyNumber,
                                   @ModelAttribute(FormTemplateControllerImpl.ATTRIBUTE_NAME) FormTemplateModel formTemplateAttribute,
                                   BindingResult binding, Model model, ServletRequest servletRequest, final HttpSession session);
}