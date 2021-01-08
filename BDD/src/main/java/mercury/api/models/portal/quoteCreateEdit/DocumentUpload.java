package mercury.api.models.portal.quoteCreateEdit;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__RequestVerificationToken",
    "UploadType",
    "UploadRef",
    "DocumentRef",
    "JobRef",
    "ValidFileExtensions"
})
public class DocumentUpload extends modelBase<DocumentUpload> {

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("UploadType")
    private String uploadType;

    @JsonProperty("UploadRef")
    private String uploadRef;

    @JsonProperty("DocumentRef")
    private String documentRef;

    @JsonProperty("JobRef")
    private String jobRef;

    @JsonProperty("ValidFileExtensions")
    private List<String> validFileExtensions = null;

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    public DocumentUpload withRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
        return this;
    }

    @JsonProperty("UploadType")
    public String getUploadType() {
        return uploadType;
    }

    @JsonProperty("UploadType")
    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public DocumentUpload withUploadType(String uploadType) {
        this.uploadType = uploadType;
        return this;
    }

    @JsonProperty("UploadRef")
    public String getUploadRef() {
        return uploadRef;
    }

    @JsonProperty("UploadRef")
    public void setUploadRef(String uploadRef) {
        this.uploadRef = uploadRef;
    }

    public DocumentUpload withUploadRef(String uploadRef) {
        this.uploadRef = uploadRef;
        return this;
    }

    @JsonProperty("DocumentRef")
    public String getDocumentRef() {
        return documentRef;
    }

    @JsonProperty("DocumentRef")
    public void setDocumentRef(String documentRef) {
        this.documentRef = documentRef;
    }

    public DocumentUpload withDocumentRef(String documentRef) {
        this.documentRef = documentRef;
        return this;
    }

    @JsonProperty("JobRef")
    public String getJobRef() {
        return jobRef;
    }

    @JsonProperty("JobRef")
    public void setJobRef(String jobRef) {
        this.jobRef = jobRef;
    }

    public DocumentUpload withJobRef(String jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("ValidFileExtensions")
    public List<String> getValidFileExtensions() {
        return validFileExtensions;
    }

    @JsonProperty("ValidFileExtensions")
    public void setValidFileExtensions(List<String> validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
    }

    public DocumentUpload withValidFileExtensions(List<String> validFileExtensions) {
        this.validFileExtensions = validFileExtensions;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken).append("uploadType", uploadType).append("uploadRef", uploadRef).append("documentRef", documentRef).append("jobRef", jobRef).append("validFileExtensions", validFileExtensions).toString();
    }

}