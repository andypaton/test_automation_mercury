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
    "jobRejectedReasonId",
    "requiresSeniorManagerApproval",
    "internalNotes",
    "seniorManagerSubmitsApprovalDecision"
})
public class QuoteRejectionDecision extends modelBase<QuoteRejectionDecision> {

    @JsonProperty("quoteReviewScenarioRejectionDecisionDtos")
    private List<QuoteReviewScenarioRejectionDecisionDto> quoteReviewScenarioRejectionDecisionDtos = null;

    @JsonProperty("quoteResourceIds")
    private List<Object> quoteResourceIds = null;

    @JsonProperty("jobRef")
    private Integer jobRef;

    @JsonProperty("currentFundingRouteId")
    private Integer currentFundingRouteId;

    @JsonProperty("jobIsApproved")
    private Boolean jobIsApproved;

    @JsonProperty("jobRejectedReasonId")
    private Integer jobRejectedReasonId;

    @JsonProperty("requiresSeniorManagerApproval")
    private Boolean requiresSeniorManagerApproval;

    @JsonProperty("internalNotes")
    private String internalNotes;

    @JsonProperty("seniorManagerSubmitsApprovalDecision")
    private Boolean seniorManagerSubmitsApprovalDecision;

    @JsonProperty("quoteReviewScenarioRejectionDecisionDtos")
    public List<QuoteReviewScenarioRejectionDecisionDto> getQuoteReviewScenarioRejectionDecisionDtos() {
        return quoteReviewScenarioRejectionDecisionDtos;
    }

    @JsonProperty("quoteReviewScenarioRejectionDecisionDtos")
    public void setQuoteReviewScenarioRejectionDecisionDtos(List<QuoteReviewScenarioRejectionDecisionDto> quoteReviewScenarioRejectionDecisionDtos) {
        this.quoteReviewScenarioRejectionDecisionDtos = quoteReviewScenarioRejectionDecisionDtos;
    }

    public QuoteRejectionDecision withQuoteReviewScenarioRejectionDecisionDtos(List<QuoteReviewScenarioRejectionDecisionDto> quoteReviewScenarioRejectionDecisionDtos) {
        this.quoteReviewScenarioRejectionDecisionDtos = quoteReviewScenarioRejectionDecisionDtos;
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

    public QuoteRejectionDecision withQuoteResourceIds(List<Object> quoteResourceIds) {
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

    public QuoteRejectionDecision withJobRef(Integer jobRef) {
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

    public QuoteRejectionDecision withCurrentFundingRouteId(Integer currentFundingRouteId) {
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

    public QuoteRejectionDecision withJobIsApproved(Boolean jobIsApproved) {
        this.jobIsApproved = jobIsApproved;
        return this;
    }

    @JsonProperty("jobRejectedReasonId")
    public Integer getJobRejectedReasonId() {
        return jobRejectedReasonId;
    }

    @JsonProperty("jobRejectedReasonId")
    public void setJobRejectedReasonId(Integer jobRejectedReasonId) {
        this.jobRejectedReasonId = jobRejectedReasonId;
    }

    public QuoteRejectionDecision withJobRejectedReasonId(Integer jobRejectedReasonId) {
        this.jobRejectedReasonId = jobRejectedReasonId;
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

    public QuoteRejectionDecision withRequiresSeniorManagerApproval(Boolean requiresSeniorManagerApproval) {
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

    public QuoteRejectionDecision withInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
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

    public QuoteRejectionDecision withSeniorManagerSubmitsApprovalDecision(Boolean seniorManagerSubmitsApprovalDecision) {
        this.seniorManagerSubmitsApprovalDecision = seniorManagerSubmitsApprovalDecision;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("quoteReviewScenarioRejectionDecisionDtos", quoteReviewScenarioRejectionDecisionDtos).append("quoteResourceIds", quoteResourceIds).append("jobRef", jobRef).append("currentFundingRouteId", currentFundingRouteId).append("jobIsApproved", jobIsApproved).append("requiresSeniorManagerApproval", requiresSeniorManagerApproval).append("internalNotes", internalNotes).append("jobRejectedReasonId", jobRejectedReasonId).append("seniorManagerSubmitsApprovalDecision", seniorManagerSubmitsApprovalDecision).toString();
    }
}
