package mercury.database.models;

import javax.persistence.Column;

public class TestAutomationLock {


    @Column(name = "Reference")
    private Integer reference;

    @Column(name = "Type")
    private String type;

    @Column(name = "CreatedOn")
    private java.sql.Timestamp createdOn;

    @Column(name = "Reason")
    private String reason;

    @Column(name = "Detail")
    private String detail;

    public Integer getReference() {
        return reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.sql.Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(java.sql.Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
