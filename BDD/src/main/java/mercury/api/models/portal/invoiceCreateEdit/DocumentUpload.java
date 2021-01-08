package mercury.api.models.portal.invoiceCreateEdit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__RequestVerificationToken",
    "WorkOrderRef",
    "JobSheetFilePath",
    "InvoiceFilePath",
    "WorkOrderType",
    "JobRef"
})
@Component
public class DocumentUpload extends modelBase<DocumentUpload> {

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("WorkOrderRef")
    private String workOrderRef;

    @JsonProperty("JobSheetFilePath")
    private String jobSheetFilePath;

    @JsonProperty("InvoiceFilePath")
    private String invoiceFilePath;

    @JsonProperty("WorkOrderType")
    private String workOrderType;

    @JsonProperty("JobRef")
    private String jobRef;

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

    @JsonProperty("WorkOrderRef")
    public String getWorkOrderRef() {
        return workOrderRef;
    }

    @JsonProperty("WorkOrderRef")
    public void setWorkOrderRef(String workOrderRef) {
        this.workOrderRef = workOrderRef;
    }

    public DocumentUpload withWorkOrderRef(String workOrderRef) {
        this.workOrderRef = workOrderRef;
        return this;
    }

    @JsonProperty("JobSheetFilePath")
    public String getJobSheetFilePath() {
        return jobSheetFilePath;
    }

    @JsonProperty("JobSheetFilePath")
    public void setJobSheetFilePath(String jobSheetFilePath) {
        this.jobSheetFilePath = jobSheetFilePath;
    }

    public DocumentUpload withJobSheetFilePath(String jobSheetFilePath) {
        this.jobSheetFilePath = jobSheetFilePath;
        return this;
    }

    @JsonProperty("InvoiceFilePath")
    public String getInvoiceFilePath() {
        return invoiceFilePath;
    }

    @JsonProperty("InvoiceFilePath")
    public void setInvoiceFilePath(String invoiceFilePath) {
        this.invoiceFilePath = invoiceFilePath;
    }

    public DocumentUpload withInvoiceFilePath(String invoiceFilePath) {
        this.invoiceFilePath = invoiceFilePath;
        return this;
    }

    @JsonProperty("WorkOrderType")
    public String getWorkOrderType() {
        return workOrderType;
    }

    @JsonProperty("WorkOrderType")
    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    public DocumentUpload withWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken).append("workOrderRef", workOrderRef).append("jobSheetFilePath", jobSheetFilePath).append("invoiceFilePath", invoiceFilePath).append("workOrderType", workOrderType).append("jobRef", jobRef).toString();
    }
}