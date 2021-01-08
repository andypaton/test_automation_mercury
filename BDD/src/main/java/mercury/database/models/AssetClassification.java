package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AssetClassification")
public class AssetClassification {

    @Id
    @Column(name = "Id") 
    private String id;

    @Column(name = "SiteId") 
    private Integer siteId;

    @Column(name = "ResourceId") 
    private Integer resourceId;
    
    @Column(name = "AssetClassificationId") 
    private Integer assetClassificationId;

    @Column(name = "Priority") 
    private Integer priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
