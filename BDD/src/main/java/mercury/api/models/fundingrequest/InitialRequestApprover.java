package mercury.api.models.fundingrequest;


import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "knownAs",
    "resourceProfileId",
    "resourceProfileName",
    "emailAddresses",
    "phoneNumbers",
    "autoAssign",
    "canAttachAsset",
    "isVip",
    "hasIPad",
    "workingHours",
    "username",
    "teamIds",
    "active",
    "alwaysChargeable",
    "referenceRequired",
    "usesResourceProfileWorkingHours",
    "blobId",
    "permanentSiteIds",
    "contractorSites",
    "epochId",
    "userProfileId",
    "usesEngineers",
    "isGeofenced",
    "homeStoreId",
    "onlineInvoicingActive",
    "supplierTCode",
    "payrollTCode",
    "visiblePartOrdering",
    "receivesUpdateEmails"
})
public class InitialRequestApprover extends modelBase<InitialRequestApprover> {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("knownAs")
    private Object knownAs;
    
    @JsonProperty("resourceProfileId")
    private Integer resourceProfileId;
    
    @JsonProperty("resourceProfileName")
    private String resourceProfileName;
    
    @JsonProperty("emailAddresses")
    private List<Object> emailAddresses = null;
    
    @JsonProperty("phoneNumbers")
    private List<Object> phoneNumbers = null;
    
    @JsonProperty("autoAssign")
    private Boolean autoAssign;
    
    @JsonProperty("canAttachAsset")
    private Boolean canAttachAsset;
    
    @JsonProperty("isVip")
    private Boolean isVip;
    
    @JsonProperty("hasIPad")
    private Boolean hasIPad;
    
    @JsonProperty("workingHours")
    private List<Object> workingHours = null;
    
    @JsonProperty("username")
    private Object username;
    
    @JsonProperty("teamIds")
    private Object teamIds;
    
    @JsonProperty("active")
    private Boolean active;
    
    @JsonProperty("alwaysChargeable")
    private Boolean alwaysChargeable;
    
    @JsonProperty("referenceRequired")
    private Boolean referenceRequired;
    
    @JsonProperty("usesResourceProfileWorkingHours")
    private Boolean usesResourceProfileWorkingHours;
    
    @JsonProperty("blobId")
    private Object blobId;
    
    @JsonProperty("permanentSiteIds")
    private Object permanentSiteIds;
    
    @JsonProperty("contractorSites")
    private Object contractorSites;
    
    @JsonProperty("epochId")
    private Integer epochId;
    
    @JsonProperty("userProfileId")
    private Object userProfileId;
    
    @JsonProperty("usesEngineers")
    private Boolean usesEngineers;
    
    @JsonProperty("isGeofenced")
    private Boolean isGeofenced;
    
    @JsonProperty("homeStoreId")
    private Object homeStoreId;
    
    @JsonProperty("onlineInvoicingActive")
    private Boolean onlineInvoicingActive;
    
    @JsonProperty("supplierTCode")
    private Object supplierTCode;
    
    @JsonProperty("payrollTCode")
    private Object payrollTCode;
    
    @JsonProperty("visiblePartOrdering")
    private Boolean visiblePartOrdering;
    
    @JsonProperty("receivesUpdateEmails")
    private Boolean receivesUpdateEmails;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public InitialRequestApprover withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public InitialRequestApprover withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("knownAs")
    public Object getKnownAs() {
        return knownAs;
    }

    @JsonProperty("knownAs")
    public void setKnownAs(Object knownAs) {
        this.knownAs = knownAs;
    }

    public InitialRequestApprover withKnownAs(Object knownAs) {
        this.knownAs = knownAs;
        return this;
    }

    @JsonProperty("resourceProfileId")
    public Integer getResourceProfileId() {
        return resourceProfileId;
    }

    @JsonProperty("resourceProfileId")
    public void setResourceProfileId(Integer resourceProfileId) {
        this.resourceProfileId = resourceProfileId;
    }

