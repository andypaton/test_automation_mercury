package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SiteVisitsGasUsage")
public class SiteVisitsGasUsage {
    
    @Id
    @Column(name = "Id") 
    private Integer id;
    
    @Column(name = "SiteVisitId") 
    private Integer siteVisitId;
    
    @Column(name = "BottleNumber") 
    private String bottleNumber;
    
    @Column(name = "BottleQuantity") 
    private float bottleQuantity;

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

    public String getBottleNumber() {
        return bottleNumber;
    }

    public void setBottleNumber(String bottleNumber) {
        this.bottleNumber = bottleNumber;
    }

    public float getBottleQuantity() {
        return bottleQuantity;
    }

    public void setBottleQuantity(float bottleQuantity) {
        this.bottleQuantity = bottleQuantity;
    }

}
