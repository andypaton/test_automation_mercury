package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PartCode")
public class PartCode {

	@Id
	@Column(name = "Id")
	private Integer id;
	
	@Column(name = "PartCode")
	private String partCode;
	
	@Column(name = "Description")
	private String description;

	@Column(name ="ManufacturerRef")
	private String manufacturerRef;
	
	@Column(name = "UnitPrice")
	private Float unitPrice;
	
	@Column(name = "SupplierCode")
	private String supplierCode;
	
	@Column(name = "SupplierName")
	private String supplierName;
	
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getPartCode() {
		return this.partCode;
	}
	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getManufacturerRef() {
		return this.description;
	}
	public void setManufacturerRef(String manufacturerRef) {
		this.manufacturerRef = manufacturerRef;
	}
	
	public Float getUnitPrice() {
		return this.unitPrice;
	}
	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public String getSupplierCode() {
		return this.supplierCode;
	}
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}
	
	public String getSupplierName() {
	    return this.supplierName;
	}
	public void setSupplierName(String supplierName) {
	    this.supplierName = supplierName;
	}
	
}
