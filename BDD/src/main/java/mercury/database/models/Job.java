package mercury.database.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Job {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "PpmJobId")
    Integer ppmJobId;

    @Column(name = "SiteId")
    Integer siteId;

    @Column(name = "JobReference")
    Integer jobReference;

    @Column(name = "JobStatusId")
    Integer jobStatusId;

    @Column(name = "AssetId")
    Integer assetId;

    @Column(name = "OriginalAssetId")
    Integer originalAssetId;

    @Column(name = "OriginalAssetClassificationId")
    Integer originalAssetClassificationId;

    @Column(name = "SpecificResourceRequestId")
    Integer specificResourceRequestId;

    @Column(name = "AssetClassificationId")
    Integer assetClassificationId;

    @Column(name = "CallerId")
    Integer callerId;

    @Column(name = "JobTypeId")
    Integer jobTypeId;

    @Column(name = "Description")
    String description;

    @Column(name = "FaultTypeId")
    Integer faultTypeId;

    @Column(name = "ResponsePriorityId")
    Integer faultPriorityId;

    @Column(name = "LocationId")
    Integer locationId;

    @Column(name = "IsDeferred")
    Integer isDeferred;

    @Column(name = "IsRetrospective")
    Integer isRetrospective;

    @Column(name = "DeferralTypeId")
    Integer deferralTypeId;

    @Column(name = "DeferralDate")
    java.sql.Timestamp deferralDate;

    @Column(name = "DeferralNote")
    String deferralNote;

    @Column(name = "JobCancellationId")
    Integer jobCancellationId;

    @Column(name = "DuplicateJobId")
    Integer duplicateJobId;

    @Column(name = "PreferredResourceProfileId")
    Integer preferredResourceProfileId;

    @Column(name = "ReasonNotLoggedAgainstAssetId")
    Integer reasonNotLoggedAgainstAssetId;

    @Column(name = "TotalTimeParked")
    Integer totalTimeParked;

    @Column(name = "UnParkedDate")
    Date unParkedDate;

    @Column(name = "JobParkingNotes")
    String jobParkingNotes;

    @Column(name = "CreatedOn")
    java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy")
    String createdBy;

    @Column(name = "UpdatedOn")
    Date updatedOn;

    @Column(name = "UpdatedBy")
    String updatedBy;

    @Column(name = "IsApproved")
    Integer isApproved;

    @Column(name = "ConfirmedSelfHelp")
    Integer confirmedSelfHelp;

    @Column(name = "CompletedDate")
    Date completedDate;

    public Integer getId() {
        return id;
    }

    public Integer getPpmJobId() {
        return ppmJobId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getJobReference() {
        return jobReference;
    }

    public Integer getJobStatusId() {
        return jobStatusId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public Integer getOriginalAssetId() {
        return originalAssetId;
    }

    public Integer getOriginalAssetClassificationId() {
        return originalAssetClassificationId;
    }

    public Integer getSpecificResourceRequestId() {
        return specificResourceRequestId;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public Integer getCallerId() {
        return callerId;
    }

    public Integer getJobTypeId() {
        return jobTypeId;
    }

    public String getDescription() {
        return description;
    }

    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    public Integer getFaultPriorityId() {
        return faultPriorityId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public Integer getIsDeferred() {
        return isDeferred;
    }

    public Integer getIsRetrospective() {
        return isRetrospective;
    }

    public Integer getDeferralTypeId() {
        return deferralTypeId;
    }

    public java.sql.Timestamp getDeferralDate() {
        return deferralDate;
    }

    public String getDeferralNote() {
        return deferralNote;
    }

    public Integer getJobCancellationId() {
        return jobCancellationId;
    }

    public Integer getDuplicateJobId() {
        return duplicateJobId;
    }

    public Integer getPreferredResourceProfileId() {
        return preferredResourceProfileId;
    }

    public Integer getReasonNotLoggedAgainstAssetId() {
        return reasonNotLoggedAgainstAssetId;
    }

    public Integer getTotalTimeParked() {
        return totalTimeParked;
    }

    public Date getUnParkedDate() {
        return unParkedDate;
    }

    public String getJobParkingNotes() {
        return jobParkingNotes;
    }

    public java.sql.Timestamp getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Integer getIsApproved() {
        return isApproved;
    }

    public Integer getConfirmedSelfHelp() {
        return confirmedSelfHelp;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

}
