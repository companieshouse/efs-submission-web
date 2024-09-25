package uk.gov.companieshouse.efs.web.transfer;

import org.springframework.http.HttpStatusCode;

/**
 * Class representing the file transfer API client response.
 */
public class FileTransferApiClientResponse {

    private String fileId;
    private HttpStatusCode httpStatusCode;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public HttpStatusCode getHttpStatus() {
        return httpStatusCode;
    }

    public void setHttpStatus(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
