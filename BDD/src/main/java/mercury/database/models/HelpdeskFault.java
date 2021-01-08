package mercury.database.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class HelpdeskFault {

    @Id
    @Column(name = "fld_int_ID")
    Integer fld_int_ID;

    @Column(name = "fld_int_HelpDeskFaultStageID")
    Integer fld_int_HelpDeskFaultStageID;

    @Column(name = "fld_str_FacilitiesLogbookNumber")
    String fld_str_FacilitiesLogbookNumber;

    @Column(name = "fld_int_StoreID")
    Integer fld_int_StoreID;

    @Column(name = "fld_int_AssetID")
    Integer fld_int_AssetID;

    @Column(name = "fld_str_ContactInfo")
    String fld_str_ContactInfo;

    @Column(name = "fld_str_ETAReportedTo")
    String fld_str_ETAReportedTo;

    @Column(name = "fld_str_FaultReference")
    String fld_str_FaultReference;

    @Column(name = "fld_int_CompanyID")
    Integer fld_int_CompanyID;

    @Column(name = "fld_str_LockingUser")
    String fld_str_LockingUser;

    @Column(name = "fld_dat_LockingTime")
    Date fld_dat_LockingTime;

    @Column(name = "fld_int_SelectedResourceType")
    Integer fld_int_SelectedResourceType;

    @Column(name = "fld_int_SelectedResource")
    Integer fld_int_SelectedResource;

    @Column(name = "fld_int_SelectedPhone")
    Integer fld_int_SelectedPhone;

    @Column(name = "fld_int_LastEditedBy")
    Integer fld_int_LastEditedBy;

    @Column(name = "fld_dat_LastEditedDate")
    Date fld_dat_LastEditedDate;

    @Column(name = "fld_bit_SRRequired")
    boolean fld_bit_SRRequired;

    @Column(name = "fld_str_LockingUser_Proc")
    String fld_str_LockingUser_Proc;

    @Column(name = "fld_dat_LockingTime_Proc")
    Date fld_dat_LockingTime_Proc;

    @Column(name = "fld_int_DownTimeMinutes")
    Integer fld_int_DownTimeMinutes;

    @Column(name = "fld_bit_AIMSAutoEscalationJob")
    boolean fld_bit_AIMSAutoEscalationJob;

    @Column(name = "fld_int_AIMSFailedRouterTypeID")
    Integer fld_int_AIMSFailedRouterTypeID;

    @Column(name = "fld_str_FirstNote")
    String fld_str_FirstNote;

    @Column(name = "fld_bit_PendingElecCert")
    boolean fld_bit_PendingElecCert;

    @Column(name = "fld_int_CallerTypeID")
    Integer fld_int_CallerTypeID;

    @Column(name = "fld_int_CallerDeptID")
    Integer fld_int_CallerDeptID;

    @Column(name = "fld_int_StoreContactTypeID")
    Integer fld_int_StoreContactTypeID;

    @Column(name = "fld_int_storeContactDeptID")
    Integer fld_int_storeContactDeptID;

    @Column(name = "fld_str_CallerInfo")
    String fld_str_CallerInfo;

    @Column(name = "fld_str_StoreContactInfo")
    String fld_str_StoreContactInfo;

    @Column(name = "fld_int_CallerResourceID")
    Integer fld_int_CallerResourceID;

    @Column(name = "fld_int_PPMLineID")
    Integer fld_int_PPMLineID;

    @Column(name = "fld_bit_QuoteJob")
    boolean fld_bit_QuoteJob;

    @Column(name = "fld_int_OriginalJobID")
    Integer fld_int_OriginalJobID;

    @Column(name = "fld_bit_HasLinkedJob")
    Integer fld_bit_HasLinkedJob;

    @Column(name = "fld_bit_StoreManned")
    boolean fld_bit_StoreManned;

    @Column(name = "fld_str_mannedUntil")
    String fld_str_mannedUntil;

    @Column(name = "fld_int_numGasBottles")
    Integer fld_int_numGasBottles;

    @Column(name = "fld_bit_GasBottlePPM")
    boolean fld_bit_GasBottlePPM;

    @Column(name = "fld_bit_WarrantyJob")
    boolean fld_bit_WarrantyJob;

    @Column(name = "fld_bit_RecallJob")
    boolean fld_bit_RecallJob;

    @Column(name = "fld_bit_OutOfHoursAttendanceRequested")
    boolean fld_bit_OutOfHoursAttendanceRequested;

    @Column(name = "fld_bit_SIC")
    boolean fld_bit_SIC;

    @Column(name = "fld_int_ParentHelpDeskFaultID")
    Integer fld_int_ParentHelpDeskFaultID;

    @Column(name = "fld_int_RecallID")
    Integer fld_int_RecallID;

    @Column(name = "fld_int_RemedialPPMLineID")
    Integer fld_int_RemedialPPMLineID;

    @Column(name = "fld_dat_checksumUpdated")
    Date fld_dat_checksumUpdated;

    @Column(name = "fld_str_FaultType")
    String fld_str_FaultType;

    @Column(name = "fld_str_Priority")
    String fld_str_Priority;

    @Column(name = "fld_str_SubTypeClassification")
    String fld_str_SubTypeClassification;

    @Column(name = "fld_str_Location")
    String fld_str_Location;

    @Column(name = "fld_str_AssetTag")
    String fld_str_AssetTag;

    public Integer getFld_int_ID() {
        return fld_int_ID;
    }

    public void setFld_int_ID(Integer fld_int_ID) {
        this.fld_int_ID = fld_int_ID;
    }

    public Integer getFld_int_HelpDeskFaultStageID() {
        return fld_int_HelpDeskFaultStageID;
    }

    public void setFld_int_HelpDeskFaultStageID(Integer fld_int_HelpDeskFaultStageID) {
        this.fld_int_HelpDeskFaultStageID = fld_int_HelpDeskFaultStageID;
    }

    public String getFld_str_FacilitiesLogbookNumber() {
        return fld_str_FacilitiesLogbookNumber;
    }

    public void setFld_str_FacilitiesLogbookNumber(String fld_str_FacilitiesLogbookNumber) {
        this.fld_str_FacilitiesLogbookNumber = fld_str_FacilitiesLogbookNumber;
    }

    public Integer getFld_int_StoreID() {
        return fld_int_StoreID;
    }

    public void setFld_int_StoreID(Integer fld_int_StoreID) {
        this.fld_int_StoreID = fld_int_StoreID;
    }

    public Integer getFld_int_AssetID() {
        return fld_int_AssetID;
    }

    public void setFld_int_AssetID(Integer fld_int_AssetID) {
        this.fld_int_AssetID = fld_int_AssetID;
    }

    public String getFld_str_ContactInfo() {
        return fld_str_ContactInfo;
    }

    public void setFld_str_ContactInfo(String fld_str_ContactInfo) {
        this.fld_str_ContactInfo = fld_str_ContactInfo;
    }

    public String getFld_str_ETAReportedTo() {
        return fld_str_ETAReportedTo;
    }

    public void setFld_str_ETAReportedTo(String fld_str_ETAReportedTo) {
        this.fld_str_ETAReportedTo = fld_str_ETAReportedTo;
    }

    public String getFld_str_FaultReference() {
        return fld_str_FaultReference;
    }

    public void setFld_str_FaultReference(String fld_str_FaultReference) {
        this.fld_str_FaultReference = fld_str_FaultReference;
    }

    public Integer getFld_int_CompanyID() {
        return fld_int_CompanyID;
    }

    public void setFld_int_CompanyID(Integer fld_int_CompanyID) {
        this.fld_int_CompanyID = fld_int_CompanyID;
    }

    public String getFld_str_LockingUser() {
        return fld_str_LockingUser;
    }

    public void setFld_str_LockingUser(String fld_str_LockingUser) {
        this.fld_str_LockingUser = fld_str_LockingUser;
    }

    public Date getFld_dat_LockingTime() {
        return fld_dat_LockingTime;
    }

    public void setFld_dat_LockingTime(Date fld_dat_LockingTime) {
        this.fld_dat_LockingTime = fld_dat_LockingTime;
    }

    public Integer getFld_int_SelectedResourceType() {
        return fld_int_SelectedResourceType;
    }

    public void setFld_int_SelectedResourceType(Integer fld_int_SelectedResourceType) {
        this.fld_int_SelectedResourceType = fld_int_SelectedResourceType;
    }

    public Integer getFld_int_SelectedResource() {
        return fld_int_SelectedResource;
    }

    public void setFld_int_SelectedResource(Integer fld_int_SelectedResource) {
        this.fld_int_SelectedResource = fld_int_SelectedResource;
    }

    public Integer getFld_int_SelectedPhone() {
        return fld_int_SelectedPhone;
    }

    public void setFld_int_SelectedPhone(Integer fld_int_SelectedPhone) {
        this.fld_int_SelectedPhone = fld_int_SelectedPhone;
    }

    public Integer getFld_int_LastEditedBy() {
        return fld_int_LastEditedBy;
    }

    public void setFld_int_LastEditedBy(Integer fld_int_LastEditedBy) {
        this.fld_int_LastEditedBy = fld_int_LastEditedBy;
    }

    public Date getFld_dat_LastEditedDate() {
        return fld_dat_LastEditedDate;
    }

    public void setFld_dat_LastEditedDate(Date fld_dat_LastEditedDate) {
        this.fld_dat_LastEditedDate = fld_dat_LastEditedDate;
    }

    public boolean isFld_bit_SRRequired() {
        return fld_bit_SRRequired;
    }

    public void setFld_bit_SRRequired(boolean fld_bit_SRRequired) {
        this.fld_bit_SRRequired = fld_bit_SRRequired;
    }

    public String getFld_str_LockingUser_Proc() {
        return fld_str_LockingUser_Proc;
    }

    public void setFld_str_LockingUser_Proc(String fld_str_LockingUser_Proc) {
        this.fld_str_LockingUser_Proc = fld_str_LockingUser_Proc;
    }

    public Date getFld_dat_LockingTime_Proc() {
        return fld_dat_LockingTime_Proc;
    }

    public void setFld_dat_LockingTime_Proc(Date fld_dat_LockingTime_Proc) {
        this.fld_dat_LockingTime_Proc = fld_dat_LockingTime_Proc;
    }

    public Integer getFld_int_DownTimeMinutes() {
        return fld_int_DownTimeMinutes;
    }

    public void setFld_int_DownTimeMinutes(Integer fld_int_DownTimeMinutes) {
        this.fld_int_DownTimeMinutes = fld_int_DownTimeMinutes;
    }

    public boolean isFld_bit_AIMSAutoEscalationJob() {
        return fld_bit_AIMSAutoEscalationJob;
    }

    public void setFld_bit_AIMSAutoEscalationJob(boolean fld_bit_AIMSAutoEscalationJob) {
        this.fld_bit_AIMSAutoEscalationJob = fld_bit_AIMSAutoEscalationJob;
    }

    public Integer getFld_int_AIMSFailedRouterTypeID() {
        return fld_int_AIMSFailedRouterTypeID;
    }

    public void setFld_int_AIMSFailedRouterTypeID(Integer fld_int_AIMSFailedRouterTypeID) {
        this.fld_int_AIMSFailedRouterTypeID = fld_int_AIMSFailedRouterTypeID;
    }

    public String getFld_str_FirstNote() {
        return fld_str_FirstNote;
    }

    public void setFld_str_FirstNote(String fld_str_FirstNote) {
        this.fld_str_FirstNote = fld_str_FirstNote;
    }

    public boolean isFld_bit_PendingElecCert() {
        return fld_bit_PendingElecCert;
    }

    public void setFld_bit_PendingElecCert(boolean fld_bit_PendingElecCert) {
        this.fld_bit_PendingElecCert = fld_bit_PendingElecCert;
    }

    public Integer getFld_int_CallerTypeID() {
        return fld_int_CallerTypeID;
    }

    public void setFld_int_CallerTypeID(Integer fld_int_CallerTypeID) {
        this.fld_int_CallerTypeID = fld_int_CallerTypeID;
    }

    public Integer getFld_int_CallerDeptID() {
        return fld_int_CallerDeptID;
    }

    public void setFld_int_CallerDeptID(Integer fld_int_CallerDeptID) {
        this.fld_int_CallerDeptID = fld_int_CallerDeptID;
    }

    public Integer getFld_int_StoreContactTypeID() {
        return fld_int_StoreContactTypeID;
    }

    public void setFld_int_StoreContactTypeID(Integer fld_int_StoreContactTypeID) {
        this.fld_int_StoreContactTypeID = fld_int_StoreContactTypeID;
    }

    public Integer getFld_int_storeContactDeptID() {
        return fld_int_storeContactDeptID;
    }

    public void setFld_int_storeContactDeptID(Integer fld_int_storeContactDeptID) {
        this.fld_int_storeContactDeptID = fld_int_storeContactDeptID;
    }

    public String getFld_str_CallerInfo() {
        return fld_str_CallerInfo;
    }

    public void setFld_str_CallerInfo(String fld_str_CallerInfo) {
        this.fld_str_CallerInfo = fld_str_CallerInfo;
    }

    public String getFld_str_StoreContactInfo() {
        return fld_str_StoreContactInfo;
    }

    public void setFld_str_StoreContactInfo(String fld_str_StoreContactInfo) {
        this.fld_str_StoreContactInfo = fld_str_StoreContactInfo;
    }

    public Integer getFld_int_CallerResourceID() {
        return fld_int_CallerResourceID;
    }

    public void setFld_int_CallerResourceID(Integer fld_int_CallerResourceID) {
        this.fld_int_CallerResourceID = fld_int_CallerResourceID;
    }

    public Integer getFld_int_PPMLineID() {
        return fld_int_PPMLineID;
    }

    public void setFld_int_PPMLineID(Integer fld_int_PPMLineID) {
        this.fld_int_PPMLineID = fld_int_PPMLineID;
    }

    public boolean isFld_bit_QuoteJob() {
        return fld_bit_QuoteJob;
    }

    public void setFld_bit_QuoteJob(boolean fld_bit_QuoteJob) {
        this.fld_bit_QuoteJob = fld_bit_QuoteJob;
    }

    public Integer getFld_int_OriginalJobID() {
        return fld_int_OriginalJobID;
    }

    public void setFld_int_OriginalJobID(Integer fld_int_OriginalJobID) {
        this.fld_int_OriginalJobID = fld_int_OriginalJobID;
    }

    public Integer isFld_bit_HasLinkedJob() {
        return fld_bit_HasLinkedJob;
    }

    public void setFld_bit_HasLinkedJob(Integer fld_bit_HasLinkedJob) {
        this.fld_bit_HasLinkedJob = fld_bit_HasLinkedJob;
    }

    public boolean isFld_bit_StoreManned() {
        return fld_bit_StoreManned;
    }

    public void setFld_bit_StoreManned(boolean fld_bit_StoreManned) {
        this.fld_bit_StoreManned = fld_bit_StoreManned;
    }

    public String getFld_str_mannedUntil() {
        return fld_str_mannedUntil;
    }

    public void setFld_str_mannedUntil(String fld_str_mannedUntil) {
        this.fld_str_mannedUntil = fld_str_mannedUntil;
    }

    public Integer getFld_int_numGasBottles() {
        return fld_int_numGasBottles;
    }

    public void setFld_int_numGasBottles(Integer fld_int_numGasBottles) {
        this.fld_int_numGasBottles = fld_int_numGasBottles;
    }

    public boolean isFld_bit_GasBottlePPM() {
        return fld_bit_GasBottlePPM;
    }

    public void setFld_bit_GasBottlePPM(boolean fld_bit_GasBottlePPM) {
        this.fld_bit_GasBottlePPM = fld_bit_GasBottlePPM;
    }

    public boolean isFld_bit_WarrantyJob() {
        return fld_bit_WarrantyJob;
    }

    public void setFld_bit_WarrantyJob(boolean fld_bit_WarrantyJob) {
        this.fld_bit_WarrantyJob = fld_bit_WarrantyJob;
    }

    public boolean isFld_bit_RecallJob() {
        return fld_bit_RecallJob;
    }

    public void setFld_bit_RecallJob(boolean fld_bit_RecallJob) {
        this.fld_bit_RecallJob = fld_bit_RecallJob;
    }

    public boolean isFld_bit_OutOfHoursAttendanceRequested() {
        return fld_bit_OutOfHoursAttendanceRequested;
    }

    public void setFld_bit_OutOfHoursAttendanceRequested(boolean fld_bit_OutOfHoursAttendanceRequested) {
        this.fld_bit_OutOfHoursAttendanceRequested = fld_bit_OutOfHoursAttendanceRequested;
    }

    public boolean isFld_bit_SIC() {
        return fld_bit_SIC;
    }

    public void setFld_bit_SIC(boolean fld_bit_SIC) {
        this.fld_bit_SIC = fld_bit_SIC;
    }

    public Integer getFld_int_ParentHelpDeskFaultID() {
        return fld_int_ParentHelpDeskFaultID;
    }

    public void setFld_int_ParentHelpDeskFaultID(Integer fld_int_ParentHelpDeskFaultID) {
        this.fld_int_ParentHelpDeskFaultID = fld_int_ParentHelpDeskFaultID;
    }

    public Integer getFld_int_RecallID() {
        return fld_int_RecallID;
    }

    public void setFld_int_RecallID(Integer fld_int_RecallID) {
        this.fld_int_RecallID = fld_int_RecallID;
    }

    public Integer getFld_int_RemedialPPMLineID() {
        return fld_int_RemedialPPMLineID;
    }

    public void setFld_int_RemedialPPMLineID(Integer fld_int_RemedialPPMLineID) {
        this.fld_int_RemedialPPMLineID = fld_int_RemedialPPMLineID;
    }

    public Date getFld_dat_checksumUpdated() {
        return fld_dat_checksumUpdated;
    }

    public void setFld_dat_checksumUpdated(Date fld_dat_checksumUpdated) {
        this.fld_dat_checksumUpdated = fld_dat_checksumUpdated;
    }

    public String getFld_str_FaultType() {
        return fld_str_FaultType;
    }

    public void setFld_str_FaultType(String fld_str_FaultType) {
        this.fld_str_FaultType = fld_str_FaultType;
    }

    public String getFld_str_Priority() {
        return fld_str_Priority;
    }

    public void setFld_str_Priority(String fld_str_Priority) {
        this.fld_str_Priority = fld_str_Priority;
    }

    public String getFld_str_SubTypeClassification() {
        return fld_str_SubTypeClassification;
    }

    public void setFld_str_SubTypeClassification(String fld_str_SubTypeClassification) {
        this.fld_str_SubTypeClassification = fld_str_SubTypeClassification;
    }

    public String getFld_str_Location() {
        return fld_str_Location;
    }

    public void setFld_str_Location(String fld_str_Location) {
        this.fld_str_Location = fld_str_Location;
    }

    public String getFld_str_AssetTag() {
        return fld_str_AssetTag;
    }

    public void setFld_str_AssetTag(String fld_str_AssetTag) {
        this.fld_str_AssetTag = fld_str_AssetTag;
    }
    
}
