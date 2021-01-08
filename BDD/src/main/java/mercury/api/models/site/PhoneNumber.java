package mercury.api.models.site;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "phoneNumber", "priority", "notes", "phoneNumberTypeId" })
public class PhoneNumber {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("priority")
    private Integer priority;
    
    @JsonProperty("notes")
    private String notes;
    
    @JsonProperty("phoneNumberTypeId")
    private Integer phoneNumberTypeId;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("phoneNumberTypeId")
    public Integer getPhoneNumberTypeId() {
        return phoneNumberTypeId;
    }

    @JsonProperty("phoneNumberTypeId")
    public void setPhoneNumberTypeId(Integer phoneNumberTypeId) {
        this.phoneNumberTypeId = phoneNumberTypeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("phoneNumber", phoneNumber).append("priority", priority).append("notes", notes).append("phoneNumberTypeId", phoneNumberTypeId)
                .toString();
    }

}
