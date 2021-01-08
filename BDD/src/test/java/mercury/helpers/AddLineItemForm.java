package mercury.helpers;

public final class AddLineItemForm {

	private String type;
	private String description;
	private String partCode;
	private Integer quantity;
	private Float unitPrice;
	private Float minUnitPrice;
	private Float maxUnitPrice;
	private Float lineValue;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPartCode() {
		return partCode;
	}
	public void setPartCode(String partCode) {
		this.partCode = partCode;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Float getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Float getMinUnitPrice() {
		return minUnitPrice;
	}
	public void setMinUnitPrice(Float minUnitPrice) {
		this.minUnitPrice = minUnitPrice;
	}
	public Float getMaxUnitPrice() {
		return maxUnitPrice;
	}
	public void setMaxUnitPrice(Float maxUnitPrice) {
		this.maxUnitPrice = maxUnitPrice;
	}
	public Float getLineValue() {
		return lineValue;
	}
	public void setLineValue(Float lineValue) {
		this.lineValue = lineValue;
	}
	
}
