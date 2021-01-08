package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Message")
public class Message {

    @Id
	@Column(name = "Id") 
    private Integer id;

    @Column(name = "SyncStatus") 
    private Integer syncStatus;

    @Column(name = "Entity") 
    private String entity;

    @Column(name = "SubEntity") 
    private String subEntity;

    @Column(name = "EventType") 
    private String eventType;

    @Column(name = "RoutingKey") 
    private String routingKey;

    @Column(name = "JobId") 
    private Integer jobId;

    @Column(name = "SiteId") 
    private Integer siteId;

    @Column(name = "JobReference") 
    private Integer jobReference;

    @Column(name = "JobStatusId") 
    private Integer jobStatusId;

    @Column(name = "AssetId") 
    private Integer assetId;

    @Column(name = "OriginalAssetId") 
    private Integer originalAssetId;

    @Column(name = "AssetClassificationId") 
    private Integer assetClassificationId;

    @Column(name = "CallerId") 
    private Integer callerId;

    @Column(name = "JobTypeId") 
    private Integer jobTypeId;

    @Column(name = "Description") 
    private String description;

    @Column(name = "FaultTypeId") 
    private Integer faultTypeId;

    @Column(name = "FaultPriorityId") 
    private Integer faultPriorityId;

    @Column(name = "LocationId") 
    private Integer locationId;

    @Column(name = "IsJobDeferred") 
    private String isJobDeferred;

    @Column(name = "IsRetrospective") 
    private String isRetrospective;

    @Column(name = "JobDeferralTypeId") 
    private Integer jobDeferralTypeId;

    @Column(name = "JobDeferralDate") 
    private java.sql.Timestamp jobDeferralDate;

    @Column(name = "JobDeferralNote") 
    private String jobDeferralNote;

    @Column(name = "JobCancellationId") 
    private Integer jobCancellationId;

    @Column(name = "DuplicateJobId") 
    private Integer duplicateJobId;

    @Column(name = "ReasonNotLoggedAgainstAssetId") 
    private Integer reasonNotLoggedAgainstAssetId;

    @Column(name = "ResourceAssignmentId") 
    private Integer resourceAssignmentId;

    @Column(name = "ResourceId") 
    private Integer resourceId;

    @Column(name = "ResourceAssignmentStatusId") 
    private Integer resourceAssignmentStatusId;

    @Column(name = "ResourceAssignmentReasonId") 
    private Integer resourceAssignmentReasonId;

    @Column(name = "RootCauseId") 
    private Integer rootCauseId;

    @Column(name = "RootCauseDescription") 
    private String rootCauseDescription;

    @Column(name = "ResourceAssignmentDeferralDate") 
    private java.sql.Timestamp resourceAssignmentDeferralDate;

    @Column(name = "ResourceAssignmentDeferralNote") 
    private String resourceAssignmentDeferralNote;

    @Column(name = "ResourceAssignmentDeferralTypeId") 
    private Integer resourceAssignmentDeferralTypeId;

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

    @Column(name = "ResourceAssignmentActive") 
    private String resourceAssignmentActive;

    @Column(name = "Reassignable") 
    private String reassignable;

    @Column(name = "EtaFrom") 
    private java.sql.Timestamp etaFrom;

    @Column(name = "EtaTo") 
    private java.sql.Timestamp etaTo;

    @Column(name = "FundingRequestId") 
    private Integer fundingRequestId;

    @Column(name = "FundingRequestStatusId") 
    private Integer fundingRequestStatusId;

    @Column(name = "FundingRequestAmount") 
    private Double fundingRequestAmount;

    @Column(name = "FundingRequestDescription") 
    private String fundingRequestDescription;

    @Column(name = "FundingRequestAuthorisedOrRejectedById") 
    private Integer fundingRequestAuthorisedOrRejectedById;

    @Column(name = "FundingRequestNotes") 
    private String fundingRequestNotes;

    @Column(name = "FundingRequestRejectionReasonId") 
    private Integer fundingRequestRejectionReasonId;

    @Column(name = "QuoteRequestId") 
    private Integer quoteRequestId;

    @Column(name = "QuoteRequestStatusId") 
    private Integer quoteRequestStatusId;

    @Column(name = "QuoteRequestAuthorisedOrRejectedById") 
    private Integer quoteRequestAuthorisedOrRejectedById;

    @Column(name = "QuoteRequestNotes") 
    private String quoteRequestNotes;

    @Column(name = "QuoteRequestRejectionReasonId") 
    private Integer quoteRequestRejectionReasonId;

