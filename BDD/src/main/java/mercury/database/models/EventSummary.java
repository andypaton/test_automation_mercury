package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EventSummary")
public class EventSummary {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "JobReference")
    private Integer jobReference;

    @Column(name = "JobId")
    private Integer jobId;

    @Column(name = "JobEventId")
    private Integer jobEventId;

    @Column(name = "JobEventTypeId")
    private Integer jobEventTypeId;

    @Column(name = "ResourceAssignmentEventTypeId")
    private Integer resourceAssignmentEventTypeId;

    @Column(name = "ResourceAssignmentId")
    private Integer resourceAssignmentId;

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

    @Column(name = "LoggedAt")
    private java.sql.Timestamp loggedAt;

    @Column(name = "UpdatedAt")
    private java.sql.Timestamp updatedAt;

    @Column(name = "ResourceName")
    private String resourceName;

    @Column(name = "LoggedBy")
    private String loggedBy;

    @Column(name = "IconIdentifier")
    private String iconIdentifier;

    public Integer getJobReference() {
        return jobReference;
    }

    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getJobEventId() {
        return jobEventId;
    }

    public void setJobEventId(Integer jobEventId) {
        this.jobEventId = jobEventId;
    }

    public Integer getJobEventTypeId() {
        return jobEventTypeId;
    }

    public void setJobEventTypeId(Integer jobEventTypeId) {
        this.jobEventTypeId = jobEventTypeId;
    }

    public Integer getResourceAssignmentEventTypeId() {
        return resourceAssignmentEventTypeId;
    }

    public void setResourceAssignmentEventTypeId(Integer resourceAssignmentEventTypeId) {
        this.resourceAssignmentEventTypeId = resourceAssignmentEventTypeId;
    }

    public Integer getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    public void setResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
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

    public java.sql.Timestamp getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(java.sql.Timestamp loggedAt) {
        this.loggedAt = loggedAt;
    }

    public java.sql.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.sql.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(String loggedBy) {
        this.loggedBy = loggedBy;
    }

    public String getIconIdentifier() {
        return iconIdentifier;
    }

    public void setIconIdentifier(String iconIdentifier) {
        this.iconIdentifier = iconIdentifier;
    }

}
