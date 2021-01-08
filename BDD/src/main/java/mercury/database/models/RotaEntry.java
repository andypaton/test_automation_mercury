package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="ResourceWorkingHours")
public class RotaEntry {

	@Id
	@Column(name = "ResourceId")
	private Integer resourceId;
	
	@Column(name = "SiteId")
	private Integer siteId;
	
	@Column(name = "StartAt")
	private String startAt;
	
	@Column(name = "EndAt")
	private String endAt;
	
	@Column(name = "ResourceProfileName")
	private String resourceProfileName;

	@Column(name = "RotaEntryType")
	private String rotaEntryType;
	
	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
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
	
	public String getResourceProfileName() {
		return resourceProfileName;
	}

	public void setResourceProfileName(String resourceProfileName) {
		this.resourceProfileName = resourceProfileName;
	}
	
	public String getRotaEntryType() {
		return rotaEntryType;
	}

	public void setRotaEntryType(String rotaEntryType) {
		this.rotaEntryType = rotaEntryType;
	}
	
	@Override
	public String toString() {
		return "RotaEntry [siteId=" + siteId 
				+ ",resourceId=" + resourceId
				+ ", startAt=" + startAt
				+ ", endAt=" + endAt 
				+ ", resourceProfileName=" + resourceProfileName
		        + ", rotaEntryType=" + rotaEntryType + "]";
	}

}
