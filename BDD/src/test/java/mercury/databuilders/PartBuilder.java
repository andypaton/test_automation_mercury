package mercury.databuilders;

/**
 * @author forbesp
 * Used to generate test data when making a part request via Portal Part Request ( After completing a job awaiting parts)
 */
public class PartBuilder {

    private String partNumber;
    private String partDescription;
    private String manufacturerRef;
    private String model;
    private String serialNumber;
    private Double unitPrice;
    private Boolean newPart;
    private String supplierCode;

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getManufacturerRef() {
        return manufacturerRef;
    }

    public void setManufacturerRef(String manufacturerRef) {
        this.manufacturerRef = manufacturerRef;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setNewPart(Boolean newPart) {
        this.newPart = newPart;
    }

    public Boolean getNewPart() {
        return newPart;
    }

    public String getSupplierCode() {
        return supplierCode;
    }
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }



    public static class Builder {
        private String partNumber;
        private String partDescription;
        private String manufacturerRef;
        private String model;
        private String serialNumber;
        private Double unitPrice;

        public Builder() {
            this.partNumber = generatePartNumbmer();
            this.partDescription = "Part Description ";
            this.manufacturerRef = generateManufacturerRef();
            this.model = generateModel();
            this.serialNumber = generateSerialNuber();
            this.unitPrice = generateUnitPrice();
        }


        public PartBuilder build() {
            return new PartBuilder(this);
        }

        public Builder partNumber(String val){
            partNumber = val;
            return this;
        }
        public Builder partDescription(String val){
            partDescription = val;
            return this;
        }
        public Builder model(String val){
            model = val;
            return this;
        }
        public Builder serialNumber(String val){
            serialNumber = val;
            return this;
        }
        public Builder unitPrice(Double val){
            unitPrice = val;
            return this;
        }

        private String generatePartNumbmer() {
            return "ACME-PN-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
        }

        private String generateManufacturerRef() {
            return "ACME-MR-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
        }

        private String generateModel() {
            return "ACME-M-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
        }
        private String generateSerialNuber() {
            return "ACME-SN-" + DataGenerator.GenerateRandomString(13, 13, 0, 3, 10, 0);
        }
        private Double generateUnitPrice() {
            return DataGenerator.GenerateRandomDouble(0.01, 250.00);
        }
    }

    private PartBuilder(Builder builder){
        partNumber = builder.partNumber;
        partDescription = builder.partDescription;
        manufacturerRef = builder.manufacturerRef;
        model = builder.model;
        serialNumber = builder.serialNumber;
        unitPrice = builder.unitPrice;
    }
    public PartBuilder() {
    }
}
