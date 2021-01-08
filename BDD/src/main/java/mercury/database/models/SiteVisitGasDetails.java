package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SiteVisitGasDetails")
public class SiteVisitGasDetails {

    @Id
    @Column(name = "SiteVisitId") private Integer siteVisitId;
    @Column(name = "GasApplianceTypeId") private Integer gasApplianceTypeId;
    @Column(name = "AssetId") private Integer assetId;
    @Column(name = "ApplianceInformation") private String applianceInformation;
    @Column(name = "ReceiverLevelRecorded") private boolean receiverLevelRecorded;
    @Column(name = "QuantityOfBallsFloating") private String quantityOfBallsFloating;
    @Column(name = "LevelIndicator") private String levelIndicator;
    @Column(name = "GasTypeId") private Integer gasTypeId;
    @Column(name = "CreatedOn") private java.sql.Timestamp createdOn;
    @Column(name = "CreatedBy") private String createdBy;
    @Column(name = "UpdatedOn") private java.sql.Timestamp updatedOn;
    @Column(name = "UpdatedBy") private String updatedBy;
    @Column(name = "GasLeakCheckStatusId") private Integer gasLeakCheckStatusId;
    @Column(name = "GasLeakCheckMethodId") private Integer gasLeakCheckMethodId;
    @Column(name = "GasLeakCheckResultTypeId") private Integer gasLeakCheckResultTypeId;
    @Column(name = "NewAssetMaximumCharge") private Float newAssetMaximumCharge;
    @Column(name = "ReasonForChangingMaximumCharge") private String reasonForChangingMaximumCharge;
    @Column(name = "IsRequiredToReturn") private boolean isRequiredToReturn;
    public Integer getSiteVisitId() {
        return siteVisitId;
    }
    public void setSiteVisitId(Integer siteVisitId) {
        this.siteVisitId = siteVisitId;
    }
    public Integer getGasApplianceTypeId() {
        return gasApplianceTypeId;
    }
    public void setGasApplianceTypeId(Integer gasApplianceTypeId) {
        this.gasApplianceTypeId = gasApplianceTypeId;
    }
    public Integer getAssetId() {
        return assetId;
    }
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }
    public String getApplianceInformation() {
        return applianceInformation;
    }
    public void setApplianceInformation(String applianceInformation) {
        this.applianceInformation = applianceInformation;
    }
    public boolean isReceiverLevelRecorded() {
        return receiverLevelRecorded;
    }
    public void setReceiverLevelRecorded(boolean receiverLevelRecorded) {
        this.receiverLevelRecorded = receiverLevelRecorded;
    }
    public String getQuantityOfBallsFloating() {
        return quantityOfBallsFloating;
    }
    public void setQuantityOfBallsFloating(String quantityOfBallsFloating) {
        this.quantityOfBallsFloating = quantityOfBallsFloating;
    }
    public String getLevelIndicator() {
        return levelIndicator;
    }
    public void setLevelIndicator(String levelIndicator) {
        this.levelIndicator = levelIndicator;
    }
    public Integer getGasTypeId() {
        return gasTypeId;
    }
    public void setGasTypeId(Integer gasTypeId) {
        this.gasTypeId = gasTypeId;
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
    public Integer getGasLeakCheckStatusId() {
        return gasLeakCheckStatusId;
    }
    public void setGasLeakCheckStatusId(Integer gasLeakCheckStatusId) {
        this.gasLeakCheckStatusId = gasLeakCheckStatusId;
    }
    public Integer getGasLeakCheckMethodId() {
        return gasLeakCheckMethodId;
    }
    public void setGasLeakCheckMethodId(Integer gasLeakCheckMethodId) {
        this.gasLeakCheckMethodId = gasLeakCheckMethodId;
    }
    public Integer getGasLeakCheckResultTypeId() {
        return gasLeakCheckResultTypeId;
    }
    public void setGasLeakCheckResultTypeId(Integer gasLeakCheckResultTypeId) {
        this.gasLeakCheckResultTypeId = gasLeakCheckResultTypeId;
    }
    public Float getNewAssetMaximumCharge() {
        return newAssetMaximumCharge;
    }
    public void setNewAssetMaximumCharge(Float newAssetMaximumCharge) {
        this.newAssetMaximumCharge = newAssetMaximumCharge;
    }
    public String getReasonForChangingMaximumCharge() {
        return reasonForChangingMaximumCharge;
    }
    public void setReasonForChangingMaximumCharge(String reasonForChangingMaximumCharge) {
        this.reasonForChangingMaximumCharge = reasonForChangingMaximumCharge;
    }
    public boolean isRequiredToReturn() {
        return isRequiredToReturn;
    }
    public void setRequiredToReturn(boolean isRequiredToReturn) {
        this.isRequiredToReturn = isRequiredToReturn;
    }


}
