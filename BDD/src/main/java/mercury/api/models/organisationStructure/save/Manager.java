package mercury.api.models.organisationStructure.save;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "firstName", "lastName", "role", "resourceId", "toBeRemoved", "isActive" })
public class Manager {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("resourceId")
    private Integer resourceId;
    
    @JsonProperty("toBeRemoved")
    private Boolean toBeRemoved;
    
    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
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

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("resourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    @JsonProperty("toBeRemoved")
    public Boolean getToBeRemoved() {
        return toBeRemoved;
    }

    @JsonProperty("toBeRemoved")
    public void setToBeRemoved(Boolean toBeRemoved) {
        this.toBeRemoved = toBeRemoved;
    }

    @JsonProperty("isActive")
    public Boolean getIsActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("firstName", firstName).append("lastName", lastName).append("role", role).append("resourceId", resourceId)
                .append("toBeRemoved", toBeRemoved).append("isActive", isActive).toString();
    }

}