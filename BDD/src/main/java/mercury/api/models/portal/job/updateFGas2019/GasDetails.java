package mercury.api.models.portal.job.updateFGas2019;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gasApplianceTypeId",
    "assetId",
    "applianceInformation",
    "receiverLevelRecorded",
    "quantityOfBallsFloating",
    "levelIndicator",
    "gasTypeId",
    "newAssetMaximumCharge",
    "reasonForChangingMaximumCharge",
    "gasLeakCheckStatusId",
    "gasLeakCheckMethodId",
    "gasLeakCheckResultTypeId",
    "gasCylinders",
    "gasLeakSites"
})
public class GasDetails {

    @JsonProperty("gasApplianceTypeId")
    private String gasApplianceTypeId;

    @JsonProperty("assetId")
    private String assetId;

    @JsonProperty("applianceInformation")
    private String applianceInformation;

    @JsonProperty("receiverLevelRecorded")
    private String receiverLevelRecorded;

    @JsonProperty("quantityOfBallsFloating")
    private String quantityOfBallsFloating;

    @JsonProperty("levelIndicator")
    private String levelIndicator;

    @JsonProperty("gasTypeId")
    private String gasTypeId;

    @JsonProperty("newAssetMaximumCharge")
    private String newAssetMaximumCharge;

    @JsonProperty("reasonForChangingMaximumCharge")
    private String reasonForChangingMaximumCharge;

    @JsonProperty("gasLeakCheckStatusId")
    private String gasLeakCheckStatusId;

    @JsonProperty("gasLeakCheckMethodId")
    private String gasLeakCheckMethodId;

    @JsonProperty("gasLeakCheckResultTypeId")
    private String gasLeakCheckResultTypeId;

    @JsonProperty("gasCylinders")
    private List<GasCylinder> gasCylinders;

    @JsonProperty("gasLeakSites")
    private List<GasLeakSite> gasLeakSites;

    @JsonProperty("gasApplianceTypeId")
    public String getGasApplianceTypeId() {
        return gasApplianceTypeId;
    }

    @JsonProperty("gasApplianceTypeId")
    public void setGasApplianceTypeId(String gasApplianceTypeId) {
        this.gasApplianceTypeId = gasApplianceTypeId;
    }

    @JsonProperty("assetId")
    public String getAssetId() {
        return assetId;
    }

    @JsonProperty("assetId")
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @JsonProperty("applianceInformation")
    public String getApplianceInformation() {
        return applianceInformation;
    }

    @JsonProperty("applianceInformation")
    public void setApplianceInformation(String applianceInformation) {
        this.applianceInformation = applianceInformation;
    }

    @JsonProperty("receiverLevelRecorded")
    public String getReceiverLevelRecorded() {
        return receiverLevelRecorded;
    }

    @JsonProperty("receiverLevelRecorded")
    public void setReceiverLevelRecorded(String receiverLevelRecorded) {
        this.receiverLevelRecorded = receiverLevelRecorded;
    }

    @JsonProperty("quantityOfBallsFloating")
    public String getQuantityOfBallsFloating() {
        return quantityOfBallsFloating;
    }

    @JsonProperty("quantityOfBallsFloating")
    public void setQuantityOfBallsFloating(String quantityOfBallsFloating) {
        this.quantityOfBallsFloating = quantityOfBallsFloating;
    }

    @JsonProperty("levelIndicator")
    public String getLevelIndicator() {
        return levelIndicator;
    }

    @JsonProperty("levelIndicator")
    public void setLevelIndicator(String levelIndicator) {
        this.levelIndicator = levelIndicator;
    }

    @JsonProperty("gasTypeId")
    public String getGasTypeId() {
        return gasTypeId;
    }

    @JsonProperty("gasTypeId")
    public void setGasTypeId(String gasTypeId) {
        this.gasTypeId = gasTypeId;
    }

    @JsonProperty("newAssetMaximumCharge")
    public String getNewAssetMaximumCharge() {
        return newAssetMaximumCharge;
    }

    @JsonProperty("newAssetMaximumCharge")
    public void setNewAssetMaximumCharge(String newAssetMaximumCharge) {
        this.newAssetMaximumCharge = newAssetMaximumCharge;
    }

    @JsonProperty("reasonForChangingMaximumCharge")
    public String getReasonForChangingMaximumCharge() {
        return reasonForChangingMaximumCharge;
    }

    @JsonProperty("reasonForChangingMaximumCharge")
    public void setReasonForChangingMaximumCharge(String reasonForChangingMaximumCharge) {
        this.reasonForChangingMaximumCharge = reasonForChangingMaximumCharge;
    }

    @JsonProperty("gasLeakCheckStatusId")
    public String getGasLeakCheckStatusId() {
        return gasLeakCheckStatusId;
    }

    @JsonProperty("gasLeakCheckStatusId")
    public void setGasLeakCheckStatusId(String gasLeakCheckStatusId) {
        this.gasLeakCheckStatusId = gasLeakCheckStatusId;
    }

    @JsonProperty("gasLeakCheckMethodId")
    public String getGasLeakCheckMethodId() {
        return gasLeakCheckMethodId;
    }

    @JsonProperty("gasLeakCheckMethodId")
    public void setGasLeakCheckMethodId(String gasLeakCheckMethodId) {
        this.gasLeakCheckMethodId = gasLeakCheckMethodId;
    }

    @JsonProperty("gasLeakCheckResultTypeId")
    public String getGasLeakCheckResultTypeId() {
        return gasLeakCheckResultTypeId;
    }

    @JsonProperty("gasLeakCheckResultTypeId")
    public void setGasLeakCheckResultTypeId(String gasLeakCheckResultTypeId) {
        this.gasLeakCheckResultTypeId = gasLeakCheckResultTypeId;
    }

    @JsonProperty("gasCylinders")
    public List<GasCylinder> getGasCylinders() {
        return gasCylinders;
    }

    @JsonProperty("gasCylinders")
    public void setGasCylinders(List<GasCylinder> gasCylinders) {
        this.gasCylinders = gasCylinders;
    }

    @JsonProperty("gasLeakSites")
    public List<GasLeakSite> getGasLeakSite() {
        return gasLeakSites;
    }

    @JsonProperty("gasLeakSites")
    public void setGasLeakSite(List<GasLeakSite> gasLeakSites) {
        this.gasLeakSites = gasLeakSites;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gasApplianceTypeId", gasApplianceTypeId).append("assetId", assetId).append("applianceInformation", applianceInformation).append("receiverLevelRecorded", receiverLevelRecorded).append("quantityOfBallsFloating", quantityOfBallsFloating).append("levelIndicator", levelIndicator).append("gasTypeId", gasTypeId).append("newAssetMaximumCharge", newAssetMaximumCharge).append("reasonForChangingMaximumCharge", reasonForChangingMaximumCharge).append("gasLeakCheckStatusId", gasLeakCheckStatusId).append("gasLeakCheckMethodId", gasLeakCheckMethodId).append("gasLeakCheckResultTypeId", gasLeakCheckResultTypeId).toString();
    }

}