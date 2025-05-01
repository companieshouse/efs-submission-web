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
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
public interface ResolutionsInfoController {

    /**
     * Get request for the category template.
     *
     * @param categoryTemplateAttribute the category template details see {@link CategoryTemplateModel}
     * @param model                     the category model
     * @param servletRequest            contains the chs session id
     * @return the view name
     */
    @GetMapping(value = {"{id}/company/{companyNumber}/resolutions-info"})
    String resolutionsInfo(@PathVariable String id, @PathVariable String companyNumber,
                               @ModelAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME) CategoryTemplateModel categoryTemplateAttribute, Model model, HttpServletRequest servletRequest);

    @PostMapping(value = {"{id}/company/{companyNumber}/resolutions-info"}, params = {"action=submit"})
    String postResolutionsInfo(@PathVariable String id, @PathVariable String companyNumber,
                                   @ModelAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME) CategoryTemplateModel categoryTemplateAttribute,
                                   BindingResult binding, Model model, ServletRequest servletRequest, final HttpSession session);
}