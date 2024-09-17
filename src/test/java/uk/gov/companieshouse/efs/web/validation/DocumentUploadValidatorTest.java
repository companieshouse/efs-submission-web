package uk.gov.companieshouse.efs.web.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.api.model.efs.submissions.FileApi;
import uk.gov.companieshouse.api.model.efs.submissions.FileListApi;
import uk.gov.companieshouse.efs.web.configuration.FileUploadConfiguration;
import uk.gov.companieshouse.efs.web.model.DocumentUploadModel;

@ExtendWith(MockitoExtension.class)
class DocumentUploadValidatorTest {

    private static final Integer MINIMUM_UPLOADS_ALLOWED    = 1;
    private static final Integer MAXIMUM_UPLOADS_ALLOWED    = 10;

    private static final String PDF_VALUE = "PDF";
    public static final List<FileUploadConfiguration.FileType> ALLOWED_TYPES = Collections.singletonList(
            (new FileUploadConfiguration.FileType(APPLICATION_PDF_VALUE, Collections.singletonList(PDF_VALUE))));
    private static final int KILOBYTE = 1024;

    @Mock
    private FileUploadConfiguration fileUploadConfiguration;

    @Mock
    private ResourceBundle resourceBundle;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private DocumentUploadValidator toTest;

    @BeforeEach
    public void start() {
        MockitoAnnotations.openMocks(this);

        //ReflectionTestUtils.setField(toTest, "bundle", resourceBundle);
    }

    @AfterEach
    public void finish() {

    }

    private List<MultipartFile> createUploads(Integer filesToCreate, MediaType mediaType) {
        return createUploads(filesToCreate, mediaType, null);
    }

    private List<MultipartFile> createUploads(Integer filesToCreate, MediaType mediaType, String data) {
        final List<MultipartFile> uploads = new ArrayList<>();

        IntStream.rangeClosed(1, filesToCreate).boxed().forEach(index -> {
            String fileSuffix = "txt";
            String contentType = TEXT_PLAIN_VALUE;

            if(mediaType == APPLICATION_PDF) {
                fileSuffix = "pdf";
                contentType = APPLICATION_PDF_VALUE;
            }

            String name = String.format("%s-file-%d", fileSuffix, index);
            String originalFilename = String.format("test-file-%d.%s", index, fileSuffix);
            String content = Optional.ofNullable(data).orElse("This is my test data.");

            uploads.add(new MockMultipartFile(name, originalFilename, contentType, content.getBytes()));
        });

        return uploads;
    }

    private List<MultipartFile> createDuplicateUploads(Integer filesToCreate) {
        final List<MultipartFile> uploads = new ArrayList<>();
        IntStream.rangeClosed(1, filesToCreate).boxed().forEach(index -> {
            String name = String.format("uploaded-file-%d", index);
            String originalFilename = String.format("uploaded-file-%d.pdf", index);
            String content = "This is my duplicate file test data.";

            uploads.add(new MockMultipartFile(name, originalFilename, APPLICATION_PDF_VALUE, content.getBytes()));
        });
        return uploads;
    }

    private FileListApi createFiles(final Integer filesToCreate) {
        final List<FileApi> files = new ArrayList<>();

        IntStream.rangeClosed(1, filesToCreate).boxed().forEach(index -> {
            String fileId = UUID.randomUUID().toString();

            files.add(new FileApi(fileId, String.format("uploaded-file-%d.pdf", index), 100L));
        });

        return new FileListApi(files);
    }

    private String createContent(final Integer sizeInBytes) {
        byte[] buffer = new byte[sizeInBytes];
        Arrays.fill(buffer, (byte) 1);

        return new String(buffer);
    }

    @Test
    void testValidateWithNoFilesSelected() {
        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setSelectedFiles(new ArrayList<>());

        List<MultipartFile> validFiles = toTest.apply(model, bindingResult);

        assertThat(validFiles.size(), is(0));
        assertThat(bindingResult.hasErrors(), is(Boolean.FALSE));
    }

