package mercury.api.models.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "phoneNumberTypeId", "priority", "phoneNumber", "notes" })
public class PhoneNumber {

	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("phoneNumberTypeId")
	private Integer phoneNumberTypeId;
	
	@JsonProperty("priority")
	private Integer priority;
	
	@JsonProperty("phoneNumber")
	private String phoneNumber;
	
	@JsonProperty("notes")
	private String notes;
	

	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Integer id) {
		this.id = id;
	}

	@JsonProperty("phoneNumberTypeId")
	public Integer getPhoneNumberTypeId() {
		return phoneNumberTypeId;
	}

	@JsonProperty("phoneNumberTypeId")
	public void setPhoneNumberTypeId(Integer phoneNumberTypeId) {
		this.phoneNumberTypeId = phoneNumberTypeId;
	}

	@JsonProperty("priority")
	public Integer getPriority() {
		return priority;
	}

	@JsonProperty("priority")
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@JsonProperty("phoneNumber")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	@JsonProperty("phoneNumber")
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@JsonProperty("notes")
	public String getNotes() {
		return notes;
	}

	@JsonProperty("notes")
	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.append("phoneNumberTypeId", phoneNumberTypeId)
				.append("priority", priority)
				.append("phoneNumber", phoneNumber)
				.append("notes", notes)
				.toString();
	}

}