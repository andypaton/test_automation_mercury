package mercury.api.models.fundingrequest;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "resourceAssignmentId",
    "statusId",
    "requestedAmount",
    "description",
    "initialRequestApproverId",
    "authorisedOrRejectedById",
    "notes",
    "fundingReasonId",
    "rejectionReasonId",
    "noAmountReasonId",
    "cancellationReasonId",
    "initialFunding",
    "automaticallyApproved",
    "createdOn",
    "createdBy",
    "updatedOn",
    "updatedBy",
    "authorisedOrRejectedBy",
    "initialRequestApprover",
    "rejectionReason",
    "noAmountReason",
    "fundingReason",
    "fundingRouteId"
})
public class FundingRequest extends modelBase<FundingRequest> {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("resourceAssignmentId")
    private Integer resourceAssignmentId;
    
    @JsonProperty("statusId")
    private Integer statusId;
    
    @JsonProperty("requestedAmount")
    private Float requestedAmount;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("initialRequestApproverId")
    private Integer initialRequestApproverId;
    
    @JsonProperty("authorisedOrRejectedById")
    private Integer authorisedOrRejectedById;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("fundingReasonId")
    private Integer fundingReasonId;
    
    @JsonProperty("rejectionReasonId")
    private Object rejectionReasonId;
    
    @JsonProperty("noAmountReasonId")
    private Integer noAmountReasonId;
    
    @JsonProperty("cancellationReasonId")
    private Object cancellationReasonId;
    
    @JsonProperty("initialFunding")
    private Boolean initialFunding;
    
    @JsonProperty("automaticallyApproved")
    private Boolean automaticallyApproved;
    
    @JsonProperty("createdOn")
    private String createdOn;
    
    @JsonProperty("createdBy")
    private String createdBy;
    
    @JsonProperty("updatedOn")
    private String updatedOn;
    
    @JsonProperty("updatedBy")
    private String updatedBy;
    
    @JsonProperty("authorisedOrRejectedBy")
    private AuthorisedOrRejectedBy authorisedOrRejectedBy;
    
    @JsonProperty("initialRequestApprover")
    private InitialRequestApprover initialRequestApprover;
    
    @JsonProperty("rejectionReason")
    private Object rejectionReason;
    
    @JsonProperty("noAmountReason")
    private NoAmountReason noAmountReason;
    
