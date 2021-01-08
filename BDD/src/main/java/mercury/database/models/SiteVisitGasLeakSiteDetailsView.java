package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SiteVisitGasLeakSiteDetailsView")
public class SiteVisitGasLeakSiteDetailsView {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "SiteVisitId")
    private Integer siteVisitId;

    @Column(name = "GasLeakLocationId")
    private Integer gasLeakLocationId;

    @Column(name = "GasLeakSubLocationId")
    private Integer gasLeakSubLocationId;

    @Column(name = "GasLeakSiteStatusId")
    private Integer gasLeakSiteStatusId;

    @Column(name = "GasLeakInitialTestId")
    private Integer gasLeakInitialTestId;

    @Column(name = "GasLeakFollowUpTestId")
    private Integer gasLeakFollowUpTestId;

    @Column(name = "GasLeakLocation")
    private String gasLeakLocation;

    @Column(name = "GasLeakSubLocation")
    private String gasLeakSubLocation;

    @Column(name = "GasLeakSiteStatus")
    private String gasLeakSiteStatus;

    @Column(name = "GasLeakInitialTest")
    private String gasLeakInitialTest;

    @Column(name = "GasLeakFollowUpTest")
    private String gasLeakFollowUpTest;

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedOn")
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "Active")
    private boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteVisitId() {
        return siteVisitId;
    }

    public void setSiteVisitId(Integer siteVisitId) {
        this.siteVisitId = siteVisitId;
    }


    public String getGasLeakLocation() {
        return gasLeakLocation;
    }

    public void setGasLeakLocation(String gasLeakLocation) {
        this.gasLeakLocation = gasLeakLocation;
    }

    public String getGasLeakSubLocation() {
        return gasLeakSubLocation;
    }

    public void setGasLeakSubLocation(String gasLeakSubLocation) {
        this.gasLeakSubLocation = gasLeakSubLocation;
    }

    public String getGasLeakSiteStatus() {
        return gasLeakSiteStatus;
    }

    public void setGasLeakSiteStatus(String gasLeakSiteStatus) {
        this.gasLeakSiteStatus = gasLeakSiteStatus;
    }

    public String getGasLeakInitialTest() {
        return gasLeakInitialTest;
    }

    public void setGasLeakInitialTest(String gasLeakInitialTest) {
        this.gasLeakInitialTest = gasLeakInitialTest;
    }

    public String getGasLeakFollowUpTest() {
        return gasLeakFollowUpTest;
    }

    public void setGasLeakFollowUpTest(String gasLeakFollowUpTest) {
        this.gasLeakFollowUpTest = gasLeakFollowUpTest;
    }

    public Integer getGasLeakLocationId() {
        return gasLeakLocationId;
    }

    public void setGasLeakLocationId(Integer gasLeakLocationId) {
        this.gasLeakLocationId = gasLeakLocationId;
    }

    public void setGasLeakSubLocationId(Integer gasLeakSubLocationId) {
        this.gasLeakSubLocationId = gasLeakSubLocationId;
    }

    public void setGasLeakSiteStatusId(Integer gasLeakSiteStatusId) {
        this.gasLeakSiteStatusId = gasLeakSiteStatusId;
    }

    public void setGasLeakInitialTestId(Integer gasLeakInitialTestId) {
        this.gasLeakInitialTestId = gasLeakInitialTestId;
    }

    public void setGasLeakFollowUpTestId(Integer gasLeakFollowUpTestId) {
        this.gasLeakFollowUpTestId = gasLeakFollowUpTestId;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
