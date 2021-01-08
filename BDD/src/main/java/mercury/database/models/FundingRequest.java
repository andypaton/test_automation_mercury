package mercury.database.models;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FundingRequest {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "ResourceAssignmentId")
    Integer resourceAssignmentId;

    @Column(name = "StatusId")
    Integer statusId;

    @Column(name = "RequestedAmount")
    BigDecimal requestedAmount;

    @Column(name = "Description")
    String description;

    @Column(name = "InitialRequestApproverId")
    Integer initialRequestApproverId;

    @Column(name = "AuthorisedOrRejectedById")
    Integer authorisedOrRejectedById;

    @Column(name = "Notes")
    String notes;

    @Column(name = "FundingReasonId")
    Integer fundingReasonId;

    @Column(name = "RejectionReasonId")
    Integer rejectionReasonId;

    @Column(name = "NoAmountReasonId")
    Integer noAmountReasonId;

    @Column(name = "CancellationReasonId")
    Integer cancellationReasonId;

    @Column(name = "InitialFunding")
    Boolean initialFunding;

    @Column(name = "AutomaticallyApproved")
    Boolean AutomaticallyApproved;

    @Column(name = "CreatedOn")
    Timestamp createdOn;

    @Column(name = "CreatedBy")
    String createdBy;

    @Column(name = "UpdatedOn")
    Timestamp updatedOn;

    @Column(name = "UpdatedBy")
    String updatedBy;

    @Column(name = "FundingRouteId")
    Integer fundingRouteId;

    public Integer getId() {
        return id;
    }

    public Integer getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public String getDescription() {
        return description;
    }

    public Integer getInitialRequestApproverId() {
        return initialRequestApproverId;
    }

    public Integer getAuthorisedOrRejectedById() {
        return authorisedOrRejectedById;
    }

    public String getNotes() {
        return notes;
    }

    public Integer getFundingReasonId() {
        return fundingReasonId;
    }

    public Integer getRejectionReasonId() {
        return rejectionReasonId;
    }

    public Integer getNoAmountReasonId() {
        return noAmountReasonId;
    }

    public Integer getCancellationReasonId() {
        return cancellationReasonId;
    }

    public Boolean getInitialFunding() {
        return initialFunding;
    }

    public Boolean getAutomaticallyApproved() {
        return AutomaticallyApproved;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

}
