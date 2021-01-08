package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.BeanUtils;

@Entity
public class JobView {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "JobReference")
    private String jobReference;

    @Column(name = "Description")
    private String description;

    @Column(name = "JobTypeId")
    private Integer jobTypeId;

    @Column(name = "JobTypeName")
    private String jobTypeName;

    @Column(name = "SiteId")
    private Integer siteId;

    @Column(name = "name")
    private String name;

    @Column(name = "SiteCode")
    private String siteCode;

    @Column(name = "LocationId")
    private Integer locationId;

    @Column(name = "LocationName")
    private String locationName;

    @Column(name = "SubLocationName")
    private String subLocationName;

    @Column(name = "AssetClassificationId")
    private Integer assetClassificationId;

    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name = "AssetName")
    private String assetName;

    @Column(name = "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "ResponsePriorityId")
    private Integer faultPriorityId;

    @Column(name = "Priority")
    private Integer priority;

    @Column(name = "FaultPriority")
    private String faultPriority;

    @Column(name = "FaultTypeId")
    private Integer faultTypeId;

    @Column(name = "FaultType")
    private String faultType;

    @Column(name = "ResourceAssignmentStatusId")
    private Integer resourceAssignmentStatusId;

    @Column(name = "ResourceAssignmentStatusName")
    private String resourceAssignmentStatusName;

    @Column(name = "ContractorReference")
    private String contractorReference;

    @Column(name = "JobStatusId")
    private Integer jobStatusId;

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "ETAFrom")
    private java.sql.Timestamp etaFrom;

    @Column(name = "ETATo")
    private java.sql.Timestamp etaTo;

    @Column(name = "ETAWindowId")
    private String etaWindowId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobReference() {
        return jobReference;
    }

    public void setJobReference(String jobReference) {
        this.jobReference = jobReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public String getJobTypeName() {
        return jobTypeName;
    }

    public void setJobTypeName(String jobTypeName) {
        this.jobTypeName = jobTypeName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getSubLocationName() {
        return subLocationName;
    }

    public void setSubLocationName(String subLocationName) {
        this.subLocationName = subLocationName;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public String getAssetSubTypeName() {
        return assetSubTypeName;
    }

    public void setAssetSubTypeName(String assetSubTypeName) {
        this.assetSubTypeName = assetSubTypeName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetClassificationName() {
        return assetClassificationName;
    }

    public void setAssetClassificationName(String assetClassificationName) {
        this.assetClassificationName = assetClassificationName;
    }

    public Integer getFaultPriorityId() {
        return faultPriorityId;
    }

    public void setFaultPriorityId(Integer faultPriorityId) {
        this.faultPriorityId = faultPriorityId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getFaultPriority() {
        return faultPriority;
    }

    public void setFaultPriority(String faultPriority) {
        this.faultPriority = faultPriority;
    }

    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public Integer getResourceAssignmentStatusId() {
        return resourceAssignmentStatusId;
    }

    public void setResourceAssignmentStatusId(Integer resourceAssignmentStatusId) {
        this.resourceAssignmentStatusId = resourceAssignmentStatusId;
    }

    public String getResourceAssignmentStatusName() {
        return resourceAssignmentStatusName;
    }

    public void setResourceAssignmentStatusName(String resourceAssignmentStatusName) {
        this.resourceAssignmentStatusName = resourceAssignmentStatusName;
    }

    public String getContractorReference() {
        return contractorReference;
    }

    public void setContractorReference(String contractorReference) {
        this.contractorReference = contractorReference;
    }

    public Integer getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(Integer jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public java.sql.Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(java.sql.Timestamp createdOn) {
        this.createdOn = createdOn;
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

    public String getEtaWindowId() {
        return etaWindowId;
    }

    public void setEtaWindowId(String etaWindowId) {
        this.etaWindowId = etaWindowId;
    }

    public void copy(JobView jobView) {
        BeanUtils.copyProperties(jobView, this);
    }

    @Override
    public String toString() {
        return "JobView [id=" + id + ", jobReference=" + jobReference + ", description=" + description + ", jobTypeId=" + jobTypeId + ", jobTypeName=" + jobTypeName + ", siteId=" + siteId + ", name=" + name + ", siteCode=" + siteCode + ", locationId=" + locationId + ", locationName=" + locationName + ", subLocationName=" + subLocationName + ", assetClassificationId=" + assetClassificationId + ", assetSubTypeName=" + assetSubTypeName + ", assetName=" + assetName + ", assetClassificationName=" + assetClassificationName + ", faultPriorityId=" + faultPriorityId + ", faultPriority=" + faultPriority + ", faultTypeId=" + faultTypeId + ", faultType=" + faultType + ", resourceAssignmentStatusId=" + resourceAssignmentStatusId + ", resourceAssignmentStatusName=" + resourceAssignmentStatusName + ", contractorReference=" + contractorReference + ", jobStatusId=" + jobStatusId + ", createdOn=" + createdOn + ", etaFrom=" + etaFrom + ", etaTo=" + etaTo + ", etaWindowId=" + etaWindowId + "]";
    }

}
