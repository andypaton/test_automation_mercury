package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalSubmission;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "quoteReviewScenarioDecisionDtos",
    "quoteResourceIds",
    "jobRef",
    "currentFundingRouteId",
    "jobIsApproved",
    "overallQuoteValue",
    "requiresSeniorManagerApproval",
    "internalNotes",
    "openQuoteRequest",
    "seniorManagerSubmitsApprovalDecision"
})
public class QuoteDecision extends modelBase<QuoteDecision> {

    @JsonProperty("quoteReviewScenarioDecisionDtos")
    private List<QuoteReviewScenarioDecisionDto> quoteReviewScenarioDecisionDtos = null;

    @JsonProperty("quoteResourceIds")
    private List<Object> quoteResourceIds = null;

    @JsonProperty("jobRef")
    private Integer jobRef;

    @JsonProperty("currentFundingRouteId")
    private Integer currentFundingRouteId;

    @JsonProperty("jobIsApproved")
    private Boolean jobIsApproved;

    @JsonProperty("overallQuoteValue")
    private int overallQuoteValue;

    @JsonProperty("requiresSeniorManagerApproval")
    private Boolean requiresSeniorManagerApproval;

    @JsonProperty("internalNotes")
    private String internalNotes;

    @JsonProperty("openQuoteRequest")
    private Boolean openQuoteRequest;

    @JsonProperty("seniorManagerSubmitsApprovalDecision")
    private Boolean seniorManagerSubmitsApprovalDecision;

    @JsonProperty("quoteReviewScenarioDecisionDtos")
    public List<QuoteReviewScenarioDecisionDto> getQuoteReviewScenarioDecisionDtos() {
        return quoteReviewScenarioDecisionDtos;
    }

    @JsonProperty("quoteReviewScenarioDecisionDtos")
    public void setQuoteReviewScenarioDecisionDtos(List<QuoteReviewScenarioDecisionDto> quoteReviewScenarioDecisionDtos) {
        this.quoteReviewScenarioDecisionDtos = quoteReviewScenarioDecisionDtos;
    }

    public QuoteDecision withQuoteReviewScenarioDecisionDtos(List<QuoteReviewScenarioDecisionDto> quoteReviewScenarioDecisionDtos) {
        this.quoteReviewScenarioDecisionDtos = quoteReviewScenarioDecisionDtos;
        return this;
    }

    @JsonProperty("quoteResourceIds")
    public List<Object> getQuoteResourceIds() {
        return quoteResourceIds;
    }

    @JsonProperty("quoteResourceIds")
    public void setQuoteResourceIds(List<Object> quoteResourceIds) {
        this.quoteResourceIds = quoteResourceIds;
    }

    public QuoteDecision withQuoteResourceIds(List<Object> quoteResourceIds) {
        this.quoteResourceIds = quoteResourceIds;
        return this;
    }

    @JsonProperty("jobRef")
    public Integer getJobRef() {
        return jobRef;
    }

    @JsonProperty("jobRef")
    public void setJobRef(Integer jobRef) {
        this.jobRef = jobRef;
    }

    public QuoteDecision withJobRef(Integer jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("currentFundingRouteId")
    public Integer getCurrentFundingRouteId() {
        return currentFundingRouteId;
    }

    @JsonProperty("currentFundingRouteId")
    public void setCurrentFundingRouteId(Integer currentFundingRouteId) {
        this.currentFundingRouteId = currentFundingRouteId;
    }

    public QuoteDecision withCurrentFundingRouteId(Integer currentFundingRouteId) {
        this.currentFundingRouteId = currentFundingRouteId;
        return this;
    }

    @JsonProperty("jobIsApproved")
    public Boolean getJobIsApproved() {
        return jobIsApproved;
    }

    @JsonProperty("jobIsApproved")
    public void setJobIsApproved(Boolean jobIsApproved) {
        this.jobIsApproved = jobIsApproved;
    }

    public QuoteDecision withJobIsApproved(Boolean jobIsApproved) {
        this.jobIsApproved = jobIsApproved;
        return this;
    }

    @JsonProperty("overallQuoteValue")
    public int getOverallQuoteValue() {
        return overallQuoteValue;
    }

    @JsonProperty("overallQuoteValue")
    public void setOverallQuoteValue(int overallQuoteValue) {
        this.overallQuoteValue = overallQuoteValue;
    }

    public QuoteDecision withOverallQuoteValue(int overallQuoteValue) {
        this.overallQuoteValue = overallQuoteValue;
        return this;
    }

    @JsonProperty("requiresSeniorManagerApproval")
    public Boolean getRequiresSeniorManagerApproval() {
        return requiresSeniorManagerApproval;
    }

    @JsonProperty("requiresSeniorManagerApproval")
    public void setRequiresSeniorManagerApproval(Boolean requiresSeniorManagerApproval) {
        this.requiresSeniorManagerApproval = requiresSeniorManagerApproval;
    }

    public QuoteDecision withRequiresSeniorManagerApproval(Boolean requiresSeniorManagerApproval) {
        this.requiresSeniorManagerApproval = requiresSeniorManagerApproval;
        return this;
    }

    @JsonProperty("internalNotes")
    public String getInternalNotes() {
        return internalNotes;
    }

    @JsonProperty("internalNotes")
    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public QuoteDecision withInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
        return this;
    }

    @JsonProperty("openQuoteRequest")
    public Boolean getOpenQuoteRequest() {
        return openQuoteRequest;
    }

    @JsonProperty("openQuoteRequest")
    public void setOpenQuoteRequest(Boolean openQuoteRequest) {
        this.openQuoteRequest = openQuoteRequest;
    }

    public QuoteDecision withOpenQuoteRequest(Boolean openQuoteRequest) {
        this.openQuoteRequest = openQuoteRequest;
        return this;
    }

    @JsonProperty("seniorManagerSubmitsApprovalDecision")
    public Boolean getSeniorManagerSubmitsApprovalDecision() {
        return seniorManagerSubmitsApprovalDecision;
    }

    @JsonProperty("seniorManagerSubmitsApprovalDecision")
    public void setSeniorManagerSubmitsApprovalDecision(Boolean seniorManagerSubmitsApprovalDecision) {
        this.seniorManagerSubmitsApprovalDecision = seniorManagerSubmitsApprovalDecision;
    }

    public QuoteDecision withSeniorManagerSubmitsApprovalDecision(Boolean seniorManagerSubmitsApprovalDecision) {
        this.seniorManagerSubmitsApprovalDecision = seniorManagerSubmitsApprovalDecision;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("quoteReviewScenarioDecisionDtos", quoteReviewScenarioDecisionDtos).append("quoteResourceIds", quoteResourceIds).append("jobRef", jobRef).append("currentFundingRouteId", currentFundingRouteId).append("jobIsApproved", jobIsApproved).append("overallQuoteValue", overallQuoteValue).append("requiresSeniorManagerApproval", requiresSeniorManagerApproval).append("internalNotes", internalNotes).append("openQuoteRequest", openQuoteRequest).append("seniorManagerSubmitsApprovalDecision", seniorManagerSubmitsApprovalDecision).toString();
    }
}
