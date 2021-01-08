package mercury.api.models.portal.job.updateFGas2019;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "gasSourceTypeId",
    "GasCylinderTypeId",
    "refrigerantSourceLocation",
    "cylinderSerialNumber",
    "gasCylinderCapacityId",
    "initialQuantity",
    "installedQuantity",
    "isPartialCylinder",
    "surplusDestinationId",
    "surplusTypeId",
    "returnedTo",
"surplus" })
public class GasCylinder extends modelBase<GasCylinder> {

    @JsonProperty("gasSourceTypeId")
    private String gasSourceTypeId;

    @JsonProperty("GasCylinderTypeId")
    private String gasCylinderTypeId;

    @JsonProperty("refrigerantSourceLocation")
    private String refrigerantSourceLocation;

    @JsonProperty("cylinderSerialNumber")
    private String cylinderSerialNumber;

    @JsonProperty("gasCylinderCapacityId")
    private String gasCylinderCapacityId;

    @JsonProperty("initialQuantity")
    private String initialQuantity;

    @JsonProperty("installedQuantity")
    private String installedQuantity;

    @JsonProperty("isPartialCylinder")
    private String isPartialCylinder;

    @JsonProperty("surplusDestinationId")
    private String surplusDestinationId;

    @JsonProperty("surplusTypeId")
    private String surplusTypeId;

    @JsonProperty("returnedTo")
    private String returnedTo;

    @JsonProperty("surplus")
    private String surplus;

    @JsonProperty("gasSourceTypeId")
    public String getGasSourceTypeId() {
        return gasSourceTypeId;
    }

    @JsonProperty("gasSourceTypeId")
    public void setGasSourceTypeId(String gasSourceTypeId) {
        this.gasSourceTypeId = gasSourceTypeId;
    }

    @JsonProperty("GasCylinderTypeId")
    public String getGasCylinderTypeId() {
        return gasCylinderTypeId;
    }

    @JsonProperty("GasCylinderTypeId")
    public void setGasCylinderTypeId(String gasCylinderTypeId) {
        this.gasCylinderTypeId = gasCylinderTypeId;
    }

    @JsonProperty("refrigerantSourceLocation")
    public String getRefrigerantSourceLocation() {
        return refrigerantSourceLocation;
    }

    @JsonProperty("refrigerantSourceLocation")
    public void setRefrigerantSourceLocation(String refrigerantSourceLocation) {
        this.refrigerantSourceLocation = refrigerantSourceLocation;
    }

    @JsonProperty("cylinderSerialNumber")
    public String getCylinderSerialNumber() {
        return cylinderSerialNumber;
    }

    @JsonProperty("cylinderSerialNumber")
    public void setCylinderSerialNumber(String cylinderSerialNumber) {
        this.cylinderSerialNumber = cylinderSerialNumber;
    }

    @JsonProperty("gasCylinderCapacityId")
    public String getGasCylinderCapacityId() {
        return gasCylinderCapacityId;
    }

    @JsonProperty("gasCylinderCapacityId")
    public void setGasCylinderCapacityId(String gasCylinderCapacityId) {
        this.gasCylinderCapacityId = gasCylinderCapacityId;
    }

    @JsonProperty("initialQuantity")
    public String getInitialQuantity() {
        return initialQuantity;
    }

    @JsonProperty("initialQuantity")
    public void setInitialQuantity(String initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    @JsonProperty("installedQuantity")
    public String getInstalledQuantity() {
        return installedQuantity;
    }

    @JsonProperty("installedQuantity")
    public void setInstalledQuantity(String installedQuantity) {
        this.installedQuantity = installedQuantity;
    }

    @JsonProperty("isPartialCylinder")
    public String getIsPartialCylinder() {
        return isPartialCylinder;
    }

    @JsonProperty("isPartialCylinder")
    public void setIsPartialCylinder(String isPartialCylinder) {
        this.isPartialCylinder = isPartialCylinder;
    }

    @JsonProperty("surplusDestinationId")
    public String getSurplusDestinationId() {
        return surplusDestinationId;
    }

    @JsonProperty("surplusDestinationId")
    public void setSurplusDestinationId(String surplusDestinationId) {
        this.surplusDestinationId = surplusDestinationId;
    }

    @JsonProperty("surplusTypeId")
    public String getSurplusTypeId() {
        return surplusTypeId;
    }

    @JsonProperty("surplusTypeId")
    public void setSurplusTypeId(String surplusTypeId) {
        this.surplusTypeId = surplusTypeId;
    }

    @JsonProperty("returnedTo")
    public String getReturnedTo() {
        return returnedTo;
    }

    @JsonProperty("returnedTo")
    public void setReturnedTo(String returnedTo) {
        this.returnedTo = returnedTo;
    }

    @JsonProperty("surplus")
    public String getSurplus() {
        return surplus;
    }

    @JsonProperty("surplus")
    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gasSourceTypeId", gasSourceTypeId).append("gasCylinderTypeId", gasCylinderTypeId).append("refrigerantSourceLocation", refrigerantSourceLocation).append("cylinderSerialNumber", cylinderSerialNumber).append("gasCylinderCapacityId", gasCylinderCapacityId).append("initialQuantity", initialQuantity).append("installedQuantity", installedQuantity).append("isPartialCylinder", isPartialCylinder).append("surplusDestinationId", surplusDestinationId).append("surplusTypeId", surplusTypeId).append("returnedTo", returnedTo).append("surplus", surplus).toString();
    }

}
