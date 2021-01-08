package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ApplicationUser")
public class ApplicationUser {

	@Id
	@Column(name = "Id") 
    private String id;

    @Column(name = "Email") 
    private String email;

    @Column(name = "EmailConfirmed") 
    private String emailConfirmed;

    @Column(name = "PasswordHash") 
    private String passwordHash;

    @Column(name = "SecurityStamp") 
    private String securityStamp;

    @Column(name = "PhoneNumber") 
    private String phoneNumber;

    @Column(name = "PhoneNumberConfirmed") 
    private String phoneNumberConfirmed;

    @Column(name = "TwoFactorEnabled") 
    private String twoFactorEnabled;

    @Column(name = "LockoutEndDateUtc") 
    private java.sql.Timestamp lockoutEndDateUtc;

    @Column(name = "LockoutEnabled") 
    private String lockoutEnabled;

    @Column(name = "AccessFailedCount") 
    private Integer accessFailedCount;

    @Column(name = "UserName") 
    private String userName;

    @Column(name = "FirstName") 
    private String firstName;

    @Column(name = "LastName") 
    private String lastName;

    @Column(name = "LookupId") 
    private Integer lookupId;

    @Column(name = "RefreshToken") 
    private String refreshToken;

    @Column(name = "RefreshTokenIssueDate") 
    private java.sql.Timestamp refreshTokenIssueDate;

    @Column(name = "RefreshTokenExpiryDate") 
    private java.sql.Timestamp refreshTokenExpiryDate;

    @Column(name = "PasswordExpiryDate") 
    private java.sql.Timestamp passwordExpiryDate;

    @Column(name = "UserProfileId") 
    private Integer userProfileId;

    @Column(name = "ResourceId") 
    private Integer resourceId;

    @Column(name = "CompanyId") 
    private Integer companyId;

    @Column(name = "SiteId") 
    private Integer siteId;

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



    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }


    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }


    public String getEmailConfirmed() {
      return emailConfirmed;
    }

    public void setEmailConfirmed(String emailConfirmed) {
      this.emailConfirmed = emailConfirmed;
    }


    public String getPasswordHash() {
      return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
      this.passwordHash = passwordHash;
    }


    public String getSecurityStamp() {
      return securityStamp;
    }

    public void setSecurityStamp(String securityStamp) {
      this.securityStamp = securityStamp;
    }


    public String getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }


    public String getPhoneNumberConfirmed() {
      return phoneNumberConfirmed;
    }

    public void setPhoneNumberConfirmed(String phoneNumberConfirmed) {
      this.phoneNumberConfirmed = phoneNumberConfirmed;
    }


    public String getTwoFactorEnabled() {
      return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(String twoFactorEnabled) {
      this.twoFactorEnabled = twoFactorEnabled;
    }


    public java.sql.Timestamp getLockoutEndDateUtc() {
      return lockoutEndDateUtc;
    }

    public void setLockoutEndDateUtc(java.sql.Timestamp lockoutEndDateUtc) {
      this.lockoutEndDateUtc = lockoutEndDateUtc;
    }


    public String getLockoutEnabled() {
      return lockoutEnabled;
    }

    public void setLockoutEnabled(String lockoutEnabled) {
      this.lockoutEnabled = lockoutEnabled;
    }


    public Integer getAccessFailedCount() {
      return accessFailedCount;
    }

    public void setAccessFailedCount(Integer accessFailedCount) {
      this.accessFailedCount = accessFailedCount;
    }


    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }


    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }


    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }


    public Integer getLookupId() {
      return lookupId;
    }

    public void setLookupId(Integer lookupId) {
      this.lookupId = lookupId;
    }


    public String getRefreshToken() {
      return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
    }


    public java.sql.Timestamp getRefreshTokenIssueDate() {
      return refreshTokenIssueDate;
    }

    public void setRefreshTokenIssueDate(java.sql.Timestamp refreshTokenIssueDate) {
      this.refreshTokenIssueDate = refreshTokenIssueDate;
    }


    public java.sql.Timestamp getRefreshTokenExpiryDate() {
      return refreshTokenExpiryDate;
    }

    public void setRefreshTokenExpiryDate(java.sql.Timestamp refreshTokenExpiryDate) {
      this.refreshTokenExpiryDate = refreshTokenExpiryDate;
    }


    public java.sql.Timestamp getPasswordExpiryDate() {
      return passwordExpiryDate;
    }

    public void setPasswordExpiryDate(java.sql.Timestamp passwordExpiryDate) {
      this.passwordExpiryDate = passwordExpiryDate;
    }


    public Integer getUserProfileId() {
      return userProfileId;
    }

    public void setUserProfileId(Integer userProfileId) {
      this.userProfileId = userProfileId;
    }


    public Integer getResourceId() {
      return resourceId;
    }

    public void setResourceId(Integer resourceId) {
      this.resourceId = resourceId;
    }


    public Integer getCompanyId() {
      return companyId;
    }

    public void setCompanyId(Integer companyId) {
      this.companyId = companyId;
    }


    public Integer getSiteId() {
      return siteId;
    }

    public void setSiteId(Integer siteId) {
      this.siteId = siteId;
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

