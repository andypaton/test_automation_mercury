package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GasApplianceType")
public class GasApplianceType {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "IsRemoteSystem")
    private boolean isRemoteSystem;

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

    @Column(name = "DefaultMaxCharge")
    private String defaultMaxCharge;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRemoteSystem() {
        return isRemoteSystem;
    }

    public void setRemoteSystem(boolean isRemoteSystem) {
        this.isRemoteSystem = isRemoteSystem;
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

    public String getDefaultMaxCharge() {
        return defaultMaxCharge;
    }

    public void setDefaultMaxCharge(String defaultMaxCharge) {
        this.defaultMaxCharge = defaultMaxCharge;
    }

}
