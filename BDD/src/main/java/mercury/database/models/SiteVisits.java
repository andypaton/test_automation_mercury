package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SiteVisits")
public class SiteVisits {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "WorkStartTime")
    private java.sql.Timestamp workStartTime;

    @Column(name = "WorkEndTime")
    private java.sql.Timestamp workEndTime;

    @Column(name = "TravelTime")
    private java.sql.Time travelTime;

    @Column(name = "OverTime")
    private java.sql.Time overTime;

    @Column(name = "Active")
    private String active;

    @Column(name = "RemoteFix")
    private String remoteFix;

    @Column(name = "EtaFrom")
    private java.sql.Timestamp etaFrom;

    @Column(name = "EtaTo")
    private java.sql.Timestamp etaTo;

    @Column(name = "EtaWindowId")
    private Integer etaWindowId;

    @Column(name = "EtaAdvisedToSiteAt")
    private java.sql.Timestamp etaAdvisedToSiteAt;

    @Column(name = "ResourceAssignmentId")
    private Integer resourceAssignmentId;

    @Column(name = "ResourceAssignmentStatusId")
    private Integer resourceAssignmentStatusId;

    @Column(name = "AssetPlantId")
    private Integer assetPlantId;

    @Column(name = "GasTypeId")
    private Integer gasTypeId;

    @Column(name = "GasLeakageCodeId")
    private Integer gasLeakageCodeId;

    @Column(name = "GasLeakageCheckMethodId")
    private Integer gasLeakageCheckMethodId;

    @Column(name = "GasActionReasonId")
    private Integer gasActionReasonId;

    @Column(name = "GasLeakLocationId")
    private Integer gasLeakLocationId;

    @Column(name = "GasSerialNo")
    private String gasSerialNo;

    @Column(name = "GasUsage")
    private String gasUsage;

    @Column(name = "GasCertNumber")
    private String gasCertNumber;

    @Column(name = "GasAdviceNoteNumber")
    private String gasAdviceNoteNumber;

    @Column(name = "GasWarnAdviceType")
    private Integer gasWarnAdviceType;

    @Column(name = "GasWarnAdviceRef")
    private String gasWarnAdviceRef;

    @Column(name = "GasDocsMailed")
    private String gasDocsMailed;

    @Column(name = "EtaAdvisedTo")
    private String etaAdvisedTo;

    @Column(name = "RomecConfirmed")
    private String romecConfirmed;

    @Column(name = "OperationalOnArrival")
    private String operationalOnArrival;

    @Column(name = "StatusOnDeparture")
    private String statusOnDeparture;

    @Column(name = "ElectricalCertSubmitted")
    private String electricalCertSubmitted;

    @Column(name = "RefrigerantUsed")
    private String refrigerantUsed;

    @Column(name = "SicTestPassed")
    private String sicTestPassed;

    @Column(name = "SicLeakTestMethodId")
    private Integer sicLeakTestMethodId;

    @Column(name = "PerformedReg2Work")
    private String performedReg2Work;

    @Column(name = "AffectsStoreTrade")
    private String affectsStoreTrade;

    @Column(name = "AsdaAssist")
    private String asdaAssist;

    @Column(name = "ScannedOnStart")
    private String scannedOnStart;

    @Column(name = "ScannedOnEnd")
    private String scannedOnEnd;

    @Column(name = "AlarmNotIsolated")
    private String alarmNotIsolated;

    @Column(name = "ReasonForNonScan")
    private String reasonForNonScan;

    @Column(name = "NoElecCertNotes")
    private String noElecCertNotes;

    @Column(name = "NoElecCertReason")
    private Integer noElecCertReason;

    @Column(name = "ElectricalCiNumber")
    private Integer electricalCiNumber;

    @Column(name = "ElectricalCertificateType")
    private Integer electricalCertificateType;

    @Column(name = "ReturningToJobReason")
    private Integer returningToJobReason;

    @Column(name = "Notes")
    private String notes;

    @Column(name = "AdditionalResourceRequired")
    private String additionalResourceRequired;

    @Column(name = "ResourceProfile")
    private Integer resourceProfile;

    @Column(name = "ResourceSelection")
    private Integer resourceSelection;

    @Column(name = "QuoteRequired")
    private String quoteRequired;

    @Column(name = "QuoteNotes")
    private String quoteNotes;

    @Column(name = "QuoteAssetId")
    private Integer quoteAssetId;

    @Column(name = "QuoteAssetSubTypeId")
    private Integer quoteAssetSubTypeId;

    @Column(name = "QuoteLocationId")
    private Integer quoteLocationId;

    @Column(name = "QuoteFaultTypeId")
    private Integer quoteFaultTypeId;

    @Column(name = "QuoteScopeOfWork")
    private String quoteScopeOfWork;

    @Column(name = "OtherGasTypeNotes")
    private String otherGasTypeNotes;

    @Column(name = "OtherAssetPlantNotes")
    private String otherAssetPlantNotes;

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedOn")
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "IsLeakTested")
    private String isLeakTested;

    @Column(name = "EtaAcknowledged")
    private String etaAcknowledged;

    @Column(name = "EtaAcknowledgedNotes")
    private String etaAcknowledgedNotes;

    @Column(name = "EtaAcknowledgedRequester")
    private String etaAcknowledgedRequester;

    @Column(name = "OtherAssetNotes")
    private String otherAssetNotes;

    @Column(name = "GasAssetId")
    private Integer gasAssetId;

    @Column(name = "FullChargeCapacity")
    private Float fullChargeCapacity;

    @Column(name = "IsLandlordJob")
    private Boolean isLandlordJob;

    @Column(name = "IsLandlordRequiredOnSite")
    private Boolean isLandlordRequiredOnSite;

    @Column(name = "PotentialLandlordWork")
    private String potentialLandlordWork;

    @Column(name = "GasFaultCodeReasonId")
    private Integer gasFaultCodeReasonId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public java.sql.Timestamp getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(java.sql.Timestamp workStartTime) {
        this.workStartTime = workStartTime;
    }


    public java.sql.Timestamp getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(java.sql.Timestamp workEndTime) {
        this.workEndTime = workEndTime;
    }


    public java.sql.Time getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(java.sql.Time travelTime) {
        this.travelTime = travelTime;
    }


    public java.sql.Time getOverTime() {
        return overTime;
    }

    public void setOverTime(java.sql.Time overTime) {
        this.overTime = overTime;
    }


    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }


    public String getRemoteFix() {
        return remoteFix;
    }

    public void setRemoteFix(String remoteFix) {
        this.remoteFix = remoteFix;
    }


    public java.sql.Timestamp getEtaFrom() {
        return etaFrom;
    }

    public void setEtaFrom(java.sql.Timestamp etaFrom) {
        this.etaFrom = etaFrom;
    }


    public java.sql.Timestamp getEtaTo() {
        return etaTo;
    }

    public void setEtaTo(java.sql.Timestamp etaTo) {
        this.etaTo = etaTo;
    }


    public Integer getEtaWindowId() {
        return etaWindowId;
    }

    public void setEtaWindowId(Integer etaWindowId) {
        this.etaWindowId = etaWindowId;
    }


    public java.sql.Timestamp getEtaAdvisedToSiteAt() {
        return etaAdvisedToSiteAt;
    }

    public void setEtaAdvisedToSiteAt(java.sql.Timestamp etaAdvisedToSiteAt) {
        this.etaAdvisedToSiteAt = etaAdvisedToSiteAt;
    }


    public Integer getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    public void setResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
    }


    public Integer getResourceAssignmentStatusId() {
        return resourceAssignmentStatusId;
    }

    public void setResourceAssignmentStatusId(Integer resourceAssignmentStatusId) {
        this.resourceAssignmentStatusId = resourceAssignmentStatusId;
    }


    public Integer getAssetPlantId() {
        return assetPlantId;
    }

    public void setAssetPlantId(Integer assetPlantId) {
        this.assetPlantId = assetPlantId;
    }


    public Integer getGasTypeId() {
        return gasTypeId;
    }

    public void setGasTypeId(Integer gasTypeId) {
        this.gasTypeId = gasTypeId;
    }


    public Integer getGasLeakageCodeId() {
        return gasLeakageCodeId;
    }

    public void setGasLeakageCodeId(Integer gasLeakageCodeId) {
        this.gasLeakageCodeId = gasLeakageCodeId;
    }


    public Integer getGasLeakageCheckMethodId() {
        return gasLeakageCheckMethodId;
    }

    public void setGasLeakageCheckMethodId(Integer gasLeakageCheckMethodId) {
        this.gasLeakageCheckMethodId = gasLeakageCheckMethodId;
    }


    public Integer getGasActionReasonId() {
        return gasActionReasonId;
    }

    public void setGasActionReasonId(Integer gasActionReasonId) {
        this.gasActionReasonId = gasActionReasonId;
    }


    public Integer getGasLeakLocationId() {
        return gasLeakLocationId;
    }

    public void setGasLeakLocationId(Integer gasLeakLocationId) {
        this.gasLeakLocationId = gasLeakLocationId;
    }


    public String getGasSerialNo() {
        return gasSerialNo;
    }

    public void setGasSerialNo(String gasSerialNo) {
        this.gasSerialNo = gasSerialNo;
    }


    public String getGasUsage() {
        return gasUsage;
    }

    public void setGasUsage(String gasUsage) {
        this.gasUsage = gasUsage;
    }


    public String getGasCertNumber() {
        return gasCertNumber;
    }

    public void setGasCertNumber(String gasCertNumber) {
        this.gasCertNumber = gasCertNumber;
    }


    public String getGasAdviceNoteNumber() {
        return gasAdviceNoteNumber;
    }

    public void setGasAdviceNoteNumber(String gasAdviceNoteNumber) {
        this.gasAdviceNoteNumber = gasAdviceNoteNumber;
    }


    public Integer getGasWarnAdviceType() {
        return gasWarnAdviceType;
    }

    public void setGasWarnAdviceType(Integer gasWarnAdviceType) {
        this.gasWarnAdviceType = gasWarnAdviceType;
    }


    public String getGasWarnAdviceRef() {
        return gasWarnAdviceRef;
    }

    public void setGasWarnAdviceRef(String gasWarnAdviceRef) {
        this.gasWarnAdviceRef = gasWarnAdviceRef;
    }


    public String getGasDocsMailed() {
        return gasDocsMailed;
    }

    public void setGasDocsMailed(String gasDocsMailed) {
        this.gasDocsMailed = gasDocsMailed;
    }


    public String getEtaAdvisedTo() {
        return etaAdvisedTo;
    }

    public void setEtaAdvisedTo(String etaAdvisedTo) {
        this.etaAdvisedTo = etaAdvisedTo;
    }


    public String getRomecConfirmed() {
        return romecConfirmed;
    }

    public void setRomecConfirmed(String romecConfirmed) {
        this.romecConfirmed = romecConfirmed;
    }


    public String getOperationalOnArrival() {
        return operationalOnArrival;
    }

    public void setOperationalOnArrival(String operationalOnArrival) {
        this.operationalOnArrival = operationalOnArrival;
    }


    public String getStatusOnDeparture() {
        return statusOnDeparture;
    }

    public void setStatusOnDeparture(String statusOnDeparture) {
        this.statusOnDeparture = statusOnDeparture;
    }

    public String getElectricalCertSubmitted() {
        return electricalCertSubmitted;
    }

    public void setElectricalCertSubmitted(String electricalCertSubmitted) {
        this.electricalCertSubmitted = electricalCertSubmitted;
    }


    public String getRefrigerantUsed() {
        return refrigerantUsed;
    }

    public void setRefrigerantUsed(String refrigerantUsed) {
        this.refrigerantUsed = refrigerantUsed;
    }


    public String getSicTestPassed() {
        return sicTestPassed;
    }

    public void setSicTestPassed(String sicTestPassed) {
        this.sicTestPassed = sicTestPassed;
    }


    public Integer getSicLeakTestMethodId() {
        return sicLeakTestMethodId;
    }

    public void setSicLeakTestMethodId(Integer sicLeakTestMethodId) {
        this.sicLeakTestMethodId = sicLeakTestMethodId;
    }


    public String getPerformedReg2Work() {
        return performedReg2Work;
    }

    public void setPerformedReg2Work(String performedReg2Work) {
        this.performedReg2Work = performedReg2Work;
    }


    public String getAffectsStoreTrade() {
        return affectsStoreTrade;
    }

    public void setAffectsStoreTrade(String affectsStoreTrade) {
        this.affectsStoreTrade = affectsStoreTrade;
    }


    public String getAsdaAssist() {
        return asdaAssist;
    }

    public void setAsdaAssist(String asdaAssist) {
        this.asdaAssist = asdaAssist;
    }


    public String getScannedOnStart() {
        return scannedOnStart;
    }

    public void setScannedOnStart(String scannedOnStart) {
        this.scannedOnStart = scannedOnStart;
    }


    public String getScannedOnEnd() {
        return scannedOnEnd;
    }

    public void setScannedOnEnd(String scannedOnEnd) {
        this.scannedOnEnd = scannedOnEnd;
    }


    public String getAlarmNotIsolated() {
        return alarmNotIsolated;
    }

    public void setAlarmNotIsolated(String alarmNotIsolated) {
        this.alarmNotIsolated = alarmNotIsolated;
    }


    public String getReasonForNonScan() {
        return reasonForNonScan;
    }

    public void setReasonForNonScan(String reasonForNonScan) {
        this.reasonForNonScan = reasonForNonScan;
    }


    public String getNoElecCertNotes() {
        return noElecCertNotes;
    }

    public void setNoElecCertNotes(String noElecCertNotes) {
        this.noElecCertNotes = noElecCertNotes;
    }


    public Integer getNoElecCertReason() {
        return noElecCertReason;
    }

    public void setNoElecCertReason(Integer noElecCertReason) {
        this.noElecCertReason = noElecCertReason;
    }


    public Integer getElectricalCiNumber() {
        return electricalCiNumber;
    }

    public void setElectricalCiNumber(Integer electricalCiNumber) {
        this.electricalCiNumber = electricalCiNumber;
    }


    public Integer getElectricalCertificateType() {
        return electricalCertificateType;
    }

    public void setElectricalCertificateType(Integer electricalCertificateType) {
        this.electricalCertificateType = electricalCertificateType;
    }


    public Integer getReturningToJobReason() {
        return returningToJobReason;
    }

    public void setReturningToJobReason(Integer returningToJobReason) {
        this.returningToJobReason = returningToJobReason;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getAdditionalResourceRequired() {
        return additionalResourceRequired;
    }

    public void setAdditionalResourceRequired(String additionalResourceRequired) {
        this.additionalResourceRequired = additionalResourceRequired;
    }


    public Integer getResourceProfile() {
        return resourceProfile;
    }

    public void setResourceProfile(Integer resourceProfile) {
        this.resourceProfile = resourceProfile;
    }


    public Integer getResourceSelection() {
        return resourceSelection;
    }

    public void setResourceSelection(Integer resourceSelection) {
        this.resourceSelection = resourceSelection;
    }


    public String getQuoteRequired() {
        return quoteRequired;
    }

    public void setQuoteRequired(String quoteRequired) {
        this.quoteRequired = quoteRequired;
    }


    public String getQuoteNotes() {
        return quoteNotes;
    }

    public void setQuoteNotes(String quoteNotes) {
        this.quoteNotes = quoteNotes;
    }


    public Integer getQuoteAssetId() {
        return quoteAssetId;
    }

    public void setQuoteAssetId(Integer quoteAssetId) {
        this.quoteAssetId = quoteAssetId;
    }


    public Integer getQuoteAssetSubTypeId() {
        return quoteAssetSubTypeId;
    }

    public void setQuoteAssetSubTypeId(Integer quoteAssetSubTypeId) {
        this.quoteAssetSubTypeId = quoteAssetSubTypeId;
    }


    public Integer getQuoteLocationId() {
        return quoteLocationId;
    }

    public void setQuoteLocationId(Integer quoteLocationId) {
        this.quoteLocationId = quoteLocationId;
    }


    public Integer getQuoteFaultTypeId() {
        return quoteFaultTypeId;
    }

    public void setQuoteFaultTypeId(Integer quoteFaultTypeId) {
        this.quoteFaultTypeId = quoteFaultTypeId;
    }


    public String getQuoteScopeOfWork() {
        return quoteScopeOfWork;
    }

    public void setQuoteScopeOfWork(String quoteScopeOfWork) {
        this.quoteScopeOfWork = quoteScopeOfWork;
    }


    public String getOtherGasTypeNotes() {
        return otherGasTypeNotes;
    }

    public void setOtherGasTypeNotes(String otherGasTypeNotes) {
        this.otherGasTypeNotes = otherGasTypeNotes;
    }


    public String getOtherAssetPlantNotes() {
        return otherAssetPlantNotes;
    }

    public void setOtherAssetPlantNotes(String otherAssetPlantNotes) {
        this.otherAssetPlantNotes = otherAssetPlantNotes;
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


    public String getIsLeakTested() {
        return isLeakTested;
    }

    public void setIsLeakTested(String isLeakTested) {
        this.isLeakTested = isLeakTested;
    }


    public String getEtaAcknowledged() {
        return etaAcknowledged;
    }

    public void setEtaAcknowledged(String etaAcknowledged) {
        this.etaAcknowledged = etaAcknowledged;
    }


    public String getEtaAcknowledgedNotes() {
        return etaAcknowledgedNotes;
    }

    public void setEtaAcknowledgedNotes(String etaAcknowledgedNotes) {
        this.etaAcknowledgedNotes = etaAcknowledgedNotes;
    }


    public String getEtaAcknowledgedRequester() {
        return etaAcknowledgedRequester;
    }

    public void setEtaAcknowledgedRequester(String etaAcknowledgedRequester) {
        this.etaAcknowledgedRequester = etaAcknowledgedRequester;
    }

    public String getOtherAssetNotes() {
        return otherAssetNotes;
    }

    public void setOtherAssetNotes(String otherAssetNotes) {
        this.otherAssetNotes = otherAssetNotes;
    }

    public Integer getGasAssetId() {
        return gasAssetId;
    }

    public void setGasAssetId(Integer gasAssetId) {
        this.gasAssetId = gasAssetId;
    }

    public Float getFullChargeCapacity() {
        return fullChargeCapacity;
    }

    public void setFullChargeCapacity(Float fullChargeCapacity) {
        this.fullChargeCapacity = fullChargeCapacity;
    }

    public Boolean getIsLandlordJob() {
        return isLandlordJob;
    }

    public void setIsLandlordJob(Boolean isLandlordJob) {
        this.isLandlordJob = isLandlordJob;
    }

    public Boolean getIsLandlordRequiredOnSite() {
        return isLandlordRequiredOnSite;
    }

    public void setIsLandlordRequiredOnSite(Boolean isLandlordRequiredOnSite) {
        this.isLandlordRequiredOnSite = isLandlordRequiredOnSite;
    }

    public String getPotentialLandlordWork() {
        return potentialLandlordWork;
    }

    public void setPotentialLandlordWork(String potentialLandlordWork) {
        this.potentialLandlordWork = potentialLandlordWork;
    }

    public Integer getGasFaultCodeReasonId() {
        return gasFaultCodeReasonId;
    }

    public void setGasFaultCodeReasonId(Integer gasFaultCodeReasonId) {
        this.gasFaultCodeReasonId = gasFaultCodeReasonId;
    }

}