    public InitialRequestApprover withResourceProfileId(Integer resourceProfileId) {
        this.resourceProfileId = resourceProfileId;
        return this;
    }

    @JsonProperty("resourceProfileName")
    public String getResourceProfileName() {
        return resourceProfileName;
    }

    @JsonProperty("resourceProfileName")
    public void setResourceProfileName(String resourceProfileName) {
        this.resourceProfileName = resourceProfileName;
    }

    public InitialRequestApprover withResourceProfileName(String resourceProfileName) {
        this.resourceProfileName = resourceProfileName;
        return this;
    }

    @JsonProperty("emailAddresses")
    public List<Object> getEmailAddresses() {
        return emailAddresses;
    }

    @JsonProperty("emailAddresses")
    public void setEmailAddresses(List<Object> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public InitialRequestApprover withEmailAddresses(List<Object> emailAddresses) {
        this.emailAddresses = emailAddresses;
        return this;
    }

    @JsonProperty("phoneNumbers")
    public List<Object> getPhoneNumbers() {
        return phoneNumbers;
    }

    @JsonProperty("phoneNumbers")
    public void setPhoneNumbers(List<Object> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public InitialRequestApprover withPhoneNumbers(List<Object> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        return this;
    }

    @JsonProperty("autoAssign")
    public Boolean getAutoAssign() {
        return autoAssign;
    }

    @JsonProperty("autoAssign")
    public void setAutoAssign(Boolean autoAssign) {
        this.autoAssign = autoAssign;
    }

    public InitialRequestApprover withAutoAssign(Boolean autoAssign) {
        this.autoAssign = autoAssign;
        return this;
    }

    @JsonProperty("canAttachAsset")
    public Boolean getCanAttachAsset() {
        return canAttachAsset;
    }

    @JsonProperty("canAttachAsset")
    public void setCanAttachAsset(Boolean canAttachAsset) {
        this.canAttachAsset = canAttachAsset;
    }

    public InitialRequestApprover withCanAttachAsset(Boolean canAttachAsset) {
        this.canAttachAsset = canAttachAsset;
        return this;
    }

    @JsonProperty("isVip")
    public Boolean getIsVip() {
        return isVip;
    }

    @JsonProperty("isVip")
    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public InitialRequestApprover withIsVip(Boolean isVip) {
        this.isVip = isVip;
        return this;
    }

    @JsonProperty("hasIPad")
    public Boolean getHasIPad() {
        return hasIPad;
    }

    @JsonProperty("hasIPad")
    public void setHasIPad(Boolean hasIPad) {
        this.hasIPad = hasIPad;
    }

    public InitialRequestApprover withHasIPad(Boolean hasIPad) {
        this.hasIPad = hasIPad;
        return this;
    }

    @JsonProperty("workingHours")
    public List<Object> getWorkingHours() {
        return workingHours;
    }

    @JsonProperty("workingHours")
    public void setWorkingHours(List<Object> workingHours) {
        this.workingHours = workingHours;
    }

    public InitialRequestApprover withWorkingHours(List<Object> workingHours) {
        this.workingHours = workingHours;
        return this;
    }

    @JsonProperty("username")
    public Object getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(Object username) {
        this.username = username;
    }

    public InitialRequestApprover withUsername(Object username) {
        this.username = username;
        return this;
    }

    @JsonProperty("teamIds")
    public Object getTeamIds() {
        return teamIds;
    }

    @JsonProperty("teamIds")
    public void setTeamIds(Object teamIds) {
        this.teamIds = teamIds;
    }

    public InitialRequestApprover withTeamIds(Object teamIds) {
        this.teamIds = teamIds;
        return this;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    public InitialRequestApprover withActive(Boolean active) {
        this.active = active;
        return this;
    }

    @JsonProperty("alwaysChargeable")
    public Boolean getAlwaysChargeable() {
        return alwaysChargeable;
    }

    @JsonProperty("alwaysChargeable")
    public void setAlwaysChargeable(Boolean alwaysChargeable) {
        this.alwaysChargeable = alwaysChargeable;
    }

    public InitialRequestApprover withAlwaysChargeable(Boolean alwaysChargeable) {
        this.alwaysChargeable = alwaysChargeable;
        return this;
    }

    @JsonProperty("referenceRequired")
    public Boolean getReferenceRequired() {
        return referenceRequired;
    }

    @JsonProperty("referenceRequired")
    public void setReferenceRequired(Boolean referenceRequired) {
        this.referenceRequired = referenceRequired;
    }

    public InitialRequestApprover withReferenceRequired(Boolean referenceRequired) {
        this.referenceRequired = referenceRequired;
        return this;
    }

    @JsonProperty("usesResourceProfileWorkingHours")
    public Boolean getUsesResourceProfileWorkingHours() {
        return usesResourceProfileWorkingHours;
    }

    @JsonProperty("usesResourceProfileWorkingHours")
    public void setUsesResourceProfileWorkingHours(Boolean usesResourceProfileWorkingHours) {
        this.usesResourceProfileWorkingHours = usesResourceProfileWorkingHours;
    }

    public InitialRequestApprover withUsesResourceProfileWorkingHours(Boolean usesResourceProfileWorkingHours) {
        this.usesResourceProfileWorkingHours = usesResourceProfileWorkingHours;
        return this;
    }

    @JsonProperty("blobId")
    public Object getBlobId() {
        return blobId;
    }

    @JsonProperty("blobId")
    public void setBlobId(Object blobId) {
        this.blobId = blobId;
    }

    public InitialRequestApprover withBlobId(Object blobId) {
        this.blobId = blobId;
        return this;
    }

    @JsonProperty("permanentSiteIds")
    public Object getPermanentSiteIds() {
        return permanentSiteIds;
    }

    @JsonProperty("permanentSiteIds")
    public void setPermanentSiteIds(Object permanentSiteIds) {
        this.permanentSiteIds = permanentSiteIds;
    }

    public InitialRequestApprover withPermanentSiteIds(Object permanentSiteIds) {
        this.permanentSiteIds = permanentSiteIds;
        return this;
    }

    @JsonProperty("contractorSites")
    public Object getContractorSites() {
        return contractorSites;
    }

    @JsonProperty("contractorSites")
    public void setContractorSites(Object contractorSites) {
        this.contractorSites = contractorSites;
    }

    public InitialRequestApprover withContractorSites(Object contractorSites) {
        this.contractorSites = contractorSites;
        return this;
    }

    @JsonProperty("epochId")
    public Integer getEpochId() {
        return epochId;
    }

    @JsonProperty("epochId")
    public void setEpochId(Integer epochId) {
        this.epochId = epochId;
    }

    public InitialRequestApprover withEpochId(Integer epochId) {
        this.epochId = epochId;
        return this;
    }

    @JsonProperty("userProfileId")
    public Object getUserProfileId() {
        return userProfileId;
    }

    @JsonProperty("userProfileId")
    public void setUserProfileId(Object userProfileId) {
        this.userProfileId = userProfileId;
    }

    public InitialRequestApprover withUserProfileId(Object userProfileId) {
        this.userProfileId = userProfileId;
        return this;
    }

    @JsonProperty("usesEngineers")
    public Boolean getUsesEngineers() {
        return usesEngineers;
    }

    @JsonProperty("usesEngineers")
    public void setUsesEngineers(Boolean usesEngineers) {
        this.usesEngineers = usesEngineers;
    }

    public InitialRequestApprover withUsesEngineers(Boolean usesEngineers) {
        this.usesEngineers = usesEngineers;
        return this;
    }

    @JsonProperty("isGeofenced")
    public Boolean getIsGeofenced() {
        return isGeofenced;
    }

    @JsonProperty("isGeofenced")
    public void setIsGeofenced(Boolean isGeofenced) {
        this.isGeofenced = isGeofenced;
    }

    public InitialRequestApprover withIsGeofenced(Boolean isGeofenced) {
        this.isGeofenced = isGeofenced;
        return this;
    }

    @JsonProperty("homeStoreId")
    public Object getHomeStoreId() {
        return homeStoreId;
    }

    @JsonProperty("homeStoreId")
    public void setHomeStoreId(Object homeStoreId) {
        this.homeStoreId = homeStoreId;
    }

    public InitialRequestApprover withHomeStoreId(Object homeStoreId) {
        this.homeStoreId = homeStoreId;
        return this;
    }

    @JsonProperty("onlineInvoicingActive")
    public Boolean getOnlineInvoicingActive() {
        return onlineInvoicingActive;
    }

    @JsonProperty("onlineInvoicingActive")
    public void setOnlineInvoicingActive(Boolean onlineInvoicingActive) {
        this.onlineInvoicingActive = onlineInvoicingActive;
    }

    public InitialRequestApprover withOnlineInvoicingActive(Boolean onlineInvoicingActive) {
        this.onlineInvoicingActive = onlineInvoicingActive;
        return this;
    }

    @JsonProperty("supplierTCode")
    public Object getSupplierTCode() {
        return supplierTCode;
    }

    @JsonProperty("supplierTCode")
    public void setSupplierTCode(Object supplierTCode) {
        this.supplierTCode = supplierTCode;
    }

    public InitialRequestApprover withSupplierTCode(Object supplierTCode) {
        this.supplierTCode = supplierTCode;
        return this;
    }

    @JsonProperty("payrollTCode")
    public Object getPayrollTCode() {
        return payrollTCode;
    }

    @JsonProperty("payrollTCode")
    public void setPayrollTCode(Object payrollTCode) {
        this.payrollTCode = payrollTCode;
    }

    public InitialRequestApprover withPayrollTCode(Object payrollTCode) {
        this.payrollTCode = payrollTCode;
        return this;
    }

    @JsonProperty("visiblePartOrdering")
    public Boolean getVisiblePartOrdering() {
        return visiblePartOrdering;
    }

    @JsonProperty("visiblePartOrdering")
    public void setVisiblePartOrdering(Boolean visiblePartOrdering) {
        this.visiblePartOrdering = visiblePartOrdering;
    }

    public InitialRequestApprover withVisiblePartOrdering(Boolean visiblePartOrdering) {
        this.visiblePartOrdering = visiblePartOrdering;
        return this;
    }

    @JsonProperty("receivesUpdateEmails")
    public Boolean getReceivesUpdateEmails() {
        return receivesUpdateEmails;
    }

    @JsonProperty("receivesUpdateEmails")
    public void setReceivesUpdateEmails(Boolean receivesUpdateEmails) {
        this.receivesUpdateEmails = receivesUpdateEmails;
    }

    public InitialRequestApprover withReceivesUpdateEmails(Boolean receivesUpdateEmails) {
        this.receivesUpdateEmails = receivesUpdateEmails;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("knownAs", knownAs).append("resourceProfileId", resourceProfileId).append("resourceProfileName", resourceProfileName).append("emailAddresses", emailAddresses).append("phoneNumbers", phoneNumbers).append("autoAssign", autoAssign).append("canAttachAsset", canAttachAsset).append("isVip", isVip).append("hasIPad", hasIPad).append("workingHours", workingHours).append("username", username).append("teamIds", teamIds).append("active", active).append("alwaysChargeable", alwaysChargeable).append("referenceRequired", referenceRequired).append("usesResourceProfileWorkingHours", usesResourceProfileWorkingHours).append("blobId", blobId).append("permanentSiteIds", permanentSiteIds).append("contractorSites", contractorSites).append("epochId", epochId).append("userProfileId", userProfileId).append("usesEngineers", usesEngineers).append("isGeofenced", isGeofenced).append("homeStoreId", homeStoreId).append("onlineInvoicingActive", onlineInvoicingActive).append("supplierTCode", supplierTCode).append("payrollTCode", payrollTCode).append("visiblePartOrdering", visiblePartOrdering).append("receivesUpdateEmails", receivesUpdateEmails).toString();
    }

}