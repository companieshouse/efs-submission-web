package uk.gov.companieshouse.efs.web.controller;

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
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;
import uk.gov.companieshouse.efs.web.model.Sh19TemplateModel;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static uk.gov.companieshouse.efs.web.controller.CompanyDetailControllerImpl.ATTRIBUTE_NAME;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public interface Sh19DeliveryController {

    /**
     * Get request for the form template.
     *
     * @param sh19TemplateAttribute     the sh19 form template details see {@link FormTemplateModel}
     * @param model                     the form model
     * @param servletRequest            contains the chs session id
     * @return the view name
     */
    @GetMapping(value = {"{id}/company/{companyNumber}/sh19-delivery"})
    String sh19Delivery(@PathVariable String id, @PathVariable String companyNumber,
                        @ModelAttribute(ATTRIBUTE_NAME) Sh19TemplateModel sh19TemplateAttribute,
                        final Model model, HttpServletRequest servletRequest);

    @PostMapping(value = {"{id}/company/{companyNumber}/sh19-delivery"}, params = {"action=submit"})
    String postSh19Delivery(@PathVariable String id, @PathVariable String companyNumber,
                            @Valid @ModelAttribute(ATTRIBUTE_NAME) Sh19TemplateModel sh19TemplateAttribute,
                            BindingResult binding, Model model, ServletRequest servletRequest);
}