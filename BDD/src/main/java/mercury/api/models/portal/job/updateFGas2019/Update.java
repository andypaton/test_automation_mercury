package mercury.api.models.portal.job.updateFGas2019;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__RequestVerificationToken",
    "JobDetails",
    "RemoteFix",
    "TravelTime",
    "OperationalOnArrival",
    "StatusOnDeparture",
    "workStartTime",
    "TimeSpent",
    "workEndTime",
    "OverTime",
    "gsanIssued",
    "UsesGas",
    "LeakCheckPerformed",
    "applianceType",
    "applianceIdentification",
    "applianceInformation",
    "receiverLevelRecorded",
    "applianceIdentificationGasType",
    "refrigerantType",
    "modifiedMaximumCharge",
    "modifiedMaximumChargeNotes",
    "leakCheckStatus",
    "leakCheckMethod",
    "leakCheckResultType",
    "gasDetails",
    "ResourceAssignmentStatusId",
    "ReturningToJobReason",
    "etaDate",
    "formEtaWindowId",
    "ResourceAssignmentId",
    "Eta",
    "EtaWindowId",
    "AssetCondition",
    "AdditionalResourceRequired",
    "QuoteRequired",
    "AsbestosRegisterChecked"

})
public class Update  extends modelBase<Update>{

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("JobDetails")
    private JobDetails jobDetails;

    @JsonProperty("AsbestosRegisterChecked")
    private String asbestosRegisterChecked;

    @JsonProperty("RemoteFix")
    private String remoteFix;

    @JsonProperty("TravelTime")
    private String travelTime;

    @JsonProperty("OperationalOnArrival")
    private String operationalOnArrival;

    @JsonProperty("StatusOnDeparture")
    private String statusOnDeparture;

    @JsonProperty("workStartTime")
    private String workStartTime;

    @JsonProperty("TimeSpent")
    private String timeSpent;

    @JsonProperty("workEndTime")
    private String workEndTime;

    @JsonProperty("OverTime")
    private String overTime;

    @JsonProperty("gsanIssued")
    private String gsanIssued;

    @JsonProperty("UsesGas")
    private String usesGas;

    @JsonProperty("LeakCheckPerformed")
    private String leakCheckPerformed;

    @JsonProperty("applianceType")
    private String applianceType;

    @JsonProperty("applianceInformation")
    private String applianceInformation;

    @JsonProperty("applianceIdentification")
    private String applianceIdentification;

    @JsonProperty("receiverLevelRecorded")
    private String receiverLevelRecorded;

    @JsonProperty("applianceIdentificationGasType")
    private String applianceIdentificationGasType;

    @JsonProperty("refrigerantType")
    private String refrigerantType;

    @JsonProperty("modifiedMaximumCharge")
    private String modifiedMaximumCharge;

    @JsonProperty("modifiedMaximumChargeNotes")
    private String modifiedMaximumChargeNotes;

    @JsonProperty("leakCheckStatus")
    private String leakCheckStatus;

    @JsonProperty("leakCheckMethod")
    private String leakCheckMethod;

    @JsonProperty("leakCheckResultType")
    private String leakCheckResultType;

    @JsonProperty("gasDetails")
    private GasDetails gasDetails;

    @JsonProperty("ResourceAssignmentStatusId")
    private String resourceAssignmentStatusId;

    @JsonProperty("ReturningToJobReason")
    private String returningToJobReason;

    @JsonProperty("etaDate")
    private String etaDate;

    @JsonProperty("formEtaWindowId")
    private String formEtaWindowId;

    @JsonProperty("ResourceAssignmentId")
    private String resourceAssignmentId;

    @JsonProperty("Eta")
    private String eta;

    @JsonProperty("EtaWindowId")
    private String etaWindowId;

    @JsonProperty("AssetCondition")
    private String assetCondition;

    @JsonProperty("AdditionalResourceRequired")
    private String additionalResourceRequired;

    @JsonProperty("QuoteRequired")
    private String quoteRequired;

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    @JsonProperty("JobDetails")
    public JobDetails getJobDetails() {
        return jobDetails;
    }

    @JsonProperty("JobDetails")
    public void setJobDetails(JobDetails jobDetails) {
        this.jobDetails = jobDetails;
    }

    @JsonProperty("AsbestosRegisterChecked")
    public String getAsbestosRegisterChecked() {
        return asbestosRegisterChecked;
    }

    @JsonProperty("AsbestosRegisterChecked")
    public void setAsbestosRegisterChecked(String asbestosRegisterChecked) {
        this.asbestosRegisterChecked = asbestosRegisterChecked;
    }

    @JsonProperty("RemoteFix")
    public String getRemoteFix() {
        return remoteFix;
    }

    @JsonProperty("RemoteFix")
    public void setRemoteFix(String remoteFix) {
        this.remoteFix = remoteFix;
    }

    @JsonProperty("TravelTime")
    public String getTravelTime() {
        return travelTime;
    }

