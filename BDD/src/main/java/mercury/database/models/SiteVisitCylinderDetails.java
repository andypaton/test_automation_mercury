package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SiteVisitCylinderDetails {

    @Id @Column(name = "Id")
    private Integer id;

    @Column(name = "SiteVisitGasDetailsId")
    private Integer siteVisitGasDetailsId;

    @Column(name = "GasSourceTypeId")
    private Integer gasSourceTypeId;

    @Column(name = "RefrigerantSourceLocation")
    private String refrigerantSourceLocation;

    @Column(name = "BottleNumber")
    private String bottleNumber;

    @Column(name = "BottleQuantity")
    private Double bottleQuantity;

    @Column(name = "Units")
    private Integer units;

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedOn")
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "InitialQuantity")
    private Double initialQuantity;

    @Column(name = "IsPartialCylinder")
    private boolean isPartialCylinder;

    @Column(name = "ReturnedTo")
    private String returnedTo;

    @Column(name = "Surplus")
    private Double surplus;

    @Column(name = "GasSurplusDestinationId")
    private Integer gasSurplusDestinationId;

    @Column(name = "GasSurplusTypeId")
    private Integer gasSurplusTypeId;

    @Column(name = "GasCylinderTypeId")
    private Integer gasCylinderTypeId;

    @Column(name = "GasCylinderCapacityId")
    private Integer gasCylinderCapacityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteVisitGasDetailsId() {
        return siteVisitGasDetailsId;
    }

    public void setSiteVisitGasDetailsId(Integer siteVisitGasDetailsId) {
        this.siteVisitGasDetailsId = siteVisitGasDetailsId;
    }

    public Integer getGasSourceTypeId() {
        return gasSourceTypeId;
    }

    public void setGasSourceTypeId(Integer gasSourceTypeId) {
        this.gasSourceTypeId = gasSourceTypeId;
    }

    public String getRefrigerantSourceLocation() {
        return refrigerantSourceLocation;
    }

    public void setRefrigerantSourceLocation(String refrigerantSourceLocation) {
        this.refrigerantSourceLocation = refrigerantSourceLocation;
    }

    public String getBottleNumber() {
        return bottleNumber;
    }

    public void setBottleNumber(String bottleNumber) {
        this.bottleNumber = bottleNumber;
    }

    public Double getBottleQuantity() {
        return bottleQuantity;
    }

    public void setBottleQuantity(Double bottleQuantity) {
        this.bottleQuantity = bottleQuantity;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
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

    public Double getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public boolean isPartialCylinder() {
        return isPartialCylinder;
    }

    public void setPartialCylinder(boolean isPartialCylinder) {
        this.isPartialCylinder = isPartialCylinder;
    }

    public String getReturnedTo() {
        return returnedTo;
    }

    public void setReturnedTo(String returnedTo) {
        this.returnedTo = returnedTo;
    }

    public Double getSurplus() {
        return surplus;
    }

    public void setSurplus(Double surplus) {
        this.surplus = surplus;
    }

    public Integer getGasSurplusDestinationId() {
        return gasSurplusDestinationId;
    }

    public void setGasSurplusDestinationId(Integer gasSurplusDestinationId) {
        this.gasSurplusDestinationId = gasSurplusDestinationId;
    }

    public Integer getGasSurplusTypeId() {
        return gasSurplusTypeId;
    }

    public void setGasSurplusTypeId(Integer gasSurplusTypeId) {
        this.gasSurplusTypeId = gasSurplusTypeId;
    }

    public Integer getGasCylinderTypeId() {
        return gasCylinderTypeId;
    }

    public void setGasCylinderTypeId(Integer gasCylinderTypeId) {
        this.gasCylinderTypeId = gasCylinderTypeId;
    }

    public Integer getGasCylinderCapacityId() {
        return gasCylinderCapacityId;
    }

    public void setGasCylinderCapacityId(Integer gasCylinderCapacityId) {
        this.gasCylinderCapacityId = gasCylinderCapacityId;
    }
}
