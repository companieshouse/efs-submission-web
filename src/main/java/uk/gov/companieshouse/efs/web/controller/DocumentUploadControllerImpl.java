package uk.gov.companieshouse.efs.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.efs.formtemplates.FormTemplateApi;
import uk.gov.companieshouse.api.model.efs.submissions.FileApi;
import uk.gov.companieshouse.api.model.efs.submissions.FileListApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionFormApi;
import uk.gov.companieshouse.api.model.efs.submissions.SubmissionResponseApi;
import uk.gov.companieshouse.efs.web.categorytemplates.controller.CategoryTypeConstants;
import uk.gov.companieshouse.efs.web.configuration.FileUploadConfiguration;
import uk.gov.companieshouse.efs.web.formtemplates.service.api.FormTemplateService;
import uk.gov.companieshouse.efs.web.model.DocumentUploadModel;
import uk.gov.companieshouse.efs.web.service.api.ApiClientService;
import uk.gov.companieshouse.efs.web.service.session.SessionService;
import uk.gov.companieshouse.efs.web.transfer.FileTransferApiClient;
import uk.gov.companieshouse.efs.web.validation.DocumentUploadValidator;
import uk.gov.companieshouse.logging.Logger;

import static uk.gov.companieshouse.efs.web.controller.DocumentUploadControllerImpl.ATTRIBUTE_NAME;

@Controller
@SessionAttributes(ATTRIBUTE_NAME)
@SuppressWarnings("squid:S3753")
/* S3753: "@Controller" classes that use "@SessionAttributes" must call "setComplete" on their "SessionStatus" objects
 *
 * The nature of the web journey across several controllers means it's not appropriate to do this. However,
 * setComplete() is properly called in ConfirmationControllerImpl at the end of the submission journey.
 */
public class DocumentUploadControllerImpl extends BaseControllerImpl implements DocumentUploadController {

    static final Integer FILE_UPLOADS_ALLOWED_FOR_FES_ENABLED_FORMS_INCORPORATION = 5;
    static final Integer FILE_UPLOADS_ALLOWED_FOR_FES_ENABLED_FORMS = 1;

    /**
     * Define the model name for this action.
     */
    public static final String ATTRIBUTE_NAME = "documentUpload";

    private FileTransferApiClient fileTransferApiClient;
    private FileUploadConfiguration fileUploadConfiguration;
    private DocumentUploadValidator documentUploadValidator;
    private DocumentUploadModel documentUploadAttribute;
    private ResourceBundle resourceBundle;

    /**
     * Constructor used by child controllers.
     *
     * @param fileTransferManager - Parameter object that groups some file transfer related in order
     *                            to reduce the number of constructor parameters.
     * @param logger the CH logger
     */
    @Autowired
    public DocumentUploadControllerImpl(FileTransferManager fileTransferManager, Logger logger,
                                        SessionService sessionService, ApiClientService apiClientService, DocumentUploadModel documentUploadAttribute,
                                        final FormTemplateService formTemplateService) {

        super(logger, sessionService, apiClientService);
        this.fileUploadConfiguration = fileTransferManager.getFileUploadConfiguration();
        this.fileTransferApiClient = fileTransferManager.getFileTransferApiClient();
        this.documentUploadValidator = fileTransferManager.getDocumentUploadValidator();
        this.documentUploadAttribute = documentUploadAttribute;
        this.resourceBundle = ResourceBundle.getBundle("messages", Locale.UK);
        this.formTemplateService = formTemplateService;
    }

    @ModelAttribute(ATTRIBUTE_NAME)
    public DocumentUploadModel getDocumentUploadAttribute() {
        return documentUploadAttribute;
    }

    @Override
    public String getViewName() {
        return ViewConstants.DOCUMENT_UPLOAD.asView();
    }