    @Test
    void testMaximumFilesAlreadyUploaded() {
        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(resourceBundle.getString("max_files_exceeded.documentUpload"))
                .thenReturn("The selected file could not be uploaded. You can only attach up to {1} files");

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MAXIMUM_UPLOADS_ALLOWED));
        final int filesToCreate = 2;
        model.setSelectedFiles(createUploads(filesToCreate, APPLICATION_PDF));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(filesToCreate));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("The selected file could not be uploaded. You can only attach up to 10 files"));
    }

    @Test
    void testMimeTypeNotSupportedWhenPdfOnly() {
        Set<String> allowedExtensions = new TreeSet<>();
        allowedExtensions.add("PDF");

        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(fileUploadConfiguration.getDistinctExtensions()).thenReturn(allowedExtensions);
        when(resourceBundle.getString("invalid_file_type.documentUpload"))
                .thenReturn("The selected file, {0}, must be {1}");

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createUploads(1, TEXT_PLAIN));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(1));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("The selected file, test-file-1.txt, must be PDF"));
    }

    @Test
    void testMimeTypeNotSupportedWhenMultiple() {
        Set<String> allowedExtensions = new TreeSet<>();
        allowedExtensions.add("PDF");
        allowedExtensions.add("JPG");
        allowedExtensions.add("JPEG");

        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(fileUploadConfiguration.getDistinctExtensions()).thenReturn(allowedExtensions);
        when(resourceBundle.getString("invalid_file_type.documentUpload"))
                .thenReturn("The selected file, {0}, must be {1}");

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createUploads(1, TEXT_PLAIN));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(1));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("The selected file, test-file-1.txt, must be JPEG, JPG or PDF"));
    }

    @Test
    void testFileUploadHasNoContent() {
        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(fileUploadConfiguration.getDistinctMimeTypes()).thenReturn(
                ALLOWED_TYPES.stream().map(FileUploadConfiguration.FileType::getMime).collect(Collectors.toSet()));
        when(resourceBundle.getString("min_file_size_exceeded.documentUpload"))
                .thenReturn("The selected file must not be empty");

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createUploads(1, APPLICATION_PDF, ""));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(1));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("The selected file must not be empty"));
    }

    @Test
    void testMaximumFileUploadSizeExceeded() {
        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(fileUploadConfiguration.getDistinctMimeTypes()).thenReturn(
                ALLOWED_TYPES.stream().map(FileUploadConfiguration.FileType::getMime).collect(Collectors.toSet()));
        when(resourceBundle.getString("max_file_size_exceeded.documentUpload"))
                .thenReturn("The selected file must be smaller than {0}");

        String fileContent = createContent(4 * KILOBYTE * KILOBYTE + 1);

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createUploads(1, APPLICATION_PDF, fileContent));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(1));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("The selected file must be smaller than 4MB"));
    }

    @ParameterizedTest(name = "max. upload limit: {0}")
    @ValueSource(strings = {"-1", "invalid"})
    void testMaximumFileUploadSizeNotExceeded(final String maxUploadLimit) {
        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn(maxUploadLimit);
        when(fileUploadConfiguration.getDistinctMimeTypes()).thenReturn(
                ALLOWED_TYPES.stream().map(FileUploadConfiguration.FileType::getMime).collect(Collectors.toSet()));

        String fileContent = createContent(4 * KILOBYTE);

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createUploads(1, APPLICATION_PDF, fileContent));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(1));
        assertThat(binding.hasErrors(), is(Boolean.FALSE));
        assertThat(binding.getErrorCount(), is(0));
    }

    @Test
    void testDuplicateFileBeingUploaded() {
        when(fileUploadConfiguration.getMaximumFilesAllowed()).thenReturn(MAXIMUM_UPLOADS_ALLOWED);
        when(fileUploadConfiguration.getMaximumFilesize()).thenReturn("4MB");
        when(fileUploadConfiguration.getDistinctMimeTypes()).thenReturn(
                ALLOWED_TYPES.stream().map(FileUploadConfiguration.FileType::getMime).collect(Collectors.toSet()));
        when(resourceBundle.getString("duplicate_file.documentUpload"))
                .thenReturn("Files must not have the same name");

        DocumentUploadModel model = new DocumentUploadModel(fileUploadConfiguration);
        model.setDetails(createFiles(MINIMUM_UPLOADS_ALLOWED));
        model.setSelectedFiles(createDuplicateUploads(1));

        BindingResult binding = new BeanPropertyBindingResult(model, "model");

        List<MultipartFile> validFiles = toTest.apply(model, binding);

        assertThat(validFiles.size(), is(0));
        assertThat(binding.hasErrors(), is(Boolean.TRUE));
        assertThat(binding.getErrorCount(), is(1));

        FieldError fieldError = binding.getFieldError("selectedFiles");
        assert fieldError != null;
        assertThat(fieldError.getDefaultMessage(),
                is("Files must not have the same name"));
    }
}
