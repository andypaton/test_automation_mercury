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
    "IsQueried",
    "InvoiceHeader.Id",
})
@Component
public class InvoiceSubmission extends modelBase<InvoiceSubmission> {

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("IsQueried")
    private String isQueried;
    
    @JsonProperty("InvoiceHeader.Id")
    private String invoiceHeaderId;


    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    public InvoiceSubmission withRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
        return this;
    }

    @JsonProperty("IsQueried")
    public String getIsQueried() {
        return isQueried;
    }

    @JsonProperty("IsQueried")
    public void setIsQueried(String isQueried) {
        this.isQueried = isQueried;
    }

    public InvoiceSubmission withIsQueried(String isQueried) {
        this.isQueried = isQueried;
        return this;
    }
    
    @JsonProperty("InvoiceHeader.Id")
    public String getInvoiceHeaderId() {
        return invoiceHeaderId;
    }

    @JsonProperty("InvoiceHeader.Id")
    public void setInvoiceHeaderId(String invoiceHeaderId) {
        this.invoiceHeaderId = invoiceHeaderId;
    }

    public InvoiceSubmission withInvoiceHeaderId(String invoiceHeaderId) {
        this.invoiceHeaderId = invoiceHeaderId;
        return this;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken).append("isQueried", isQueried).append("invoiceHeaderId", invoiceHeaderId).toString();
    }
}