    @Override
    public String prepare(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(ATTRIBUTE_NAME) DocumentUploadModel documentUploadAttribute,
        Model model, HttpServletRequest servletRequest, HttpSession session) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));

        if (!verifySubmission(submissionApi)) {
            return ViewConstants.ERROR.asView();
        }

        if (!ALLOWED_STATUSES.contains(submissionApi.getStatus())) {
            return ViewConstants.GONE.asView();
        }

        FileListApi uploadedFiles = getUploadedFiles(submissionApi);
        final FormTemplateApi formTemplate = getFormTemplateApi(submissionApi.getSubmissionForm().getFormType());

        addDataToPrepareModel(documentUploadAttribute, submissionApi, formTemplate, uploadedFiles);
        addDynamicHintText(documentUploadAttribute, formTemplate.getFormCategory());
        model.mergeAttributes(documentUploadAttribute.getAttributes());
        addDataToModel(documentUploadAttribute, model, formTemplate);

        return ViewConstants.DOCUMENT_UPLOAD.asView();
    }

    private FormTemplateApi getFormTemplateApi(final String formType) {
        ApiResponse<FormTemplateApi> formResponse = formTemplateService.getFormTemplate(formType);
        return formResponse.getData();
    }

    @Override
    public String process(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(ATTRIBUTE_NAME) DocumentUploadModel documentUploadAttribute, BindingResult binding, Model model,
        HttpServletRequest servletRequest, HttpSession session) {

        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
        final FormTemplateApi formTemplate = getFormTemplateApi(submissionApi.getSubmissionForm().getFormType());

        model.mergeAttributes(documentUploadAttribute.getAttributes());
        if (!verifySubmission(submissionApi)) {
            addDataToModel(documentUploadAttribute, model, formTemplate);
            return ViewConstants.ERROR.asView();
        }

        // Update the page model with the database document's id and details
        documentUploadAttribute.setSubmissionId(submissionApi.getId());
        documentUploadAttribute.setDetails(new FileListApi());
        documentUploadAttribute.addDetails(submissionApi);

        // Proceed to validate the request and upload the valid files to the server.
        final Map<String, MultipartFile> uploadedFiles = documentUploadValidator.apply(documentUploadAttribute, binding)
            .stream().collect(Collectors.toMap(file -> fileTransferApiClient.upload(file).getFileId(), file -> file));

        if (binding.hasErrors()) {
            addDataToModel(documentUploadAttribute, model, formTemplate);
            return ViewConstants.DOCUMENT_UPLOAD.asView();
        }

        // Build the FileListApi from our uploaded files.
        final List<FileApi> fileApiList = uploadedFiles.entrySet().stream().map(file ->
                new FileApi(file.getKey(), file.getValue().getOriginalFilename(), file.getValue().getSize(),
                        documentUploadAttribute.getIncorporationComponent())
        ).collect(Collectors.toList());

        // Merge existing files with the new uploaded files at a later date.
        SubmissionFormApi submissionFormApi = submissionApi.getSubmissionForm();
        if ((submissionFormApi != null) && (submissionFormApi.getFileDetails() != null)) {
            submissionFormApi.getFileDetails().getList().forEach(
                file -> fileApiList.add(new FileApi(file.getFileId(), file.getFileName(), file.getFileSize(),
                        file.getIncorporationComponent()))
            );
        }

        // Call the API layer to persist the updated evidence.
        ApiResponse<SubmissionResponseApi> response = apiClientService.putFileList(submissionApi.getId(),
            new FileListApi(fileApiList));
        logApiResponse(response, submissionApi.getId(), "Uploaded Files: " + response.getData().getId());

        // Update the page model with latest uploads.
        documentUploadAttribute.setSubmissionId(submissionApi.getId());
        documentUploadAttribute.setDetails(new FileListApi(fileApiList));

        return ViewConstants.DOCUMENT_UPLOAD.asRedirectUri(chsUrl, id, companyNumber);
    }

    @Override
    @PostMapping(value = {"{id}/company/{companyNumber}/document-upload"}, params = {"action=submit"})
    public String finish(@PathVariable String id, @PathVariable String companyNumber,
        @ModelAttribute(ATTRIBUTE_NAME) DocumentUploadModel documentUploadAttribute, BindingResult binding,
        Model model, HttpServletRequest servletRequest, HttpSession session) {

        final FileListApi files = getDocumentUploadAttribute().getDetails();
        final SubmissionApi submissionApi = Objects.requireNonNull(getSubmission(id));
        final FormTemplateApi formTemplate = getFormTemplateApi(submissionApi.getSubmissionForm().getFormType());

        model.mergeAttributes(documentUploadAttribute.getAttributes());
        if (files == null || files.getFiles().isEmpty()) {
            String message = resourceBundle.getString("no_file_selected.documentUpload");
            binding.rejectValue("selectedFiles", "error.minimum-file-limit", message);
            addDataToModel(documentUploadAttribute, model, formTemplate);
            return ViewConstants.DOCUMENT_UPLOAD.asView();
        }

        if (formTemplate.getFormType().startsWith("SH19")) {
            return ViewConstants.SH19_DELIVERY.asRedirectUri(chsUrl, id, companyNumber);
        } else {
            return ViewConstants.CHECK_DETAILS.asRedirectUri(chsUrl, id, companyNumber);
        }
    }

    private FileListApi getUploadedFiles(final SubmissionApi submissionApi) {
        Optional<SubmissionFormApi> submissionForm = Optional.ofNullable(submissionApi.getSubmissionForm());

        // Check the submission form has been built previously, and that files are uploaded.
        if (!submissionForm.isPresent() || submissionForm.get().getFileDetails() == null) {
            return new FileListApi();
        }

        // Files have been added previously, so map them to our required model.
        List<FileApi> uploadedFiles = submissionForm.get().getFileDetails().getList().stream()
            .map(file -> new FileApi(file.getFileId(), file.getFileName(), file.getFileSize(),
                    file.getIncorporationComponent()))
            .collect(Collectors.toList());

        return new FileListApi(uploadedFiles);
    }

    private void addDataToPrepareModel(final DocumentUploadModel documentUploadAttribute,
        final SubmissionApi submissionApi, @NonNull FormTemplateApi formTemplate, final FileListApi uploadedFiles) {
        final boolean isFesEnabled = formTemplate.isFesEnabled();
        final boolean isIncorporationForm = StringUtils.equals("INC01", formTemplate.getFormCategory());

        /*
         * Have to ascertain how many uploads can be supplied as part of a submission.
         * Depending on the type of form we are using, we may need to override the basic
         * configuration to restrict it to a single file (for FES enabled forms).
         */
        Integer maximumUploadsAllowed;
        if (isFesEnabled && isIncorporationForm) {
            maximumUploadsAllowed = FILE_UPLOADS_ALLOWED_FOR_FES_ENABLED_FORMS_INCORPORATION;
        }
        else {
            maximumUploadsAllowed = isFesEnabled ? FILE_UPLOADS_ALLOWED_FOR_FES_ENABLED_FORMS :
                    fileUploadConfiguration.getMaximumFilesAllowed();
        }
        Boolean maximumUploadsLimitReached = (uploadedFiles.getFiles().size() == maximumUploadsAllowed);

        documentUploadAttribute.setSubmissionId(submissionApi.getId());
        documentUploadAttribute.setCompanyName(submissionApi.getCompany().getCompanyName());
        documentUploadAttribute.setMaximumUploadsAllowed(maximumUploadsAllowed);
        documentUploadAttribute.setMaximumUploadLimitReached(maximumUploadsLimitReached);
        documentUploadAttribute.setMaximumFileSize(fileUploadConfiguration.getMaximumFilesize());
        documentUploadAttribute.setDetails(uploadedFiles);
    }

    private void addDynamicHintText(final DocumentUploadModel documentUploadAttribute, final String formCategory) {
        documentUploadAttribute.addAttribute("showCcReminder",
            CategoryTypeConstants.nameOf(formCategory).orElse(CategoryTypeConstants.OTHER)
                == CategoryTypeConstants.CHANGE_OF_CONSTITUTION);
    }

    private void addDataToModel(@ModelAttribute(ATTRIBUTE_NAME) final DocumentUploadModel documentUploadAttribute,
        final Model model, final FormTemplateApi formTemplate) {
        model.addAttribute("allowedFileExtensions", documentUploadAttribute.getAllowedFileExtensions());
        model.addAttribute("formType", formTemplate.getFormType());
        model.addAttribute("messageTextList", formTemplate.getMessageTexts());
        logger.debug(String.format("Adding text fragments: %s", formTemplate.getMessageTexts()));
        addTrackingAttributeToModel(model);
    }

}
