package mercury.api.models.portal.quoteLine;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Id",
    "JobRef",
    "ProjectQuoteId",
    "QuoteLineTypeValue",
    "PartNumber",
    "Description",
    "Quantity",
    "UnitPrice",
    "QuoteType",
    "PartAndPriceInPriceBook",
    "SupplierCode",
    "HelpdekResourceProfileId",
    "HelpdekResourceProfileLabourRateTypeId",
    "__RequestVerificationToken"
})
public class QuoteLine extends modelBase<QuoteLine>{

    @JsonProperty("Id")
    private String id;

    @JsonProperty("JobRef")
    private String jobRef;

    @JsonProperty("ProjectQuoteId")
    private String projectQuoteId;

    @JsonProperty("QuoteLineTypeValue")
    private String quoteLineTypeValue;

    @JsonProperty("PartNumber")
    private String partNumber;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("Quantity")
    private String quantity;

    @JsonProperty("UnitPrice")
    private String unitPrice;

    @JsonProperty("QuoteType")
    private String quoteType;

    @JsonProperty("PartAndPriceInPriceBook")
    private String partAndPriceInPriceBook;

    @JsonProperty("SupplierCode")
    private String supplierCode;

    @JsonProperty("HelpdekResourceProfileId")
    private String helpdekResourceProfileId;

    @JsonProperty("HelpdekResourceProfileLabourRateTypeId")
    private String helpdekResourceProfileLabourRateTypeId;

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    public QuoteLine withId(String id) {
        this.id = id;
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

    public QuoteLine withJobRef(String jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("ProjectQuoteId")
    public String getProjectQuoteId() {
        return projectQuoteId;
    }

    @JsonProperty("ProjectQuoteId")
    public void setProjectQuoteId(String projectQuoteId) {
        this.projectQuoteId = projectQuoteId;
    }

    public QuoteLine withProjectQuoteId(String projectQuoteId) {
        this.projectQuoteId = projectQuoteId;
        return this;
    }

    @JsonProperty("QuoteLineTypeValue")
    public String getQuoteLineTypeValue() {
        return quoteLineTypeValue;
    }

    @JsonProperty("QuoteLineTypeValue")
    public void setQuoteLineTypeValue(String quoteLineTypeValue) {
        this.quoteLineTypeValue = quoteLineTypeValue;
    }

    public QuoteLine withQuoteLineTypeValue(String quoteLineTypeValue) {
        this.quoteLineTypeValue = quoteLineTypeValue;
        return this;
    }

    @JsonProperty("PartNumber")
    public String getPartNumber() {
        return partNumber;
    }

    @JsonProperty("PartNumber")
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public QuoteLine withPartNumber(String partNumber) {
        this.partNumber = partNumber;
        return this;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("Description")
    public void setDescription(String description) {
        this.description = description;
    }

    public QuoteLine withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("Quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("Quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public QuoteLine withQuantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    @JsonProperty("UnitPrice")
    public String getUnitPrice() {
        return unitPrice;
    }

    @JsonProperty("UnitPrice")
    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public QuoteLine withUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    @JsonProperty("QuoteType")
    public String getQuoteType() {
        return quoteType;
    }

    @JsonProperty("QuoteType")
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public QuoteLine withQuoteType(String quoteType) {
        this.quoteType = quoteType;
        return this;
    }

    @JsonProperty("PartAndPriceInPriceBook")
    public String getPartAndPriceInPriceBook() {
        return partAndPriceInPriceBook;
    }

    @JsonProperty("PartAndPriceInPriceBook")
    public void setPartAndPriceInPriceBook(String partAndPriceInPriceBook) {
        this.partAndPriceInPriceBook = partAndPriceInPriceBook;
    }

    public QuoteLine withPartAndPriceInPriceBook(String partAndPriceInPriceBook) {
        this.partAndPriceInPriceBook = partAndPriceInPriceBook;
        return this;
    }

    @JsonProperty("SupplierCode")
    public String getSupplierCode() {
        return supplierCode;
    }

    @JsonProperty("SupplierCode")
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public QuoteLine withSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
        return this;
    }

    @JsonProperty("HelpdekResourceProfileId")
    public String getHelpdekResourceProfileId() {
        return helpdekResourceProfileId;
    }

    @JsonProperty("HelpdekResourceProfileId")
    public void setHelpdekResourceProfileId(String helpdekResourceProfileId) {
        this.helpdekResourceProfileId = helpdekResourceProfileId;
    }

    public QuoteLine withHelpdekResourceProfileId(String helpdekResourceProfileId) {
        this.helpdekResourceProfileId = helpdekResourceProfileId;
        return this;
    }

    @JsonProperty("HelpdekResourceProfileLabourRateTypeId")
    public String getHelpdekResourceProfileLabourRateTypeId() {
        return helpdekResourceProfileLabourRateTypeId;
    }

    @JsonProperty("HelpdekResourceProfileLabourRateTypeId")
    public void setHelpdekResourceProfileLabourRateTypeId(String helpdekResourceProfileLabourRateTypeId) {
        this.helpdekResourceProfileLabourRateTypeId = helpdekResourceProfileLabourRateTypeId;
    }

    public QuoteLine withHelpdekResourceProfileLabourRateTypeId(String helpdekResourceProfileLabourRateTypeId) {
        this.helpdekResourceProfileLabourRateTypeId = helpdekResourceProfileLabourRateTypeId;
        return this;
    }

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    public QuoteLine withRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("jobRef", jobRef).append("projectQuoteId", projectQuoteId).append("quoteLineTypeValue", quoteLineTypeValue).append("partNumber", partNumber).append("description", description).append("quantity", quantity).append("unitPrice", unitPrice).append("quoteType", quoteType).append("partAndPriceInPriceBook", partAndPriceInPriceBook).append("supplierCode", supplierCode).append("helpdekResourceProfileId", helpdekResourceProfileId).append("helpdekResourceProfileLabourRateTypeId", helpdekResourceProfileLabourRateTypeId).append("requestVerificationToken", requestVerificationToken).toString();
    }
}
