package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="QuoteLines")
public class QuoteLine {
	
	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "ProjectQuoteId") 
    private Integer projectQuoteId;
    
    @Column(name = "PartCode")
    private String partCode;
    
    @Column(name = "PartDescription")
    private String partDescription;
    
    @Column(name = "PartCodeDetail")
    private String partCodeDetail;
    
    @Column(name = "UnitPrice")
    private Float unitPrice;
        
    @Column(name = "Quantity")
    private Integer quantity;
    
    @Column(name = "TotalCost")
    private Float totalCost;
    
    @Column(name = "CreatedDate")
    private java.sql.Timestamp createdDate;
    
    @Column(name = "DoesPartRequireSupplierOrder")
    private Boolean doesPartRequireSupplierOrder;
    
    @Column(name = "PartExistsInPriceBook")
    private Boolean partExistsInPriceBook;
    
    @Column(name = "PartOrderSupplierResourceId")
    private Integer partOrderSupplierResourceId;
    
    @Column(name = "HelpdekResourceProfileId")
    private Integer helpdekResourceProfileId;
    
    @Column(name = "HelpdekResourceProfileLabourRateTypeId")
    private Integer helpdekResourceProfileLabourRateTypeId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectQuoteId() {
		return projectQuoteId;
	}

	public void setProjectQuoteId(Integer projectQuoteId) {
		this.projectQuoteId = projectQuoteId;
	}

	public String getPartCode() {
		return partCode;
	}

	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}

	public String getPartDescription() {
		return partDescription;
	}

	public void setPartDescription(String partDescription) {
		this.partDescription = partDescription;
	}

	public String getPartCodeDetail() {
		return partCodeDetail;
	}

	public void setPartCodeDetail(String partCodeDetail) {
		this.partCodeDetail = partCodeDetail;
	}

	public Float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Float totalCost) {
		this.totalCost = totalCost;
	}

	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Boolean getDoesPartRequireSupplierOrder() {
		return doesPartRequireSupplierOrder;
	}

	public void setDoesPartRequireSupplierOrder(Boolean doesPartRequireSupplierOrder) {
		this.doesPartRequireSupplierOrder = doesPartRequireSupplierOrder;
	}

	public Boolean getPartExistsInPriceBook() {
		return partExistsInPriceBook;
	}

	public void setPartExistsInPriceBook(Boolean partExistsInPriceBook) {
		this.partExistsInPriceBook = partExistsInPriceBook;
	}

	public Integer getPartOrderSupplierResourceId() {
		return partOrderSupplierResourceId;
	}

	public void setPartOrderSupplierResourceId(Integer partOrderSupplierResourceId) {
		this.partOrderSupplierResourceId = partOrderSupplierResourceId;
	}

	public Integer getHelpdekResourceProfileId() {
		return helpdekResourceProfileId;
	}

	public void setHelpdekResourceProfileId(Integer helpdekResourceProfileId) {
		this.helpdekResourceProfileId = helpdekResourceProfileId;
	}

	public Integer getHelpdekResourceProfileLabourRateTypeId() {
		return helpdekResourceProfileLabourRateTypeId;
	}

	public void setHelpdekResourceProfileLabourRateTypeId(Integer helpdekResourceProfileLabourRateTypeId) {
		this.helpdekResourceProfileLabourRateTypeId = helpdekResourceProfileLabourRateTypeId;
	}
    
    
    
    
}
