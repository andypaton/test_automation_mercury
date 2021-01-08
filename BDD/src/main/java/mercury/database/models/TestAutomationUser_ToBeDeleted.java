package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TestAutomation_Users")
public class TestAutomationUser_ToBeDeleted {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "Type")
    private String type;

    @Column(name = "LoggedOn")
    private Boolean loggedOn;

    @Column(name = "UpdatedOn")
    private java.sql.Timestamp updatedOn;

    @Column(name = "Active")
    private Boolean active;

    @Column(name = "DomainUser")
    private String domainUser;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getLoggedOn() {
        return loggedOn;
    }

    public void setLoggedOn(Boolean loggedOn) {
        this.loggedOn = loggedOn;
    }

    public java.sql.Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(java.sql.Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDomainUser() {
        return domainUser;
    }

    public void setDomainUser(String domainUser) {
        this.domainUser = domainUser;
    }

}
