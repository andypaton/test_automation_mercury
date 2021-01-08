package mercury.api.models.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;
import org.apache.commons.lang.builder.ToStringBuilder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "resourceAvailabilityTypeId",
    "coveringResourceId",
    "resourceId",
    "startAt",
    "endAt",
    "active",
    "notes",
    "addedViaHelpdesk"
})
public class Absence extends modelBase<Absence>{

    @JsonProperty("resourceAvailabilityTypeId")
    private Integer resourceAvailabilityTypeId;

    @JsonProperty("coveringResourceId")
    private Integer coveringResourceId;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("startAt")
    private String startAt;

    @JsonProperty("endAt")
    private String endAt;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("addedViaHelpdesk")
    private Boolean addedViaHelpdesk;

    @JsonProperty("resourceAvailabilityTypeId")
    public Integer getResourceAvailabilityTypeId() {
        return resourceAvailabilityTypeId;
    }

    @JsonProperty("resourceAvailabilityTypeId")
    public void setResourceAvailabilityTypeId(Integer resourceAvailabilityTypeId) {
        this.resourceAvailabilityTypeId = resourceAvailabilityTypeId;
    }

    @JsonProperty("coveringResourceId")
    public Integer getCoveringResourceId() {
        return coveringResourceId;
    }

    @JsonProperty("coveringResourceId")
    public void setCoveringResourceId(Integer coveringResourceId) {
        this.coveringResourceId = coveringResourceId;
    }

    @JsonProperty("resourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
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

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("addedViaHelpdesk")
    public Boolean getAddedViaHelpdesk() {
        return addedViaHelpdesk;
    }

    @JsonProperty("addedViaHelpdesk")
    public void setAddedViaHelpdesk(Boolean addedViaHelpdesk) {
        this.addedViaHelpdesk = addedViaHelpdesk;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("resourceAvailabilityTypeId", resourceAvailabilityTypeId)
                .append("coveringResourceId", coveringResourceId).append("resourceId", resourceId)
                .append("startAt", startAt).append("endAt", endAt)
                .append("active", active).append("notes", notes)
                .append("addedViaHelpdesk", addedViaHelpdesk)
                .toString();
    }

}
