package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Reason")
public class Reason {

	@Id
	@Column(name = "Id") 
    private Integer id;

    @Column(name = "Code") 
    private String code;

    @Column(name = "Name") 
    private String name;

    @Column(name = "ReasonCategory") 
    private String reasonCategory;

    @Column(name = "CreatedOn") 
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy") 
    private String createdBy;

    @Column(name = "UpdatedOn") 
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy") 
    private String updatedBy;

    @Column(name = "Active") 
    private String active;



    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }


    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }


    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }


    public String getReasonCategory() {
      return reasonCategory;
    }

    public void setReasonCategory(String reasonCategory) {
      this.reasonCategory = reasonCategory;
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


    public String getActive() {
      return active;
    }

    public void setActive(String active) {
      this.active = active;
    }

}
