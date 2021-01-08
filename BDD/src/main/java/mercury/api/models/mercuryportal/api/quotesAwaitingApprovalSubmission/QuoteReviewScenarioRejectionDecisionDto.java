package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "quoteApprovalScenarioId",
    "resourceId",
    "portalResourceId",
    "approved",
    "rejectedReasonId",
    "rejectedNotes",
    "isHighRisk"
})
public class QuoteReviewScenarioRejectionDecisionDto extends modelBase<QuoteReviewScenarioRejectionDecisionDto> {

    @JsonProperty("quoteApprovalScenarioId")
    private Integer quoteApprovalScenarioId;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("portalResourceId")
    private Integer portalResourceId;

    @JsonProperty("approved")
    private Boolean approved;

    @JsonProperty("rejectedReasonId")
    private Integer rejectedReasonId;

    @JsonProperty("rejectedNotes")
    private String rejectedNotes;

    @JsonProperty("isHighRisk")
    private Boolean isHighRisk;

    @JsonProperty("quoteApprovalScenarioId")
    public Integer getQuoteApprovalScenarioId() {
        return quoteApprovalScenarioId;
    }

    @JsonProperty("quoteApprovalScenarioId")
    public void setQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
    }

    public QuoteReviewScenarioRejectionDecisionDto withQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
        return this;
    }

    @JsonProperty("resourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public QuoteReviewScenarioRejectionDecisionDto withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @JsonProperty("portalResourceId")
    public Integer getPortalResourceId() {
        return portalResourceId;
    }

    @JsonProperty("portalResourceId")
    public void setPortalResourceId(Integer portalResourceId) {
        this.portalResourceId = portalResourceId;
    }

    public QuoteReviewScenarioRejectionDecisionDto withPortalResourceId(Integer portalResourceId) {
        this.portalResourceId = portalResourceId;
        return this;
    }

    @JsonProperty("approved")
    public Boolean getApproved() {
        return approved;
    }

    @JsonProperty("approved")
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public QuoteReviewScenarioRejectionDecisionDto withApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    @JsonProperty("rejectedReasonId")
    public Integer getRejectedReasonId() {
        return rejectedReasonId;
    }

    @JsonProperty("rejectedReasonId")
    public void setRejectedReasonId(Integer rejectedReasonId) {
        this.rejectedReasonId = rejectedReasonId;
    }

    public QuoteReviewScenarioRejectionDecisionDto withrejectedNotes(Integer rejectedReasonId) {
        this.rejectedReasonId = rejectedReasonId;
        return this;
    }

    @JsonProperty("rejectedNotes")
    public String getRejectedNotes() {
        return rejectedNotes;
    }

    @JsonProperty("rejectedNotes")
    public void setRejectedNotes(String rejectedNotes) {
        this.rejectedNotes = rejectedNotes;
    }

    public QuoteReviewScenarioRejectionDecisionDto withrejectedNotes(String rejectedNotes) {
        this.rejectedNotes = rejectedNotes;
        return this;
    }

    @JsonProperty("isHighRisk")
    public Boolean getIsHighRisk() {
        return isHighRisk;
    }

    @JsonProperty("isHighRisk")
    public void setIsHighRisk(Boolean isHighRisk) {
        this.isHighRisk = isHighRisk;
    }

    public QuoteReviewScenarioRejectionDecisionDto withIsHighRisk(Boolean isHighRisk) {
        this.isHighRisk = isHighRisk;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("quoteApprovalScenarioId", quoteApprovalScenarioId).append("resourceId", resourceId).append("portalResourceId", portalResourceId).append("approved", approved).append("rejectedReasonId", rejectedReasonId).append("rejectedNotes", rejectedNotes).append("isHighRisk", isHighRisk).toString();
    }

}
