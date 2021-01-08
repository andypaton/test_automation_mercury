package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "description",
    "quoteLineTypeDescription",
    "partNumber",
    "quantity",
    "unitPrice",
    "lineValue"
})
public class QuoteLine extends modelBase<QuoteLine>  {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("quoteLineTypeDescription")
    private String quoteLineTypeDescription;
    
    @JsonProperty("partNumber")
    private Object partNumber;
    
    @JsonProperty("quantity")
    private Float quantity;
    
    @JsonProperty("unitPrice")
    private Float unitPrice;
    
    @JsonProperty("lineValue")
    private Float lineValue;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public QuoteLine withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    public QuoteLine withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("quoteLineTypeDescription")
    public String getQuoteLineTypeDescription() {
        return quoteLineTypeDescription;
    }

    @JsonProperty("quoteLineTypeDescription")
    public void setQuoteLineTypeDescription(String quoteLineTypeDescription) {
        this.quoteLineTypeDescription = quoteLineTypeDescription;
    }

    public QuoteLine withQuoteLineTypeDescription(String quoteLineTypeDescription) {
        this.quoteLineTypeDescription = quoteLineTypeDescription;
        return this;
    }

    @JsonProperty("partNumber")
    public Object getPartNumber() {
        return partNumber;
    }

    @JsonProperty("partNumber")
    public void setPartNumber(Object partNumber) {
        this.partNumber = partNumber;
    }

    public QuoteLine withPartNumber(Object partNumber) {
        this.partNumber = partNumber;
        return this;
    }

    @JsonProperty("quantity")
    public Float getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public QuoteLine withQuantity(Float quantity) {
        this.quantity = quantity;
        return this;
    }

    @JsonProperty("unitPrice")
    public Float getUnitPrice() {
        return unitPrice;
    }

    @JsonProperty("unitPrice")
    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public QuoteLine withUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    @JsonProperty("lineValue")
    public Float getLineValue() {
        return lineValue;
    }

    @JsonProperty("lineValue")
    public void setLineValue(Float lineValue) {
        this.lineValue = lineValue;
    }

    public QuoteLine withLineValue(Float lineValue) {
        this.lineValue = lineValue;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("description", description).append("quoteLineTypeDescription", quoteLineTypeDescription).append("partNumber", partNumber).append("quantity", quantity).append("unitPrice", unitPrice).append("lineValue", lineValue).toString();
    }

}
