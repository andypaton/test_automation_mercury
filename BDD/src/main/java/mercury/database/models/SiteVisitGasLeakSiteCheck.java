package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SiteVisitGasLeakSiteCheck")
public class SiteVisitGasLeakSiteCheck {

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

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy")
    private String createdBy;

    @Column(name = "UpdatedOn")
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy")
    private String updatedBy;

    @Column(name = "PrimaryComponentInformation")
    private String primaryComponentInformation;

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

    public Integer getGasLeakLocationId() {
        return gasLeakLocationId;
    }

    public void setGasLeakLocationId(Integer gasLeakLocationId) {
        this.gasLeakLocationId = gasLeakLocationId;
    }

    public Integer getGasLeakSubLocationId() {
        return gasLeakSubLocationId;
    }

    public void setGasLeakSubLocationId(Integer gasLeakSubLocationId) {
        this.gasLeakSubLocationId = gasLeakSubLocationId;
    }

    public Integer getGasLeakSiteStatusId() {
        return gasLeakSiteStatusId;
    }

    public void setGasLeakSiteStatusId(Integer gasLeakSiteStatusId) {
        this.gasLeakSiteStatusId = gasLeakSiteStatusId;
    }

    public Integer getGasLeakInitialTestId() {
        return gasLeakInitialTestId;
    }

    public void setGasLeakInitialTestId(Integer gasLeakInitialTestId) {
        this.gasLeakInitialTestId = gasLeakInitialTestId;
    }

    public Integer getGasLeakFollowUpTestId() {
        return gasLeakFollowUpTestId;
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

    public String getPrimaryComponentInformation() {
        return primaryComponentInformation;
    }

    public void setActive(String primaryComponentInformation) {
        this.primaryComponentInformation = primaryComponentInformation;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
