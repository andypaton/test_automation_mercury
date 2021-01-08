package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="InitialApproverRule")
public class InitialApproverRule {

    @Id
    @Column(name = "Id") 
    Integer id; 
    
    @Column(name = "ApprovalTypeId") 
    Integer approvalTypeId; 
    
    @Column(name = "FundingRouteId") 
    Integer fundingRouteId; 
    
    @Column(name = "ResourceProfileId") 
    Integer resourceProfileId; 
    
    @Column(name = "MaximumCost") 
    String maximumCost; 
    
    @Column(name = "CreatedOn") 
    String createdOn; 
    
    @Column(name = "CreatedBy") 
    String createdBy; 
    
    @Column(name = "UpdatedOn") 
    String updatedOn; 
    
    @Column(name = "UpdatedBy") 
    String updatedBy; 
    
    @Column(name = "Active") 
    boolean active; 
    
    @Column(name = "RequiresActiveRotaEntries") 
    boolean requiresActiveRotaEntries;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApprovalTypeId() {
        return approvalTypeId;
    }

    public void setApprovalTypeId(Integer approvalTypeId) {
        this.approvalTypeId = approvalTypeId;
    }

    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    public Integer getResourceProfileId() {
        return resourceProfileId;
    }

    public void setResourceProfileId(Integer resourceProfileId) {
        this.resourceProfileId = resourceProfileId;
    }

    public String getMaximumCost() {
        return maximumCost;
    }

    public void setMaximumCost(String maximumCost) {
        this.maximumCost = maximumCost;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
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

    public boolean isRequiresActiveRotaEntries() {
        return requiresActiveRotaEntries;
    }

    public void setRequiresActiveRotaEntries(boolean requiresActiveRotaEntries) {
        this.requiresActiveRotaEntries = requiresActiveRotaEntries;
    } 
    
    
}
