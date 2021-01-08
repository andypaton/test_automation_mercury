package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "quoteApprovalScenarioId",
    "resourceId",
    "portalResourceId",
    "approved",
    "recommendedNotes",
    "mercuryBudgetId",
    "isHighRisk"
})
public class QuoteReviewScenarioDecisionDto extends modelBase<QuoteReviewScenarioDecisionDto> {

    @JsonProperty("quoteApprovalScenarioId")
    private Integer quoteApprovalScenarioId;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("portalResourceId")
    private Integer portalResourceId;

    @JsonProperty("approved")
    private Boolean approved;

    @JsonProperty("recommendedNotes")
    private String recommendedNotes;

    @JsonProperty("mercuryBudgetId")
    private Integer mercuryBudgetId;

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

    public QuoteReviewScenarioDecisionDto withQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
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

    public QuoteReviewScenarioDecisionDto withResourceId(Integer resourceId) {
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

    public QuoteReviewScenarioDecisionDto withPortalResourceId(Integer portalResourceId) {
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

    public QuoteReviewScenarioDecisionDto withApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    @JsonProperty("recommendedNotes")
    public String getRecommendedNotes() {
        return recommendedNotes;
    }

    @JsonProperty("recommendedNotes")
    public void setRecommendedNotes(String recommendedNotes) {
        this.recommendedNotes = recommendedNotes;
    }

    public QuoteReviewScenarioDecisionDto withRecommendedNotes(String recommendedNotes) {
        this.recommendedNotes = recommendedNotes;
        return this;
    }

    @JsonProperty("mercuryBudgetId")
    public Integer getMercuryBudgetId() {
        return mercuryBudgetId;
    }

    @JsonProperty("mercuryBudgetId")
    public void setMercuryBudgetId(Integer mercuryBudgetId) {
        this.mercuryBudgetId = mercuryBudgetId;
    }

    public QuoteReviewScenarioDecisionDto withMercuryBudgetId(Integer mercuryBudgetId) {
        this.mercuryBudgetId = mercuryBudgetId;
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

    public QuoteReviewScenarioDecisionDto withIsHighRisk(Boolean isHighRisk) {
        this.isHighRisk = isHighRisk;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("quoteApprovalScenarioId", quoteApprovalScenarioId).append("resourceId", resourceId).append("portalResourceId", portalResourceId).append("approved", approved).append("recommendedNotes", recommendedNotes).append("mercuryBudgetId", mercuryBudgetId).append("isHighRisk", isHighRisk).toString();
    }

}
