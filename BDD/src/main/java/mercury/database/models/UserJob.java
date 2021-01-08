package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserJob {

    @Id
    @Column(name = "ResourceId")
    Integer resourceId;

    @Column(name = "JobReference")
    Integer jobReference;

    @Column(name = "UserName")
    String userName;

    @Column(name = "SiteId")
    Integer siteId;

    public Integer getResourceId() {
        return resourceId;
    }
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getJobReference() {
        return jobReference;
    }
    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getSiteId() {
        return siteId;
    }
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
