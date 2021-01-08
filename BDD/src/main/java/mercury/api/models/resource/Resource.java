package mercury.api.models.resource;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "knownAs", "resourceProfileId", "resourceProfileName", "resourceProfileAlias", "emailAddresses",
    "phoneNumbers", "autoAssign", "canAttachAsset", "isVip", "hasIPad", "workingHours", "username", "teamIds",
    "active", "alwaysChargeable", "referenceRequired", "usesResourceProfileWorkingHours", "blobId",
    "permanentSiteIds", "contractorSites", "epochId", "userProfileId", "usesEngineers", "isGeofenced",
    "homeStoreId", "onlineInvoicingActive", "supplierTCode", "payrollTCode", "visiblePartOrdering", "receivesUpdateEmails" })
public class Resource {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("knownAs")
    private String knownAs;

    @JsonProperty("resourceProfileId")
    private Integer resourceProfileId;

    @JsonProperty("resourceProfileName")
    private String resourceProfileName;

    @JsonProperty("resourceProfileAlias")
    private String resourceProfileAlias;

    @JsonProperty("emailAddresses")
    private List<EmailAddress> emailAddresses = null;

    @JsonProperty("phoneNumbers")
    private List<PhoneNumber> phoneNumbers = null;

    @JsonProperty("autoAssign")
    private Boolean autoAssign;

    @JsonProperty("canAttachAsset")
    private Boolean canAttachAsset;

    @JsonProperty("isVip")
    private Boolean isVip;

    @JsonProperty("hasIPad")
    private Boolean hasIPad;

    @JsonProperty("workingHours")
    private List<WorkingHours> workingHours = null;

    @JsonProperty("username")
    private String username;

    @JsonProperty("teamIds")
    private List<Integer> teamIds = null;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("alwaysChargeable")
    private Boolean alwaysChargeable;

    @JsonProperty("referenceRequired")
    private Boolean referenceRequired;

    @JsonProperty("usesResourceProfileWorkingHours")
    private Boolean usesResourceProfileWorkingHours;

    @JsonProperty("blobId")
    private Integer blobId;

    @JsonProperty("permanentSiteIds")
    private List<Integer> permanentSiteIds = null;

    @JsonProperty("contractorSites")
    private List<ContractorSite> contractorSites = null;

    @JsonProperty("epochId")
    private Integer epochId;

    @JsonProperty("userProfileId")
    private Integer userProfileId;

    @JsonProperty("usesEngineers")
    private Boolean usesEngineers;

    @JsonProperty("isGeofenced")
    private Boolean isGeofenced;

    @JsonProperty("homeStoreId")
    private Integer homeStoreId;

    @JsonProperty("onlineInvoicingActive")
    private Boolean onlineInvoicingActive;

    @JsonProperty("supplierTCode")
    private String supplierTCode;

    @JsonProperty("payrollTCode")
    private String payrollTCode;

    @JsonProperty("visiblePartOrdering")
    private String visiblePartOrdering;

    @JsonProperty("receivesUpdateEmails")
    private String receivesUpdateEmails;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("knownAs")
    public Object getKnownAs() {
        return knownAs;
    }

    @JsonProperty("knownAs")
    public void setKnownAs(String knownAs) {
        this.knownAs = knownAs;
    }

    @JsonProperty("resourceProfileId")
    public Integer getResourceProfileId() {
        return resourceProfileId;
    }

    @JsonProperty("resourceProfileId")
    public void setResourceProfileId(Integer resourceProfileId) {
        this.resourceProfileId = resourceProfileId;
    }

    @JsonProperty("resourceProfileName")
    public String getResourceProfileName() {
        return resourceProfileName;
    }

    @JsonProperty("resourceProfileName")
    public void setResourceProfileName(String resourceProfileName) {
        this.resourceProfileName = resourceProfileName;
    }

    @JsonProperty("resourceProfileAlias")
    public String getResourceProfileAlias() {
        return resourceProfileAlias;
    }

