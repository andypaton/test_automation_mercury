package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;


@Entity
@Table(name="SiteContractorAsset")
public class SiteContractorAsset {

    @Id
    @Column(name = "SiteId")
    private Integer siteId;

    @Column(name = "ResourceId")
    private Integer resourceId;

    @Column(name = "AssetClassificationSiteId")
    private Integer assetClassificationSiteId;

    @Column(name = "AssetClassificationId")
    private Integer assetClassificationId;

    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name = "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "Priority")
    private Integer priority;

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

    @Column(name = "StartAt")
    private String startAt;

    @Column(name = "EndAt")
    private String endAt;

    @Column(name = "StartDayOfTheWeek")
    private Integer startDayOfTheWeek;

    @Column(name = "EndDayOfTheWeek")
    private Integer endDayOfTheWeek;


    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAssetClassificationSiteId() {
        return assetClassificationSiteId;
    }

    public void setAssetClassificationSiteId(Integer assetClassificationSiteId) {
        this.assetClassificationSiteId = assetClassificationSiteId;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getFaultPriorityId() {
        return responsePriorityId;
    }

    public void setFaultPriorityId(Integer faultPriorityId) {
        this.responsePriorityId = faultPriorityId;
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

    public Integer getStartDayOfTheWeek() {
        return startDayOfTheWeek;
    }

    public void setStartDayOfTheWeek(Integer startDayOfTheWeek) {
        this.startDayOfTheWeek = startDayOfTheWeek;
    }

    public Integer getEndDayOfTheWeek() {
        return endDayOfTheWeek;
    }

    public void setEndDayOfTheWeek(Integer endDayOfTheWeek) {
        this.endDayOfTheWeek = endDayOfTheWeek;
    }

    public void copy(SiteContractorAsset siteContractorAsset) {
        BeanUtils.copyProperties(siteContractorAsset, this);
    }

    @Override
    public String toString() {
        return  "siteId=" + siteId +
                ", resourceId=" + resourceId +
                ", resourceName=" + resourceName +
                ", resourceProfileName=" + resourceProfileName +
                "\n\t\t" +
                ", assetClassificationSiteId=" + assetClassificationSiteId +
                ", assetClassificationId=" + assetClassificationId +
                ", assetSubTypeName=" + assetSubTypeName +
                ", faultTypeName=" + faultTypeName +
                ", priority=" + priority +
                "\n\t\t" +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", startDayOfTheWeek=" + startDayOfTheWeek +
                ", endDayOfTheWeek=" + endDayOfTheWeek;
    }

}
