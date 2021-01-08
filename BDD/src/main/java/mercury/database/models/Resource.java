package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Resource")
public class Resource {

	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "ResourceProfileId") 
    private Integer resourceProfileId;

    @Column(name = "Name") 
    private String name;

    @Column(name = "KnownAs") 
    private String knownAs;

    @Column(name = "UsesJobClosedown") 
    private Integer usesJobClosedown;

    @Column(name = "AlwaysChargeable") 
    private Integer alwaysChargeable;

    @Column(name = "AutoAssign") 
    private String autoAssign;

    @Column(name = "ReferenceRequired") 
    private String referenceRequired;

    @Column(name = "SlaFromTypeId") 
    private Integer slaFromTypeId;

    @Column(name = "CreatedOn") 
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy") 
    private String createdBy;

    @Column(name = "UpdatedOn") 
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy") 
    private String updatedBy;

    @Column(name = "Active") 
    private String active;

    @Column(name = "EpochId") 
    private Integer epochId;

    @Column(name = "IsVip") 
    private String isVip;

    @Column(name = "CanAttachAsset") 
    private String canAttachAsset;

    @Column(name = "IsGeoFenced") 
    private String isGeoFenced;

    @Column(name = "UsesEngineers") 
    private Integer usesEngineers;

    @Column(name = "HomeStoreId") 
    private Integer homeStoreId;

    @Column(name = "OnlineInvoicingActive") 
    private Integer onlineInvoicingActive;

    @Column(name = "SupplierTCode") 
    private String supplierTCode;

    @Column(name = "PayrollTCode") 
    private String payrollTCode;



    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }


    public Integer getResourceProfileId() {
      return resourceProfileId;
    }

    public void setResourceProfileId(Integer resourceProfileId) {
      this.resourceProfileId = resourceProfileId;
    }


    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }


    public String getKnownAs() {
      return knownAs;
    }

    public void setKnownAs(String knownAs) {
      this.knownAs = knownAs;
    }


    public Integer getUsesJobClosedown() {
      return usesJobClosedown;
    }

    public void setUsesJobClosedown(Integer usesJobClosedown) {
      this.usesJobClosedown = usesJobClosedown;
    }


    public Integer getAlwaysChargeable() {
      return alwaysChargeable;
    }

    public void setAlwaysChargeable(Integer alwaysChargeable) {
      this.alwaysChargeable = alwaysChargeable;
    }


    public String getAutoAssign() {
      return autoAssign;
    }

    public void setAutoAssign(String autoAssign) {
      this.autoAssign = autoAssign;
    }


    public String getReferenceRequired() {
      return referenceRequired;
    }

    public void setReferenceRequired(String referenceRequired) {
      this.referenceRequired = referenceRequired;
    }


    public Integer getSlaFromTypeId() {
      return slaFromTypeId;
    }

    public void setSlaFromTypeId(Integer slaFromTypeId) {
      this.slaFromTypeId = slaFromTypeId;
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


    public String getActive() {
      return active;
    }

    public void setActive(String active) {
      this.active = active;
    }


    public Integer getEpochId() {
      return epochId;
    }

    public void setEpochId(Integer epochId) {
      this.epochId = epochId;
    }


    public String getIsVip() {
      return isVip;
    }

    public void setIsVip(String isVip) {
      this.isVip = isVip;
    }


    public String getCanAttachAsset() {
      return canAttachAsset;
    }

    public void setCanAttachAsset(String canAttachAsset) {
      this.canAttachAsset = canAttachAsset;
    }


    public String getIsGeoFenced() {
      return isGeoFenced;
    }

    public void setIsGeoFenced(String isGeoFenced) {
      this.isGeoFenced = isGeoFenced;
    }


    public Integer getUsesEngineers() {
      return usesEngineers;
    }

    public void setUsesEngineers(Integer usesEngineers) {
      this.usesEngineers = usesEngineers;
    }


    public Integer getHomeStoreId() {
      return homeStoreId;
    }

    public void setHomeStoreId(Integer homeStoreId) {
      this.homeStoreId = homeStoreId;
    }


    public Integer getOnlineInvoicingActive() {
      return onlineInvoicingActive;
    }

    public void setOnlineInvoicingActive(Integer onlineInvoicingActive) {
      this.onlineInvoicingActive = onlineInvoicingActive;
    }


    public String getSupplierTCode() {
      return supplierTCode;
    }

    public void setSupplierTCode(String supplierTCode) {
      this.supplierTCode = supplierTCode;
    }


    public String getPayrollTCode() {
      return payrollTCode;
    }

    public void setPayrollTCode(String payrollTCode) {
      this.payrollTCode = payrollTCode;
    }

}