    @Column(name = "JobChangedToQuoteType") 
    private String jobChangedToQuoteType;

    @Column(name = "LinkedQuoteJobReference") 
    private Integer linkedQuoteJobReference;

    @Column(name = "SiteVisitId") 
    private Integer siteVisitId;

    @Column(name = "SiteVisitWorkStartTime") 
    private java.sql.Timestamp siteVisitWorkStartTime;

    @Column(name = "SiteVisitWorkEndTime") 
    private java.sql.Timestamp siteVisitWorkEndTime;

    @Column(name = "SiteVisitTravelTime") 
    private java.sql.Time siteVisitTravelTime;

    @Column(name = "SiteVisitOverTime") 
    private java.sql.Time siteVisitOverTime;

    @Column(name = "SiteVisitTimeOnSite") 
    private java.sql.Time siteVisitTimeOnSite;

    @Column(name = "SyncErrorMessage") 
    private String syncErrorMessage;

    @Column(name = "IsSnowManagement") 
    private String isSnowManagement;

    @Column(name = "CreatedOn") 
    private java.sql.Timestamp createdOn;



    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }


    public Integer getSyncStatus() {
      return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
      this.syncStatus = syncStatus;
    }


    public String getEntity() {
      return entity;
    }

    public void setEntity(String entity) {
      this.entity = entity;
    }


    public String getSubEntity() {
      return subEntity;
    }

    public void setSubEntity(String subEntity) {
      this.subEntity = subEntity;
    }


    public String getEventType() {
      return eventType;
    }

    public void setEventType(String eventType) {
      this.eventType = eventType;
    }


    public String getRoutingKey() {
      return routingKey;
    }

    public void setRoutingKey(String routingKey) {
      this.routingKey = routingKey;
    }


    public Integer getJobId() {
      return jobId;
    }

    public void setJobId(Integer jobId) {
      this.jobId = jobId;
    }


    public Integer getSiteId() {
      return siteId;
    }

    public void setSiteId(Integer siteId) {
      this.siteId = siteId;
    }


    public Integer getJobReference() {
      return jobReference;
    }

    public void setJobReference(Integer jobReference) {
      this.jobReference = jobReference;
    }


    public Integer getJobStatusId() {
      return jobStatusId;
    }

    public void setJobStatusId(Integer jobStatusId) {
      this.jobStatusId = jobStatusId;
    }


    public Integer getAssetId() {
      return assetId;
    }

    public void setAssetId(Integer assetId) {
      this.assetId = assetId;
    }


    public Integer getOriginalAssetId() {
      return originalAssetId;
    }

    public void setOriginalAssetId(Integer originalAssetId) {
      this.originalAssetId = originalAssetId;
    }


    public Integer getAssetClassificationId() {
      return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
      this.assetClassificationId = assetClassificationId;
    }


    public Integer getCallerId() {
      return callerId;
    }

    public void setCallerId(Integer callerId) {
      this.callerId = callerId;
    }


    public Integer getJobTypeId() {
      return jobTypeId;
    }

    public void setJobTypeId(Integer jobTypeId) {
      this.jobTypeId = jobTypeId;
    }


    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }


    public Integer getFaultTypeId() {
      return faultTypeId;
    }

    public void setFaultTypeId(Integer faultTypeId) {
      this.faultTypeId = faultTypeId;
    }


    public Integer getFaultPriorityId() {
      return faultPriorityId;
    }

    public void setFaultPriorityId(Integer faultPriorityId) {
      this.faultPriorityId = faultPriorityId;
    }


    public Integer getLocationId() {
      return locationId;
    }

    public void setLocationId(Integer locationId) {
      this.locationId = locationId;
    }


    public String getIsJobDeferred() {
      return isJobDeferred;
    }

    public void setIsJobDeferred(String isJobDeferred) {
      this.isJobDeferred = isJobDeferred;
    }


    public String getIsRetrospective() {
      return isRetrospective;
    }

    public void setIsRetrospective(String isRetrospective) {
      this.isRetrospective = isRetrospective;
    }


    public Integer getJobDeferralTypeId() {
      return jobDeferralTypeId;
    }

    public void setJobDeferralTypeId(Integer jobDeferralTypeId) {
      this.jobDeferralTypeId = jobDeferralTypeId;
    }


    public java.sql.Timestamp getJobDeferralDate() {
      return jobDeferralDate;
    }

    public void setJobDeferralDate(java.sql.Timestamp jobDeferralDate) {
      this.jobDeferralDate = jobDeferralDate;
    }


    public String getJobDeferralNote() {
      return jobDeferralNote;
    }

    public void setJobDeferralNote(String jobDeferralNote) {
      this.jobDeferralNote = jobDeferralNote;
    }


    public Integer getJobCancellationId() {
      return jobCancellationId;
    }

    public void setJobCancellationId(Integer jobCancellationId) {
      this.jobCancellationId = jobCancellationId;
    }


    public Integer getDuplicateJobId() {
      return duplicateJobId;
    }

    public void setDuplicateJobId(Integer duplicateJobId) {
      this.duplicateJobId = duplicateJobId;
    }


    public Integer getReasonNotLoggedAgainstAssetId() {
      return reasonNotLoggedAgainstAssetId;
    }

    public void setReasonNotLoggedAgainstAssetId(Integer reasonNotLoggedAgainstAssetId) {
      this.reasonNotLoggedAgainstAssetId = reasonNotLoggedAgainstAssetId;
    }


    public Integer getResourceAssignmentId() {
      return resourceAssignmentId;
    }

    public void setResourceAssignmentId(Integer resourceAssignmentId) {
      this.resourceAssignmentId = resourceAssignmentId;
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


    public Integer getResourceAssignmentReasonId() {
      return resourceAssignmentReasonId;
    }

    public void setResourceAssignmentReasonId(Integer resourceAssignmentReasonId) {
      this.resourceAssignmentReasonId = resourceAssignmentReasonId;
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


    public java.sql.Timestamp getResourceAssignmentDeferralDate() {
      return resourceAssignmentDeferralDate;
    }

    public void setResourceAssignmentDeferralDate(java.sql.Timestamp resourceAssignmentDeferralDate) {
      this.resourceAssignmentDeferralDate = resourceAssignmentDeferralDate;
    }


    public String getResourceAssignmentDeferralNote() {
      return resourceAssignmentDeferralNote;
    }

    public void setResourceAssignmentDeferralNote(String resourceAssignmentDeferralNote) {
      this.resourceAssignmentDeferralNote = resourceAssignmentDeferralNote;
    }


    public Integer getResourceAssignmentDeferralTypeId() {
      return resourceAssignmentDeferralTypeId;
    }

    public void setResourceAssignmentDeferralTypeId(Integer resourceAssignmentDeferralTypeId) {
      this.resourceAssignmentDeferralTypeId = resourceAssignmentDeferralTypeId;
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


    public String getResourceAssignmentActive() {
      return resourceAssignmentActive;
    }

    public void setResourceAssignmentActive(String resourceAssignmentActive) {
      this.resourceAssignmentActive = resourceAssignmentActive;
    }


    public String getReassignable() {
      return reassignable;
    }

    public void setReassignable(String reassignable) {
      this.reassignable = reassignable;
    }


    public java.sql.Timestamp getEtaFrom() {
      return etaFrom;
    }

    public void setEtaFrom(java.sql.Timestamp etaFrom) {
      this.etaFrom = etaFrom;
    }


    public java.sql.Timestamp getEtaTo() {
      return etaTo;
    }

    public void setEtaTo(java.sql.Timestamp etaTo) {
      this.etaTo = etaTo;
    }


    public Integer getFundingRequestId() {
      return fundingRequestId;
    }

    public void setFundingRequestId(Integer fundingRequestId) {
      this.fundingRequestId = fundingRequestId;
    }


    public Integer getFundingRequestStatusId() {
      return fundingRequestStatusId;
    }

    public void setFundingRequestStatusId(Integer fundingRequestStatusId) {
      this.fundingRequestStatusId = fundingRequestStatusId;
    }


    public Double getFundingRequestAmount() {
      return fundingRequestAmount;
    }

    public void setFundingRequestAmount(Double fundingRequestAmount) {
      this.fundingRequestAmount = fundingRequestAmount;
    }


    public String getFundingRequestDescription() {
      return fundingRequestDescription;
    }

    public void setFundingRequestDescription(String fundingRequestDescription) {
      this.fundingRequestDescription = fundingRequestDescription;
    }


    public Integer getFundingRequestAuthorisedOrRejectedById() {
      return fundingRequestAuthorisedOrRejectedById;
    }

    public void setFundingRequestAuthorisedOrRejectedById(Integer fundingRequestAuthorisedOrRejectedById) {
      this.fundingRequestAuthorisedOrRejectedById = fundingRequestAuthorisedOrRejectedById;
    }


    public String getFundingRequestNotes() {
      return fundingRequestNotes;
    }

    public void setFundingRequestNotes(String fundingRequestNotes) {
      this.fundingRequestNotes = fundingRequestNotes;
    }


    public Integer getFundingRequestRejectionReasonId() {
      return fundingRequestRejectionReasonId;
    }

    public void setFundingRequestRejectionReasonId(Integer fundingRequestRejectionReasonId) {
      this.fundingRequestRejectionReasonId = fundingRequestRejectionReasonId;
    }


    public Integer getQuoteRequestId() {
      return quoteRequestId;
    }

    public void setQuoteRequestId(Integer quoteRequestId) {
      this.quoteRequestId = quoteRequestId;
    }


    public Integer getQuoteRequestStatusId() {
      return quoteRequestStatusId;
    }

    public void setQuoteRequestStatusId(Integer quoteRequestStatusId) {
      this.quoteRequestStatusId = quoteRequestStatusId;
    }


    public Integer getQuoteRequestAuthorisedOrRejectedById() {
      return quoteRequestAuthorisedOrRejectedById;
    }

    public void setQuoteRequestAuthorisedOrRejectedById(Integer quoteRequestAuthorisedOrRejectedById) {
      this.quoteRequestAuthorisedOrRejectedById = quoteRequestAuthorisedOrRejectedById;
    }


    public String getQuoteRequestNotes() {
      return quoteRequestNotes;
    }

    public void setQuoteRequestNotes(String quoteRequestNotes) {
      this.quoteRequestNotes = quoteRequestNotes;
    }


    public Integer getQuoteRequestRejectionReasonId() {
      return quoteRequestRejectionReasonId;
    }

    public void setQuoteRequestRejectionReasonId(Integer quoteRequestRejectionReasonId) {
      this.quoteRequestRejectionReasonId = quoteRequestRejectionReasonId;
    }


    public String getJobChangedToQuoteType() {
      return jobChangedToQuoteType;
    }

    public void setJobChangedToQuoteType(String jobChangedToQuoteType) {
      this.jobChangedToQuoteType = jobChangedToQuoteType;
    }


    public Integer getLinkedQuoteJobReference() {
      return linkedQuoteJobReference;
    }

    public void setLinkedQuoteJobReference(Integer linkedQuoteJobReference) {
      this.linkedQuoteJobReference = linkedQuoteJobReference;
    }


    public Integer getSiteVisitId() {
      return siteVisitId;
    }

    public void setSiteVisitId(Integer siteVisitId) {
      this.siteVisitId = siteVisitId;
    }


    public java.sql.Timestamp getSiteVisitWorkStartTime() {
      return siteVisitWorkStartTime;
    }

    public void setSiteVisitWorkStartTime(java.sql.Timestamp siteVisitWorkStartTime) {
      this.siteVisitWorkStartTime = siteVisitWorkStartTime;
    }


    public java.sql.Timestamp getSiteVisitWorkEndTime() {
      return siteVisitWorkEndTime;
    }

    public void setSiteVisitWorkEndTime(java.sql.Timestamp siteVisitWorkEndTime) {
      this.siteVisitWorkEndTime = siteVisitWorkEndTime;
    }


    public java.sql.Time getSiteVisitTravelTime() {
      return siteVisitTravelTime;
    }

    public void setSiteVisitTravelTime(java.sql.Time siteVisitTravelTime) {
      this.siteVisitTravelTime = siteVisitTravelTime;
    }


    public java.sql.Time getSiteVisitOverTime() {
      return siteVisitOverTime;
    }

    public void setSiteVisitOverTime(java.sql.Time siteVisitOverTime) {
      this.siteVisitOverTime = siteVisitOverTime;
    }


    public java.sql.Time getSiteVisitTimeOnSite() {
      return siteVisitTimeOnSite;
    }

    public void setSiteVisitTimeOnSite(java.sql.Time siteVisitTimeOnSite) {
      this.siteVisitTimeOnSite = siteVisitTimeOnSite;
    }


    public String getSyncErrorMessage() {
      return syncErrorMessage;
    }

    public void setSyncErrorMessage(String syncErrorMessage) {
      this.syncErrorMessage = syncErrorMessage;
    }


    public String getIsSnowManagement() {
      return isSnowManagement;
    }

    public void setIsSnowManagement(String isSnowManagement) {
      this.isSnowManagement = isSnowManagement;
    }


    public java.sql.Timestamp getCreatedOn() {
      return createdOn;
    }

    public void setCreatedOn(java.sql.Timestamp createdOn) {
      this.createdOn = createdOn;
    }

}

