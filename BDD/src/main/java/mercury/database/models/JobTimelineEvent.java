package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="JobTimelineEvent")
public class JobTimelineEvent {

	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "JobId") 
    private Integer jobId;

    @Column(name = "JobEventTypeId") 
    private Integer jobEventTypeId;

    @Column(name = "Title") 
    private String title;

    @Column(name = "Detail1") 
    private String detail1;

    @Column(name = "Detail2") 
    private String detail2;

    @Column(name = "Reason") 
    private String reason;

    @Column(name = "Notes") 
    private String notes;

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


    public Integer getJobId() {
      return jobId;
    }

    public void setJobId(Integer jobId) {
      this.jobId = jobId;
    }


    public Integer getJobEventTypeId() {
      return jobEventTypeId;
    }

    public void setJobEventTypeId(Integer jobEventTypeId) {
      this.jobEventTypeId = jobEventTypeId;
    }


    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }


    public String getDetail1() {
      return detail1;
    }

    public void setDetail1(String detail1) {
      this.detail1 = detail1;
    }


    public String getDetail2() {
      return detail2;
    }

    public void setDetail2(String detail2) {
      this.detail2 = detail2;
    }


    public String getReason() {
      return reason;
    }

    public void setReason(String reason) {
      this.reason = reason;
    }


    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
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
