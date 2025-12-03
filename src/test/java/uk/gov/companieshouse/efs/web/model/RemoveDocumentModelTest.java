package uk.gov.companieshouse.efs.web.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveDocumentModelTest {

    private RemoveDocumentModel removeDocumentModel;
    private RemoveDocumentModel removeDocumentModel2;

    @BeforeEach
    void setUp() {
        removeDocumentModel = new RemoveDocumentModel();
        removeDocumentModel2 = new RemoveDocumentModel();
    }

    @Test
    void constructorCopiesAllFieldsFromOriginalObject() {
        removeDocumentModel.setSubmissionId("submission1");
        removeDocumentModel.setFileId("file1");
        removeDocumentModel.setFileName("fileName1");
        removeDocumentModel.setRequired("required1");

        RemoveDocumentModel copiedModel = new RemoveDocumentModel(removeDocumentModel);

        assertEquals(removeDocumentModel.getSubmissionId(), copiedModel.getSubmissionId());
        assertEquals(removeDocumentModel.getFileId(), copiedModel.getFileId());
        assertEquals(removeDocumentModel.getFileName(), copiedModel.getFileName());
        assertEquals(removeDocumentModel.getRequired(), copiedModel.getRequired());
    }

    @Test
    void getCompanyNumberReturnsNullWhenNotSet() {
        assertNull(removeDocumentModel.getCompanyNumber());
    }

    @Test
    void getSetCompanyNumberReturnsSetValue() {
        removeDocumentModel.setCompanyNumber("12345678");
        assertEquals("12345678", removeDocumentModel.getCompanyNumber());
    }

    @Test
    void equalsReturnsTrueForEqualObjects() {
        
        removeDocumentModel.setSubmissionId("submission1");
        removeDocumentModel.setFileId("file1");
        removeDocumentModel.setFileName("fileName1");
        removeDocumentModel.setRequired("required1");

        removeDocumentModel2.setSubmissionId("submission1");
        removeDocumentModel2.setFileId("file1");
        removeDocumentModel2.setFileName("fileName1");
        removeDocumentModel2.setRequired("required1");

        assertEquals(removeDocumentModel, removeDocumentModel2);
    }

    @Test
    void equalsReturnsFalseForDifferentSubmissionIds() {
        removeDocumentModel.setSubmissionId("submission1");

        RemoveDocumentModel model2 = new RemoveDocumentModel();
        model2.setSubmissionId("submission2");

        assertNotEquals(removeDocumentModel, model2);
    }

    @Test
    void hashCodeIsConsistentWithEquals() {
        removeDocumentModel.setSubmissionId("submission1");
        removeDocumentModel.setFileId("file1");
        removeDocumentModel.setFileName("fileName1");
        removeDocumentModel.setRequired("required1");

        removeDocumentModel2.setSubmissionId("submission1");
        removeDocumentModel2.setFileId("file1");
        removeDocumentModel2.setFileName("fileName1");
        removeDocumentModel2.setRequired("required1");

        assertEquals(removeDocumentModel.hashCode(), removeDocumentModel2.hashCode());
    }

    @Test
    void toStringReturnsExpectedFormat() {
        removeDocumentModel = new RemoveDocumentModel();
        removeDocumentModel.setSubmissionId("submission1");
        removeDocumentModel.setFileId("file1");
        removeDocumentModel.setFileName("fileName1");
        removeDocumentModel.setRequired("required1");

        String expected = "RemoveDocumentModel[submissionId=submission1,fileId=file1,fileName=fileName1,required=required1]";
        assertEquals(expected, removeDocumentModel.toString());
    }
}