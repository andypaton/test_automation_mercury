package mercury.database.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DeferredJob {
	
	@Id
	@Column(name = "Id")	
	Integer id;
	
	@Column(name = "JobReference")	
	Integer jobReference;
	
	@Column(name = "ResourceAssignmentId")	
	Integer resourceAssignmentId;
	
	@Column(name = "ResourceAssignmentDeferralDate")	
	Date resourceAssignmentDeferralDate;
	
	@Column(name = "SiteId")	
	Integer siteId;
	
	@Column(name = "FaultPriority")	
	String faultPriority;
	
	@Column(name = "AssetType")	
	String assetType;
	
	@Column(name = "AssetSubtype")	
	String assetSubtype;
	
	@Column(name = "JobDeferralDate")	
	Date jobDeferralDate;

	@Column(name = "ResourceName")	
	String resourceName;

	@Column(name = "ResourceId")	
	Integer resourceId;
	
	@Column(name = "RwhId")	
	Integer rwhId;
	
	@Column(name = "StartAt")	
	String startAt;
	
	@Column(name = "EndAt")	
	String endAt;
	
	@Column(name = "NotificationMethod")	
	String notificationMethod;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJobReference() {
		return jobReference;
	}

	public void setJobReference(Integer jobReference) {
		this.jobReference = jobReference;
	}

	public Integer getResourceAssignmentId() {
		return resourceAssignmentId;
	}

	public void setResourceAssignmentId(Integer resourceAssignmentId) {
		resourceAssignmentId = resourceAssignmentId;
	}

	public Date getResourceAssignmentDeferralDate() {
		return resourceAssignmentDeferralDate;
	}

	public void setResourceAssignmentDeferralDate(Date resourceAssignmentDeferralDate) {
		this.resourceAssignmentDeferralDate = resourceAssignmentDeferralDate;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getRwhId() {
		return rwhId;
	}

	public void setRwhId(Integer rwhId) {
		this.rwhId = rwhId;
	}

	public String getFaultPriority() {
		return faultPriority;
	}

	public void setFaultPriority(String faultPriority) {
		this.faultPriority = faultPriority;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getAssetSubtype() {
		return assetSubtype;
	}

	public void setAssetSubtype(String assetSubtype) {
		this.assetSubtype = assetSubtype;
	}

	public Date getJobDeferralDate() {
		return jobDeferralDate;
	}

	public void setJobDeferralDate(Date jobDeferralDate) {
		this.jobDeferralDate = jobDeferralDate;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	public String getNotificationMethod() {
		return notificationMethod;
	}

	public void setNotificationMethod(String notificationMethod) {
		this.notificationMethod = notificationMethod;
	}
}
