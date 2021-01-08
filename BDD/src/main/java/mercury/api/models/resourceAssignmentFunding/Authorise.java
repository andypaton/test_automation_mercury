package mercury.api.models.resourceAssignmentFunding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "authorisedByResourceId",
    "authorisedAmount",
    "fundingReasonId",
    "notes",
    "fundingRouteId",
    "isPotentialInsurance"
})
public class Authorise extends modelBase<Authorise> {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("authorisedByResourceId")
    private Integer authorisedByResourceId;
    
    @JsonProperty("authorisedAmount")
    private Integer authorisedAmount;
    
    @JsonProperty("fundingReasonId")
    private Integer fundingReasonId;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;
    
    @JsonProperty("isPotentialInsurance")
    private String isPotentialInsurance;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public Authorise withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("authorisedByResourceId")
    public Integer getAuthorisedByResourceId() {
        return authorisedByResourceId;
    }

    @JsonProperty("authorisedByResourceId")
    public void setAuthorisedByResourceId(Integer authorisedByResourceId) {
        this.authorisedByResourceId = authorisedByResourceId;
    }

    public Authorise withAuthorisedByResourceId(Integer authorisedByResourceId) {
        this.authorisedByResourceId = authorisedByResourceId;
        return this;
    }

    @JsonProperty("authorisedAmount")
    public Integer getAuthorisedAmount() {
        return authorisedAmount;
    }

    @JsonProperty("authorisedAmount")
    public void setAuthorisedAmount(Integer authorisedAmount) {
        this.authorisedAmount = authorisedAmount;
    }

    public Authorise withAuthorisedAmount(Integer authorisedAmount) {
        this.authorisedAmount = authorisedAmount;
        return this;
    }

    @JsonProperty("fundingReasonId")
    public Integer getFundingReasonId() {
        return fundingReasonId;
    }

    @JsonProperty("fundingReasonId")
    public void setFundingReasonId(Integer fundingReasonId) {
        this.fundingReasonId = fundingReasonId;
    }

    public Authorise withFundingReasonId(Integer fundingReasonId) {
        this.fundingReasonId = fundingReasonId;
        return this;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Authorise withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @JsonProperty("fundingRouteId")
    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("fundingRouteId")
    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    public Authorise withFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
        return this;
    }

    @JsonProperty("isPotentialInsurance")
    public String getPotentialInsuranceValue() {
        return isPotentialInsurance;
    }

    @JsonProperty("isPotentialInsurance")
    public void setPotentialInsuranceValue(String potentialInsuranceValue) {
        this.isPotentialInsurance = potentialInsuranceValue;
    }

    public Authorise withPotentialInsuranceValue(String potentialInsuranceValue) {
        this.isPotentialInsurance = potentialInsuranceValue;
        return this;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("authorisedByResourceId", authorisedByResourceId).append("authorisedAmount", authorisedAmount).append("fundingReasonId", fundingReasonId).append("notes", notes).append("fundingRouteId", fundingRouteId).append("isPotentialInsurance", isPotentialInsurance).toString();
    }
}