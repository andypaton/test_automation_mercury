package mercury.api.models.portal.quoteJobEdit;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CancellationReason",
    "HasSelectedAlternativeResource",
    "ResourceType",
    "NewResourceAssignment",
    "AlternativeFundingRoute",
    "AdditionalCancellationComments",
    "JobReference",
    "EpochSiteId",
    "__RequestVerificationToken"
})
public class QuoteReject extends modelBase<QuoteReject>{

    @JsonProperty("CancellationReason")
    private Integer cancellationReason;

    @JsonProperty("HasSelectedAlternativeResource")
    private Boolean hasSelectedAlternativeResource;

    @JsonProperty("ResourceType")
    private Integer resourceType;

    @JsonProperty("NewResourceAssignment")
    private Integer newResourceAssignment;

    @JsonProperty("AlternativeFundingRoute")
    private Integer alternativeFundingRoute;

    @JsonProperty("AdditionalCancellationComments")
    private String additionalCancellationComments;

    @JsonProperty("JobReference")
    private Integer jobReference;

    @JsonProperty("EpochSiteId")
    private Integer epochSiteId;

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    public Integer getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(Integer cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Boolean getHasSelectedAlternativeResource() {
        return hasSelectedAlternativeResource;
    }

    public void setHasSelectedAlternativeResource(Boolean hasSelectedAlternativeResource) {
        this.hasSelectedAlternativeResource = hasSelectedAlternativeResource;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Integer getNewResourceAssignment() {
        return newResourceAssignment;
    }

    public void setNewResourceAssignment(Integer newResourceAssignment) {
        this.newResourceAssignment = newResourceAssignment;
    }

    public Integer getAlternativeFundingRoute() {
        return alternativeFundingRoute;
    }

    public void setAlternativeFundingRoute(Integer alternativeFundingRoute) {
        this.alternativeFundingRoute = alternativeFundingRoute;
    }

    public String getAdditionalCancellationComments() {
        return additionalCancellationComments;
    }

    public void setAdditionalCancellationComments(String additionalCancellationComments) {
        this.additionalCancellationComments = additionalCancellationComments;
    }

    public Integer getJobReference() {
        return jobReference;
    }

    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    public Integer getEpochSiteId() {
        return epochSiteId;
    }

    public void setEpochSiteId(Integer epochSiteId) {
        this.epochSiteId = epochSiteId;
    }

    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("cancellationReason", cancellationReason)
                .append("hasSelectedAlternativeResource", hasSelectedAlternativeResource)
                .append("resourceType", resourceType)
                .append("newResourceAssignment", newResourceAssignment)
                .append("alternativeFundingRoute", alternativeFundingRoute)
                .append("additionalCancellationComments", additionalCancellationComments)
                .append("jobReference", jobReference)
                .append("epochSiteId", epochSiteId)
                .toString();
    }
}
