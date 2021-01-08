package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ResourceAssignment")
public class ResourceAssignment {

	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "JobId") 
    private Integer jobId;

    @Column(name = "ResourceId") 
    private Integer resourceId;

    @Column(name = "ResourceAssignmentStatusId") 
    private Integer resourceAssignmentStatusId;

    @Column(name = "ReasonId") 
    private Integer reasonId;

    @Column(name = "RootCauseId") 
    private Integer rootCauseId;

    @Column(name = "RootCauseDescription") 
    private String rootCauseDescription;

    @Column(name = "DeferralDate") 
    private java.sql.Timestamp deferralDate;

    @Column(name = "DeferralNote") 
    private String deferralNote;

    @Column(name = "DeferralTypeId") 
    private Integer deferralTypeId;

    @Column(name = "SlaTime") 
    private java.sql.Time slaTime;

    @Column(name = "EnteredRate") 
    private Double enteredRate;

    @Column(name = "NotificationSent") 
    private java.sql.Timestamp notificationSent;

    @Column(name = "ResourceAssignmentRank") 
    private Integer resourceAssignmentRank;

    @Column(name = "ResourceOverrideRequestor") 
    private String resourceOverrideRequestor;

    @Column(name = "CallbackDueAt") 
    private java.sql.Timestamp callbackDueAt;

    @Column(name = "CallbackNotes") 
    private String callbackNotes;

    @Column(name = "ResourceAssignmentSuperStatusId") 
    private Integer resourceAssignmentSuperStatusId;

    @Column(name = "ContractorReference") 
    private String contractorReference;

    @Column(name = "IncreaseCalloutFeeAmount") 
    private Double increaseCalloutFeeAmount;

    @Column(name = "IncreaseCalloutFeeReasonId") 
    private Integer increaseCalloutFeeReasonId;

    @Column(name = "IncreaseCalloutFeeNotes") 
    private String increaseCalloutFeeNotes;

    @Column(name = "CreatedOn") 
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy") 
    private String createdBy;

    @Column(name = "UpdatedOn") 
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy") 
    private String updatedBy;

    @Column(name = "Active") 
    private String active;

    @Column(name = "Reassignable") 
    private String reassignable;

    @Column(name = "AcceptanceTimeAllowance") 
    private Integer acceptanceTimeAllowance;

    @Column(name = "ResourceAssignmentCategoryId") 
    private Integer resourceAssignmentCategoryId;

    @Column(name = "IsCancelled") 
    private String isCancelled;

    @Column(name = "SuperStateAtCancellationId") 
    private Integer superStateAtCancellationId;

    @Column(name = "StatusAtCancellationId") 
    private Integer statusAtCancellationId;



    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }


    public Integer getJobId() {
      return jobId;
    }

    public void setJobId(Integer jobId) {
      this.jobId = jobId;
    }


    public Integer getResourceId() {
      return resourceId;
    }

    public void setResourceId(Integer resourceId) {
      this.resourceId = resourceId;
    }


    public Integer getResourceAssignmentStatusId() {
      return resourceAssignmentStatusId;
    }

    public void setResourceAssignmentStatusId(Integer resourceAssignmentStatusId) {
      this.resourceAssignmentStatusId = resourceAssignmentStatusId;
    }


    public Integer getReasonId() {
      return reasonId;
    }

    public void setReasonId(Integer reasonId) {
      this.reasonId = reasonId;
    }


    public Integer getRootCauseId() {
      return rootCauseId;
    }

    public void setRootCauseId(Integer rootCauseId) {
      this.rootCauseId = rootCauseId;
    }


    public String getRootCauseDescription() {
      return rootCauseDescription;
    }

    public void setRootCauseDescription(String rootCauseDescription) {
      this.rootCauseDescription = rootCauseDescription;
    }


    public java.sql.Timestamp getDeferralDate() {
      return deferralDate;
    }

    public void setDeferralDate(java.sql.Timestamp deferralDate) {
      this.deferralDate = deferralDate;
    }


    public String getDeferralNote() {
      return deferralNote;
    }

    public void setDeferralNote(String deferralNote) {
      this.deferralNote = deferralNote;
    }


    public Integer getDeferralTypeId() {
      return deferralTypeId;
    }

    public void setDeferralTypeId(Integer deferralTypeId) {
      this.deferralTypeId = deferralTypeId;
    }


    public java.sql.Time getSlaTime() {
      return slaTime;
    }

    public void setSlaTime(java.sql.Time slaTime) {
      this.slaTime = slaTime;
    }


    public Double getEnteredRate() {
      return enteredRate;
    }

    public void setEnteredRate(Double enteredRate) {
      this.enteredRate = enteredRate;
    }


    public java.sql.Timestamp getNotificationSent() {
      return notificationSent;
    }

    public void setNotificationSent(java.sql.Timestamp notificationSent) {
      this.notificationSent = notificationSent;
    }


    public Integer getResourceAssignmentRank() {
      return resourceAssignmentRank;
    }

    public void setResourceAssignmentRank(Integer resourceAssignmentRank) {
      this.resourceAssignmentRank = resourceAssignmentRank;
    }


    public String getResourceOverrideRequestor() {
      return resourceOverrideRequestor;
    }

    public void setResourceOverrideRequestor(String resourceOverrideRequestor) {
      this.resourceOverrideRequestor = resourceOverrideRequestor;
    }


    public java.sql.Timestamp getCallbackDueAt() {
      return callbackDueAt;
    }

    public void setCallbackDueAt(java.sql.Timestamp callbackDueAt) {
      this.callbackDueAt = callbackDueAt;
    }


    public String getCallbackNotes() {
      return callbackNotes;
    }

    public void setCallbackNotes(String callbackNotes) {
      this.callbackNotes = callbackNotes;
    }


    public Integer getResourceAssignmentSuperStatusId() {
      return resourceAssignmentSuperStatusId;
    }

    public void setResourceAssignmentSuperStatusId(Integer resourceAssignmentSuperStatusId) {
      this.resourceAssignmentSuperStatusId = resourceAssignmentSuperStatusId;
    }


    public String getContractorReference() {
      return contractorReference;
    }

    public void setContractorReference(String contractorReference) {
      this.contractorReference = contractorReference;
    }


    public Double getIncreaseCalloutFeeAmount() {
      return increaseCalloutFeeAmount;
    }

    public void setIncreaseCalloutFeeAmount(Double increaseCalloutFeeAmount) {
      this.increaseCalloutFeeAmount = increaseCalloutFeeAmount;
    }


    public Integer getIncreaseCalloutFeeReasonId() {
      return increaseCalloutFeeReasonId;
    }

    public void setIncreaseCalloutFeeReasonId(Integer increaseCalloutFeeReasonId) {
      this.increaseCalloutFeeReasonId = increaseCalloutFeeReasonId;
    }


    public String getIncreaseCalloutFeeNotes() {
      return increaseCalloutFeeNotes;
    }

    public void setIncreaseCalloutFeeNotes(String increaseCalloutFeeNotes) {
      this.increaseCalloutFeeNotes = increaseCalloutFeeNotes;
    }


    public java.sql.Timestamp getCreatedOn() {
      return createdOn;
    }

    public void setCreatedOn(java.sql.Timestamp createdOn) {
      this.createdOn = createdOn;
    }


    public String getCreatedBy() {
      return createdBy;
    }

    public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
    }


    public java.sql.Timestamp getUpdatedOn() {
      return updatedOn;
    }

    public void setUpdatedOn(java.sql.Timestamp updatedOn) {
      this.updatedOn = updatedOn;
    }


    public String getUpdatedBy() {
      return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
      this.updatedBy = updatedBy;
    }


    public String getActive() {
      return active;
    }

    public void setActive(String active) {
      this.active = active;
    }


    public String getReassignable() {
      return reassignable;
    }

    public void setReassignable(String reassignable) {
      this.reassignable = reassignable;
    }


    public Integer getAcceptanceTimeAllowance() {
      return acceptanceTimeAllowance;
    }

    public void setAcceptanceTimeAllowance(Integer acceptanceTimeAllowance) {
      this.acceptanceTimeAllowance = acceptanceTimeAllowance;
    }


    public Integer getResourceAssignmentCategoryId() {
      return resourceAssignmentCategoryId;
    }

    public void setResourceAssignmentCategoryId(Integer resourceAssignmentCategoryId) {
      this.resourceAssignmentCategoryId = resourceAssignmentCategoryId;
    }


    public String getIsCancelled() {
      return isCancelled;
    }

    public void setIsCancelled(String isCancelled) {
      this.isCancelled = isCancelled;
    }


    public Integer getSuperStateAtCancellationId() {
      return superStateAtCancellationId;
    }

    public void setSuperStateAtCancellationId(Integer superStateAtCancellationId) {
      this.superStateAtCancellationId = superStateAtCancellationId;
    }


    public Integer getStatusAtCancellationId() {
      return statusAtCancellationId;
    }

    public void setStatusAtCancellationId(Integer statusAtCancellationId) {
      this.statusAtCancellationId = statusAtCancellationId;
    }

}
