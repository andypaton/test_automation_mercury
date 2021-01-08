package mercury.api.models.organisationStructure.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "resourceId", "resourceProfileName", "resourceProfileId", "userProfileId", "userProfileName", "userProfileActive", "userName", "firstName", "lastName", "fullName", "email",
        "phoneNumber", "active", "isContractor", "role", "canEdit", "password" })
public class Manager {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("resourceId")
    private Integer resourceId;
    
    @JsonProperty("resourceProfileName")
    private String resourceProfileName;
    
    @JsonProperty("resourceProfileId")
    private Integer resourceProfileId;
    
    @JsonProperty("userProfileId")
    private Integer userProfileId;
    
    @JsonProperty("userProfileName")
    private String userProfileName;
    
    @JsonProperty("userProfileActive")
    private Boolean userProfileActive;
    
    @JsonProperty("userName")
    private String userName;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("active")
    private Boolean active;
    
    @JsonProperty("isContractor")
    private Boolean isContractor;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("canEdit")
    private Boolean canEdit;
    
    @JsonProperty("password")
    private String password;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("resourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @JsonProperty("resourceProfileName")
    public String getResourceProfileName() {
        return resourceProfileName;
    }

    @JsonProperty("resourceProfileName")
    public void setResourceProfileName(String resourceProfileName) {
        this.resourceProfileName = resourceProfileName;
    }

    @JsonProperty("resourceProfileId")
    public Integer getResourceProfileId() {
        return resourceProfileId;
    }

    @JsonProperty("resourceProfileId")
    public void setResourceProfileId(Integer resourceProfileId) {
        this.resourceProfileId = resourceProfileId;
    }

    @JsonProperty("userProfileId")
    public Integer getUserProfileId() {
        return userProfileId;
    }

    @JsonProperty("userProfileId")
    public void setUserProfileId(Integer userProfileId) {
        this.userProfileId = userProfileId;
    }

    @JsonProperty("userProfileName")
    public String getUserProfileName() {
        return userProfileName;
    }

    @JsonProperty("userProfileName")
    public void setUserProfileName(String userProfileName) {
        this.userProfileName = userProfileName;
    }

    @JsonProperty("userProfileActive")
    public Boolean getUserProfileActive() {
        return userProfileActive;
    }

    @JsonProperty("userProfileActive")
    public void setUserProfileActive(Boolean userProfileActive) {
        this.userProfileActive = userProfileActive;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("fullName")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("isContractor")
    public Boolean getIsContractor() {
        return isContractor;
    }

    @JsonProperty("isContractor")
    public void setIsContractor(Boolean isContractor) {
        this.isContractor = isContractor;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("canEdit")
    public Boolean getCanEdit() {
        return canEdit;
    }

    @JsonProperty("canEdit")
    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("resourceId", resourceId).append("resourceProfileName", resourceProfileName).append("resourceProfileId", resourceProfileId)
                .append("userProfileId", userProfileId).append("userProfileName", userProfileName).append("userProfileActive", userProfileActive).append("userName", userName)
                .append("firstName", firstName).append("lastName", lastName).append("fullName", fullName).append("email", email).append("phoneNumber", phoneNumber).append("active", active)
                .append("isContractor", isContractor).append("role", role).append("canEdit", canEdit).append("password", password).toString();
    }

}