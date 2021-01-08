package mercury.api.models.resource;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "startDayOfTheWeek", "endDayOfTheWeek", "startAt", "endAt", "valid" })
public class WorkingHours {

	@JsonProperty("startDayOfTheWeek")
	private Integer startDayOfTheWeek;
	
	@JsonProperty("endDayOfTheWeek")
	private Integer endDayOfTheWeek;
	
	@JsonProperty("startAt")
	private String startAt;
	
	@JsonProperty("endAt")
	private String endAt;
	
    @JsonProperty("valid")
    private Boolean valid = true;
    
	@JsonProperty("startDayOfTheWeek")
	public Integer getStartDayOfTheWeek() {
		return startDayOfTheWeek;
	}

	@JsonProperty("startDayOfTheWeek")
	public void setStartDayOfTheWeek(Integer startDayOfTheWeek) {
		this.startDayOfTheWeek = startDayOfTheWeek;
	}

	@JsonProperty("endDayOfTheWeek")
	public Integer getEndDayOfTheWeek() {
		return endDayOfTheWeek;
	}

	@JsonProperty("endDayOfTheWeek")
	public void setEndDayOfTheWeek(Integer endDayOfTheWeek) {
		this.endDayOfTheWeek = endDayOfTheWeek;
	}

	@JsonProperty("startAt")
	public String getStartAt() {
		return startAt;
	}

	@JsonProperty("startAt")
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	@JsonProperty("endAt")
	public String getEndAt() {
		return endAt;
	}

	@JsonProperty("endAt")
	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

    @JsonProperty("valid")
    public Boolean getValid() {
        return valid;
    }

    @JsonProperty("valid")
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("startDayOfTheWeek", startDayOfTheWeek)
				.append("endDayOfTheWeek", endDayOfTheWeek).append("startAt", startAt).append("endAt", endAt).append("valid", valid)
				.toString();
	}

}