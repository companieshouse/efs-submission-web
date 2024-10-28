package uk.gov.companieshouse.efs.web.formtemplates.controller;

import static uk.gov.companieshouse.efs.web.formtemplates.controller.FormTemplateControllerImpl.ATTRIBUTE_NAME;

import jakarta.servlet.ServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTemplateControllerImpl;
import uk.gov.companieshouse.efs.web.categorytemplates.model.CategoryTemplateModel;
import uk.gov.companieshouse.efs.web.controller.BaseControllerImpl;
import uk.gov.companieshouse.efs.web.formtemplates.model.FormTemplateModel;

@RequestMapping(BaseControllerImpl.SERVICE_URI)
public interface FormTemplateController {

    @GetMapping(value = {"{id}/company/{companyNumber}/document-selection"})
    @CrossOrigin(origins = {"http://chs.local", "http://account.chs.local"})
    String formTemplate(@PathVariable String id, @PathVariable String companyNumber,
            @RequestParam("category") String category,
            @SessionAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME)
                    CategoryTemplateModel categoryTemplateAttribute,
            @ModelAttribute(ATTRIBUTE_NAME) FormTemplateModel formTemplateAttribute, Model model,
            ServletRequest servletRequest);

    @PostMapping(value = {"{id}/company/{companyNumber}/document-selection"},
            params = {"action=submit"})
    String postFormTemplate(@PathVariable String id, @PathVariable String companyNumber,
            @SessionAttribute(CategoryTemplateControllerImpl.ATTRIBUTE_NAME)
                    CategoryTemplateModel categoryTemplateAttribute,
            @ModelAttribute(ATTRIBUTE_NAME) FormTemplateModel formTemplateAttribute,
            BindingResult binding, Model model, ServletRequest servletRequest);
}