    @JsonProperty("fundingReason")
    private FundingReason fundingReason;
    
    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;
    
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public FundingRequest withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("resourceAssignmentId")
    public Integer getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    @JsonProperty("resourceAssignmentId")
    public void setResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
    }

    public FundingRequest withResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
        return this;
    }

    @JsonProperty("statusId")
    public Integer getStatusId() {
        return statusId;
    }

    @JsonProperty("statusId")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public FundingRequest withStatusId(Integer statusId) {
        this.statusId = statusId;
        return this;
    }

    @JsonProperty("requestedAmount")
    public Float getRequestedAmount() {
        return requestedAmount;
    }

    @JsonProperty("requestedAmount")
    public void setRequestedAmount(Float requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public FundingRequest withRequestedAmount(Float requestedAmount) {
        this.requestedAmount = requestedAmount;
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

    public FundingRequest withDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("initialRequestApproverId")
    public Integer getInitialRequestApproverId() {
        return initialRequestApproverId;
    }

    @JsonProperty("initialRequestApproverId")
    public void setInitialRequestApproverId(Integer initialRequestApproverId) {
        this.initialRequestApproverId = initialRequestApproverId;
    }

    public FundingRequest withInitialRequestApproverId(Integer initialRequestApproverId) {
        this.initialRequestApproverId = initialRequestApproverId;
        return this;
    }

    @JsonProperty("authorisedOrRejectedById")
    public Integer getAuthorisedOrRejectedById() {
        return authorisedOrRejectedById;
    }

    @JsonProperty("authorisedOrRejectedById")
    public void setAuthorisedOrRejectedById(Integer authorisedOrRejectedById) {
        this.authorisedOrRejectedById = authorisedOrRejectedById;
    }

    public FundingRequest withAuthorisedOrRejectedById(Integer authorisedOrRejectedById) {
        this.authorisedOrRejectedById = authorisedOrRejectedById;
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

    public FundingRequest withNotes(String notes) {
        this.notes = notes;
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

    public FundingRequest withFundingReasonId(Integer fundingReasonId) {
        this.fundingReasonId = fundingReasonId;
        return this;
    }

    @JsonProperty("rejectionReasonId")
    public Object getRejectionReasonId() {
        return rejectionReasonId;
    }

    @JsonProperty("rejectionReasonId")
    public void setRejectionReasonId(Object rejectionReasonId) {
        this.rejectionReasonId = rejectionReasonId;
    }

    public FundingRequest withRejectionReasonId(Object rejectionReasonId) {
        this.rejectionReasonId = rejectionReasonId;
        return this;
    }

    @JsonProperty("noAmountReasonId")
    public Integer getNoAmountReasonId() {
        return noAmountReasonId;
    }

    @JsonProperty("noAmountReasonId")
    public void setNoAmountReasonId(Integer noAmountReasonId) {
        this.noAmountReasonId = noAmountReasonId;
    }

    public FundingRequest withNoAmountReasonId(Integer noAmountReasonId) {
        this.noAmountReasonId = noAmountReasonId;
        return this;
    }

    @JsonProperty("cancellationReasonId")
    public Object getCancellationReasonId() {
        return cancellationReasonId;
    }

    @JsonProperty("cancellationReasonId")
    public void setCancellationReasonId(Object cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
    }

    public FundingRequest withCancellationReasonId(Object cancellationReasonId) {
        this.cancellationReasonId = cancellationReasonId;
        return this;
    }

    @JsonProperty("initialFunding")
    public Boolean getInitialFunding() {
        return initialFunding;
    }

    @JsonProperty("initialFunding")
    public void setInitialFunding(Boolean initialFunding) {
        this.initialFunding = initialFunding;
    }

    public FundingRequest withInitialFunding(Boolean initialFunding) {
        this.initialFunding = initialFunding;
        return this;
    }

    @JsonProperty("automaticallyApproved")
    public Boolean getAutomaticallyApproved() {
        return automaticallyApproved;
    }

    @JsonProperty("automaticallyApproved")
    public void setAutomaticallyApproved(Boolean automaticallyApproved) {
        this.automaticallyApproved = automaticallyApproved;
    }

    public FundingRequest withAutomaticallyApproved(Boolean automaticallyApproved) {
        this.automaticallyApproved = automaticallyApproved;
        return this;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public FundingRequest withCreatedOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public FundingRequest withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @JsonProperty("updatedOn")
    public String getUpdatedOn() {
        return updatedOn;
    }

    @JsonProperty("updatedOn")
    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public FundingRequest withUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    @JsonProperty("updatedBy")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updatedBy")
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public FundingRequest withUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    @JsonProperty("authorisedOrRejectedBy")
    public AuthorisedOrRejectedBy getAuthorisedOrRejectedBy() {
        return authorisedOrRejectedBy;
    }

    @JsonProperty("authorisedOrRejectedBy")
    public void setAuthorisedOrRejectedBy(AuthorisedOrRejectedBy authorisedOrRejectedBy) {
        this.authorisedOrRejectedBy = authorisedOrRejectedBy;
    }

    public FundingRequest withAuthorisedOrRejectedBy(AuthorisedOrRejectedBy authorisedOrRejectedBy) {
        this.authorisedOrRejectedBy = authorisedOrRejectedBy;
        return this;
    }

    @JsonProperty("initialRequestApprover")
    public InitialRequestApprover getInitialRequestApprover() {
        return initialRequestApprover;
    }

    @JsonProperty("initialRequestApprover")
    public void setInitialRequestApprover(InitialRequestApprover initialRequestApprover) {
        this.initialRequestApprover = initialRequestApprover;
    }

    public FundingRequest withInitialRequestApprover(InitialRequestApprover initialRequestApprover) {
        this.initialRequestApprover = initialRequestApprover;
        return this;
    }

    @JsonProperty("rejectionReason")
    public Object getRejectionReason() {
        return rejectionReason;
    }

    @JsonProperty("rejectionReason")
    public void setRejectionReason(Object rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public FundingRequest withRejectionReason(Object rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    @JsonProperty("noAmountReason")
    public NoAmountReason getNoAmountReason() {
        return noAmountReason;
    }

    @JsonProperty("noAmountReason")
    public void setNoAmountReason(NoAmountReason noAmountReason) {
        this.noAmountReason = noAmountReason;
    }

    public FundingRequest withNoAmountReason(NoAmountReason noAmountReason) {
        this.noAmountReason = noAmountReason;
        return this;
    }

    @JsonProperty("fundingReason")
    public FundingReason getFundingReason() {
        return fundingReason;
    }

    @JsonProperty("fundingReason")
    public void setFundingReason(FundingReason fundingReason) {
        this.fundingReason = fundingReason;
    }

    public FundingRequest withFundingReason(FundingReason fundingReason) {
        this.fundingReason = fundingReason;
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

    public FundingRequest withFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("resourceAssignmentId", resourceAssignmentId).append("statusId", statusId).append("requestedAmount", requestedAmount).append("description", description).append("initialRequestApproverId", initialRequestApproverId).append("authorisedOrRejectedById", authorisedOrRejectedById).append("notes", notes).append("fundingReasonId", fundingReasonId).append("rejectionReasonId", rejectionReasonId).append("noAmountReasonId", noAmountReasonId).append("cancellationReasonId", cancellationReasonId).append("initialFunding", initialFunding).append("automaticallyApproved", automaticallyApproved).append("createdOn", createdOn).append("createdBy", createdBy).append("updatedOn", updatedOn).append("updatedBy", updatedBy).append("authorisedOrRejectedBy", authorisedOrRejectedBy).append("initialRequestApprover", initialRequestApprover).append("rejectionReason", rejectionReason).append("noAmountReason", noAmountReason).append("fundingReason", fundingReason).append("fundingRouteId", fundingRouteId).toString();
    }

}