    @JsonProperty("resourceProfileAlias")
    public void setResourceProfileAlias(String resourceProfileAlias) {
        this.resourceProfileAlias = resourceProfileAlias;
    }

    @JsonProperty("emailAddresses")
    public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    @JsonProperty("emailAddresses")
    public void setEmailAddresses(List<EmailAddress> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    @JsonProperty("phoneNumbers")
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    @JsonProperty("phoneNumbers")
    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @JsonProperty("autoAssign")
    public Boolean getAutoAssign() {
        return autoAssign;
    }

    @JsonProperty("autoAssign")
    public void setAutoAssign(Boolean autoAssign) {
        this.autoAssign = autoAssign;
    }

    @JsonProperty("canAttachAsset")
    public Boolean getCanAttachAsset() {
        return canAttachAsset;
    }

    @JsonProperty("canAttachAsset")
    public void setCanAttachAsset(Boolean canAttachAsset) {
        this.canAttachAsset = canAttachAsset;
    }

    @JsonProperty("isVip")
    public Boolean getIsVip() {
        return isVip;
    }

    @JsonProperty("isVip")
    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    @JsonProperty("hasIPad")
    public Boolean getHasIPad() {
        return hasIPad;
    }

    @JsonProperty("hasIPad")
    public void setHasIPad(Boolean hasIPad) {
        this.hasIPad = hasIPad;
    }

    @JsonProperty("workingHours")
    public List<WorkingHours> getWorkingHours() {
        return workingHours;
    }

    @JsonProperty("workingHours")
    public void setWorkingHours(List<WorkingHours> workingHours) {
        this.workingHours = workingHours;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("teamIds")
    public List<Integer> getTeamIds() {
        return teamIds;
    }

    @JsonProperty("teamIds")
    public void setTeamIds(List<Integer> teamIds) {
        this.teamIds = teamIds;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("alwaysChargeable")
    public Object getAlwaysChargeable() {
        return alwaysChargeable;
    }

    @JsonProperty("alwaysChargeable")
    public void setAlwaysChargeable(Boolean alwaysChargeable) {
        this.alwaysChargeable = alwaysChargeable;
    }

    @JsonProperty("referenceRequired")
    public Boolean getReferenceRequired() {
        return referenceRequired;
    }

    @JsonProperty("referenceRequired")
    public void setReferenceRequired(Boolean referenceRequired) {
        this.referenceRequired = referenceRequired;
    }

    @JsonProperty("usesResourceProfileWorkingHours")
    public Boolean getUsesResourceProfileWorkingHours() {
        return usesResourceProfileWorkingHours;
    }

    @JsonProperty("usesResourceProfileWorkingHours")
    public void setUsesResourceProfileWorkingHours(Boolean usesResourceProfileWorkingHours) {
        this.usesResourceProfileWorkingHours = usesResourceProfileWorkingHours;
    }

    @JsonProperty("blobId")
    public Object getBlobId() {
        return blobId;
    }

    @JsonProperty("blobId")
    public void setBlobId(Integer blobId) {
        this.blobId = blobId;
    }

    @JsonProperty("permanentSiteIds")
    public List<Integer> getPermanentSiteIds() {
        return permanentSiteIds;
    }

    @JsonProperty("permanentSiteIds")
    public void setPermanentSiteIds(List<Integer> permanentSiteIds) {
        this.permanentSiteIds = permanentSiteIds;
    }

    @JsonProperty("contractorSites")
    public List<ContractorSite> getContractorSites() {
        return contractorSites;
    }

    @JsonProperty("contractorSites")
    public void setContractorSites(List<ContractorSite> contractorSites) {
        this.contractorSites = contractorSites;
    }

    @JsonProperty("epochId")
    public Integer getEpochId() {
        return epochId;
    }

    @JsonProperty("epochId")
    public void setEpochId(Integer epochId) {
        this.epochId = epochId;
    }

    @JsonProperty("userProfileId")
    public Integer getUserProfileId() {
        return userProfileId;
    }

    @JsonProperty("userProfileId")
    public void setUserProfileId(Integer userProfileId) {
        this.userProfileId = userProfileId;
    }

    @JsonProperty("usesEngineers")
    public Boolean getUsesEngineers() {
        return usesEngineers;
    }

    @JsonProperty("usesEngineers")
    public void setUsesEngineers(Boolean usesEngineers) {
        this.usesEngineers = usesEngineers;
    }

    @JsonProperty("isGeofenced")
    public Boolean getIsGeofenced() {
        return isGeofenced;
    }

    @JsonProperty("isGeofenced")
    public void setIsGeofenced(Boolean isGeofenced) {
        this.isGeofenced = isGeofenced;
    }

    @JsonProperty("homeStoreId")
    public Integer getHomeStoreId() {
        return homeStoreId;
    }

    @JsonProperty("homeStoreId")
    public void setHomeStoreId(Integer homeStoreId) {
        this.homeStoreId = homeStoreId;
    }

    @JsonProperty("onlineInvoicingActive")
    public Boolean getOnlineInvoicingActive() {
        return onlineInvoicingActive;
    }

    @JsonProperty("onlineInvoicingActive")
    public void setOnlineInvoicingActive(Boolean onlineInvoicingActive) {
        this.onlineInvoicingActive = onlineInvoicingActive;
    }

    @JsonProperty("supplierTCode")
    public Object getSupplierTCode() {
        return supplierTCode;
    }

    @JsonProperty("supplierTCode")
    public void setSupplierTCode(String supplierTCode) {
        this.supplierTCode = supplierTCode;
    }

    @JsonProperty("payrollTCode")
    public String getPayrollTCode() {
        return payrollTCode;
    }

    @JsonProperty("payrollTCode")
    public void setPayrollTCode(String payrollTCode) {
        this.payrollTCode = payrollTCode;
    }

    @JsonProperty("visiblePartOrdering")
    public String getVisiblePartOrdering() {
        return visiblePartOrdering;
    }

    @JsonProperty("visiblePartOrdering")
    public void setVisiblePartOrdering(String visiblePartOrdering) {
        this.visiblePartOrdering = visiblePartOrdering;
    }

    @JsonProperty("receivesUpdateEmails")
    public String getReceivesUpdateEmails() {
        return receivesUpdateEmails;
    }

    @JsonProperty("receivesUpdateEmails")
    public void setReceivesUpdateEmails(String receivesUpdateEmails) {
        this.receivesUpdateEmails = receivesUpdateEmails;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("knownAs", knownAs)
                .append("resourceProfileId", resourceProfileId).append("resourceProfileName", resourceProfileName)
                .append("emailAddresses", emailAddresses).append("phoneNumbers", phoneNumbers)
                .append("autoAssign", autoAssign).append("canAttachAsset", canAttachAsset).append("isVip", isVip)
                .append("hasIPad", hasIPad).append("workingHours", workingHours).append("username", username)
                .append("teamIds", teamIds).append("active", active).append("alwaysChargeable", alwaysChargeable)
                .append("referenceRequired", referenceRequired)
                .append("usesResourceProfileWorkingHours", usesResourceProfileWorkingHours).append("blobId", blobId)
                .append("permanentSiteIds", permanentSiteIds).append("contractorSites", contractorSites)
                .append("epochId", epochId).append("userProfileId", userProfileId)
                .append("usesEngineers", usesEngineers).append("isGeofenced", isGeofenced)
                .append("homeStoreId", homeStoreId).append("onlineInvoicingActive", onlineInvoicingActive)
                .append("supplierTCode", supplierTCode).append("payrollTCode", payrollTCode)
                .append("visiblePartOrdering", visiblePartOrdering)
                .append("receivesUpdateEmails", receivesUpdateEmails)
                .toString();
    }

}