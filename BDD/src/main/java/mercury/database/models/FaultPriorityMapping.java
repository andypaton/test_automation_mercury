package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FaultPriorityMapping {
	
	@Id
	@Column(name = "Id")
	private Integer id;

	@Column(name = "SiteTypeId")
	private Integer siteTypeId;

	@Column(name = "SiteTypeName")
	private String siteTypeName;
	
    @Column(name = "AssetSubTypeId")
    private Integer assetSubTypeId;

    @Column(name = "AssetTypeName")
    private String assetTypeName;
    
    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name = "AssetClassificationId")
    private Integer assetClassificationId;

    @Column(name = "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "FaultTypeId")
    private Integer faultTypeId;

    @Column(name = "FaultTypeName")
    private String faultTypeName;

    @Column(name = "ResponsePriorityId")
    private Integer faultPriorityId;

    @Column(name = "Priority")
    private Integer priority;

    @Column(name = "Detail")
    private String detail;

    @Column(name = "SelfAssigned", columnDefinition="BIT")
    private Boolean selfAssigned;

    @Column(name = "TechBureau", columnDefinition="BIT")
    private Boolean techBureau;

    @Column(name = "ImmediateCallout", columnDefinition="BIT")
    private Boolean immediateCallout;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSiteTypeId() {
		return siteTypeId;
	}

	public void setSiteTypeId(Integer siteTypeId) {
		this.siteTypeId = siteTypeId;
	}

	public String getSiteTypeName() {
		return siteTypeName;
	}

	public void setSiteTypeName(String siteTypeName) {
		this.siteTypeName = siteTypeName;
	}

	public Integer getAssetSubTypeId() {
		return assetSubTypeId;
	}

	public void setAssetSubTypeId(Integer assetSubTypeId) {
		this.assetSubTypeId = assetSubTypeId;
	}

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

	public String getAssetSubTypeName() {
		return assetSubTypeName;
	}

	public void setAssetSubTypeName(String assetSubTypeName) {
		this.assetSubTypeName = assetSubTypeName;
	}

	public Integer getAssetClassificationId() {
		return assetClassificationId;
	}

	public void setAssetClassificationId(Integer assetClassificationId) {
		this.assetClassificationId = assetClassificationId;
	}

	public String getAssetClassificationName() {
		return assetClassificationName;
	}

	public void setAssetClassificationName(String assetClassificationName) {
		this.assetClassificationName = assetClassificationName;
	}

	public Integer getFaultTypeId() {
		return faultTypeId;
	}

	public void setFaultTypeId(Integer faultTypeId) {
		this.faultTypeId = faultTypeId;
	}

	public String getFaultTypeName() {
		return faultTypeName;
	}

	public void setFaultTypeName(String faultTypeName) {
		this.faultTypeName = faultTypeName;
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

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Boolean getSelfAssigned() {
		return selfAssigned;
	}

	public void setSelfAssigned(Boolean selfAssigned) {
		this.selfAssigned = selfAssigned;
	}

	public Boolean getTechBureau() {
		return techBureau;
	}

	public void setTechBureau(Boolean techBureau) {
		this.techBureau = techBureau;
	}

	public Boolean getImmediateCallout() {
		return immediateCallout;
	}

	public void setImmediateCallout(Boolean immediateCallout) {
		this.immediateCallout = immediateCallout;
	}

	@Override
	public String toString() {
		return "id=" + id + 
				", siteTypeId=" + siteTypeId + 
				", siteTypeName=" + siteTypeName + 
				", assetSubTypeId=" + assetSubTypeId + 
				", assetSubTypeName=" + assetSubTypeName + 
				", assetClassificationId=" + assetClassificationId + 
				", assetClassificationName=" + assetClassificationName + 
				", faultTypeId=" + faultTypeId + 
				", faultTypeName=" + faultTypeName + 
				", responsePriorityId=" + faultPriorityId + 
				", priority=" + priority + 
				", detail=" + detail + 
				", selfAssigned=" + selfAssigned + 
				", techBureau=" + techBureau + 
				", immediateCallout=" + immediateCallout;
	}

}
