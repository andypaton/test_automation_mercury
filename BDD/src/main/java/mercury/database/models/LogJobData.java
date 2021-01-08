package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;


@Entity
@Table(name="LogJobData")
public class LogJobData {

    @Id
    @Column(name = "SiteId")
    private Integer siteId;

    @Column(name = "SiteName")
    private String siteName;

    @Column(name = "ResourceId")
    private Integer resourceId;

    @Column(name = "AssetClassificationId")
    private Integer assetClassificationId;

    @Column(name = "AssetTypeName")
    private String assetTypeName;

    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name = "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "ResponsePriorityId")
    private Integer responsePriorityId;

    @Column(name = "FaultTypeName")
    private String faultTypeName;

    @Column(name = "FaultTypeId")
    private Integer faultTypeId;

    @Column(name = "ResourceName")
    private String resourceName;

    @Column(name = "ResourceProfileName")
    private String resourceProfileName;

    @Column(name = "ResourceTypeName")
    private String resourceTypeName;

    @Column(name = "LocationName")
    private String locationName;

    @Column(name = "LocationId")
    private Integer locationId;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public Integer getPriority() {
        return responsePriorityId;
    }

    public void setPriority(Integer responsePriorityId) {
        this.responsePriorityId = responsePriorityId;
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

    public String getAssetClassificationName() {
        return assetClassificationName;
    }

    public void setAssetClassificationName(String assetClassificationName) {
        this.assetClassificationName = assetClassificationName;
    }

    public String getFaultTypeName() {
        return faultTypeName;
    }

    public void setFaultTypeName(String faultTypeName) {
        this.faultTypeName = faultTypeName;
    }

    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceProfileName() {
        return resourceProfileName;
    }

    public void setResourceProfileName(String resourceProfileName) {
        this.resourceProfileName = resourceProfileName;
    }

    public String getResourceTypeName() {
        return resourceTypeName;
    }

    public void setResourceTypeName(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationTypeName(String locationTypeName) {
        this.locationName = locationTypeName;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void copy(LogJobData logJobData) {
        BeanUtils.copyProperties(logJobData, this);
    }

    @Override
    public String toString() {
        return  "siteId=" + siteId +
                ", resourceId=" + resourceId +
                ", resourceName=" + resourceName +
                ", resourceProfileName=" + resourceProfileName +
                ", resourceTypeName=" + resourceTypeName +
                ", assetClassificationId=" + assetClassificationId +
                ", assetClassificationName=" + assetClassificationName +
                ", assetSubTypeName=" + assetSubTypeName +
                ", faultTypeId=" + faultTypeId +
                ", faultTypeName=" + faultTypeName +
                ", responsePriorityId=" + responsePriorityId +
                ", locationName=" + locationName +
                ", locationId=" + locationId;
    }

}