    @JsonProperty("TravelTime")
    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    @JsonProperty("OperationalOnArrival")
    public String getOperationalOnArrival() {
        return operationalOnArrival;
    }

    @JsonProperty("OperationalOnArrival")
    public void setOperationalOnArrival(String operationalOnArrival) {
        this.operationalOnArrival = operationalOnArrival;
    }

    @JsonProperty("StatusOnDeparture")
    public String getStatusOnDeparture() {
        return statusOnDeparture;
    }

    @JsonProperty("StatusOnDeparture")
    public void setStatusOnDeparture(String statusOnDeparture) {
        this.statusOnDeparture = statusOnDeparture;
    }

    @JsonProperty("workStartTime")
    public String getWorkStartTime() {
        return workStartTime;
    }

    @JsonProperty("workStartTime")
    public void setWorkStartTime(String workStartTime) {
        this.workStartTime = workStartTime;
    }

    @JsonProperty("TimeSpent")
    public String getTimeSpent() {
        return timeSpent;
    }

    @JsonProperty("TimeSpent")
    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    @JsonProperty("workEndTime")
    public String getWorkEndTime() {
        return workEndTime;
    }

    @JsonProperty("workEndTime")
    public void setWorkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }

    @JsonProperty("gsanIssued")
    public String getGsanIssued() {
        return gsanIssued;
    }

    @JsonProperty("gsanIssued")
    public void setGsanIssued(String gsanIssued) {
        this.gsanIssued = gsanIssued;
    }

    @JsonProperty("OverTime")
    public String getOverTime() {
        return overTime;
    }

    @JsonProperty("OverTime")
    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    @JsonProperty("UsesGas")
    public String getUsesGas() {
        return usesGas;
    }

    @JsonProperty("UsesGas")
    public void setUsesGas(String usesGas) {
        this.usesGas = usesGas;
    }

    @JsonProperty("LeakCheckPerformed")
    public String getLeakCheckPerformed() {
        return leakCheckPerformed;
    }

    @JsonProperty("LeakCheckPerformed")
    public void setLeakCheckPerformed(String leakCheckPerformed) {
        this.leakCheckPerformed = leakCheckPerformed;
    }

    @JsonProperty("applianceType")
    public String getApplianceType() {
        return applianceType;
    }

    @JsonProperty("applianceType")
    public void setApplianceType(String applianceType) {
        this.applianceType = applianceType;
    }

    @JsonProperty("applianceInformation")
    public String getApplianceInformation() {
        return applianceInformation;
    }

    @JsonProperty("applianceInformation")
    public void setApplianceInformation(String applianceInformation) {
        this.applianceInformation = applianceInformation;
    }

    @JsonProperty("applianceIdentification")
    public String getApplianceIdentification() {
        return applianceIdentification;
    }

    @JsonProperty("applianceIdentification")
    public void setApplianceIdentification(String applianceIdentification) {
        this.applianceIdentification = applianceIdentification;
    }

    @JsonProperty("receiverLevelRecorded")
    public String getReceiverLevelRecorded() {
        return receiverLevelRecorded;
    }

    @JsonProperty("receiverLevelRecorded")
    public void setReceiverLevelRecorded(String receiverLevelRecorded) {
        this.receiverLevelRecorded = receiverLevelRecorded;
    }

    @JsonProperty("applianceIdentificationGasType")
    public String getApplianceIdentificationGasType() {
        return applianceIdentificationGasType;
    }

    @JsonProperty("applianceIdentificationGasType")
    public void setApplianceIdentificationGasType(String applianceIdentificationGasType) {
        this.applianceIdentificationGasType = applianceIdentificationGasType;
    }

    @JsonProperty("refrigerantType")
    public String getRefrigerantType() {
        return refrigerantType;
    }

    @JsonProperty("refrigerantType")
    public void setRefrigerantType(String refrigerantType) {
        this.refrigerantType = refrigerantType;
    }

    @JsonProperty("modifiedMaximumCharge")
    public String getModifiedMaximumCharge() {
        return modifiedMaximumCharge;
    }

    @JsonProperty("modifiedMaximumCharge")
    public void setModifiedMaximumCharge(String modifiedMaximumCharge) {
        this.modifiedMaximumCharge = modifiedMaximumCharge;
    }

    @JsonProperty("modifiedMaximumChargeNotes")
    public String getModifiedMaximumChargeNotes() {
        return modifiedMaximumChargeNotes;
    }

    @JsonProperty("modifiedMaximumChargeNotes")
    public void setModifiedMaximumChargeNotes(String modifiedMaximumChargeNotes) {
        this.modifiedMaximumChargeNotes = modifiedMaximumChargeNotes;
    }

    @JsonProperty("leakCheckStatus")
    public String getLeakCheckStatus() {
        return leakCheckStatus;
    }

    @JsonProperty("leakCheckStatus")
    public void setLeakCheckStatus(String leakCheckStatus) {
        this.leakCheckStatus = leakCheckStatus;
    }

    @JsonProperty("leakCheckMethod")
    public String getLeakCheckMethod() {
        return leakCheckMethod;
    }

    @JsonProperty("leakCheckMethod")
    public void setLeakCheckMethod(String leakCheckMethod) {
        this.leakCheckMethod = leakCheckMethod;
    }

    @JsonProperty("leakCheckResultType")
    public String getLeakCheckResultType() {
        return leakCheckResultType;
    }

    @JsonProperty("leakCheckResultType")
    public void setLeakCheckResultType(String leakCheckResultType) {
        this.leakCheckResultType = leakCheckResultType;
    }

    @JsonProperty("gasDetails")
    public GasDetails getGasDetails() {
        return gasDetails;
    }

    @JsonProperty("gasDetails")
    public void setGasDetails(GasDetails gasDetails) {
        this.gasDetails = gasDetails;
    }

    @JsonProperty("ResourceAssignmentStatusId")
    public String getResourceAssignmentStatusId() {
        return resourceAssignmentStatusId;
    }

    @JsonProperty("ResourceAssignmentStatusId")
    public void setResourceAssignmentStatusId(String resourceAssignmentStatusId) {
        this.resourceAssignmentStatusId = resourceAssignmentStatusId;
    }

    @JsonProperty("ReturningToJobReason")
    public String getReturningToJobReason() {
        return returningToJobReason;
    }

    @JsonProperty("ReturningToJobReason")
    public void setReturningToJobReason(String returningToJobReason) {
        this.returningToJobReason = returningToJobReason;
    }

    @JsonProperty("etaDate")
    public String getEtaDate() {
        return etaDate;
    }

    @JsonProperty("etaDate")
    public void setEtaDate(String etaDate) {
        this.etaDate = etaDate;
    }

    @JsonProperty("formEtaWindowId")
    public String getFormEtaWindowId() {
        return formEtaWindowId;
    }

    @JsonProperty("formEtaWindowId")
    public void setFormEtaWindowId(String formEtaWindowId) {
        this.formEtaWindowId = formEtaWindowId;
    }

    @JsonProperty("ResourceAssignmentId")
    public String getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    @JsonProperty("ResourceAssignmentId")
    public void setResourceAssignmentId(String resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
    }

    @JsonProperty("Eta")
    public String getEta() {
        return eta;
    }

    @JsonProperty("Eta")
    public void setEta(String eta) {
        this.eta = eta;
    }

    @JsonProperty("EtaWindowId")
    public String getEtaWindowId() {
        return etaWindowId;
    }

    @JsonProperty("EtaWindowId")
    public void setEtaWindowId(String etaWindowId) {
        this.etaWindowId = etaWindowId;
    }

    @JsonProperty("AssetCondition")
    public String getAssetCondition() {
        return assetCondition;
    }

    @JsonProperty("AssetCondition")
    public void setAssetCondition(String assetCondition) {
        this.assetCondition = assetCondition;
    }

    @JsonProperty("AdditionalResourceRequired")
    public String getAdditionalResourceRequired() {
        return additionalResourceRequired;
    }

    @JsonProperty("AdditionalResourceRequired")
    public void setAdditionalResourceRequired(String additionalResourceRequired) {
        this.additionalResourceRequired = additionalResourceRequired;
    }

    @JsonProperty("QuoteRequired")
    public String getQuoteRequired() {
        return quoteRequired;
    }

    @JsonProperty("QuoteRequired")
    public void setQuoteRequired(String quoteRequired) {
        this.quoteRequired = quoteRequired;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken)
                .append("remoteFix", remoteFix).append("travelTime", travelTime)
                .append("operationalOnArrival", operationalOnArrival)
                .append("statusOnDeparture", statusOnDeparture)
                .append("workStartTime", workStartTime)
                .append("workEndTime", workEndTime)
                .append("gsanIssued", gsanIssued)
                .append("usesGas", usesGas)
                .append("applianceType", applianceType)
                .append("applianceIdentification", applianceIdentification)
                .append("applianceInformation", applianceInformation)
                .append("receiverLevelRecorded", receiverLevelRecorded)
                .append("refrigerantType", refrigerantType)
                .append("leakCheckStatus", leakCheckStatus)
                .append("leakCheckMethod", leakCheckMethod)
                .append("leakCheckResultType", leakCheckResultType)
                .append("resourceAssignmentStatusId", resourceAssignmentStatusId)
                .append("returningToJobReason", returningToJobReason)
                .append("etaDate", etaDate)
                .append("formEtaWindowId", formEtaWindowId)
                .append("resourceAssignmentId", resourceAssignmentId)
                .append("eta", eta)
                .append("etaWindowId", etaWindowId)
                .append("assetCondition", assetCondition)
                .append("additionalResourceRequired", additionalResourceRequired)
                .append("quoteRequired", quoteRequired)
                .append("asbestosRegisterChecked", asbestosRegisterChecked)
                .toString();
    }
}
