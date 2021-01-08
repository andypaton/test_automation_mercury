package mercury.database.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PortalNotificationsQueue {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "NotificationMethodTypeId")
    Integer notificationMethodTypeId;

    @Column(name = "Subject")
    String subject;

    @Column(name = "Body")
    String body;

    @Column(name = "Recipients")
    String recipients;

    @Column(name = "CCRecipients")
    String ccRecipients;

    @Column(name = "BCCRecipients")
    String bccRecipients;

    @Column(name = "Attachments")
    String attachments;

    @Column(name = "QueueItemStatusId")
    Integer queueItemStatusId;

    @Column(name = "EpochId")
    Integer epochId;

    @Column(name = "JobReference")
    Integer jobReference;

    @Column(name = "TimelineTitle")
    String timelineTitle;

    @Column(name = "TimelineDetail")
    String timelineDetail;

    @Column(name = "TimelineNotes")
    String timelineNotes;

    @Column(name = "FailedException")
    String failedException;

    @Column(name = "FailedOn")
    Date failedOn;

    @Column(name = "DateCreated")
    Date dateCreated;

    @Column(name = "CreatedOn")
    Date createdOn;

    @Column(name = "CreatedBy")
    String createdBy;

    @Column(name = "UpdatedOn")
    Date updatedOn;

    @Column(name = "UpdatedBy")
    String updatedBy;

    @Column(name = "OrderRelatedNotification")
    boolean orderRelatedNotification;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNotificationMethodTypeId() {
        return notificationMethodTypeId;
    }

    public void setNotificationMethodTypeId(Integer notificationMethodTypeId) {
        this.notificationMethodTypeId = notificationMethodTypeId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getCcRecipients() {
        return ccRecipients;
    }

    public void setCcRecipients(String ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    public String getBccRecipients() {
        return bccRecipients;
    }

    public void setBccRecipients(String bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public Integer getQueueItemStatusId() {
        return queueItemStatusId;
    }

    public void setQueueItemStatusId(Integer queueItemStatusId) {
        this.queueItemStatusId = queueItemStatusId;
    }

    public Integer getEpochId() {
        return epochId;
    }

    public void setEpochId(Integer epochId) {
        this.epochId = epochId;
    }

    public Integer getJobReference() {
        return jobReference;
    }

    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    public String getTimelineTitle() {
        return timelineTitle;
    }

    public void setTimelineTitle(String timelineTitle) {
        this.timelineTitle = timelineTitle;
    }

    public String getTimelineDetail() {
        return timelineDetail;
    }

    public void setTimelineDetail(String timelineDetail) {
        this.timelineDetail = timelineDetail;
    }

    public String getTimelineNotes() {
        return timelineNotes;
    }

    public void setTimelineNotes(String timelineNotes) {
        this.timelineNotes = timelineNotes;
    }

    public String getFailedException() {
        return failedException;
    }

    public void setFailedException(String failedException) {
        this.failedException = failedException;
    }

    public Date getFailedOn() {
        return failedOn;
    }

    public void setFailedOn(Date failedOn) {
        this.failedOn = failedOn;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isOrderRelatedNotification() {
        return orderRelatedNotification;
    }

    public void setOrderRelatedNotification(boolean orderRelatedNotification) {
        this.orderRelatedNotification = orderRelatedNotification;
    }

}
