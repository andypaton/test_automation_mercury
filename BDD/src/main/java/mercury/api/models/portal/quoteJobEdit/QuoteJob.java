package mercury.api.models.portal.quoteJobEdit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "FaultId",
    "FundingRouteId",
    "QuotePriorityId",
    "ScopeOfWorks",
    "NumberOfQuotesRequired",
    "InvitationsQuoted",
    "QuoteJobApprovalStatusId",
    "IsBypass",
    "MultiQuoteBypassReasonId",
    "MultiQuoteBypassNote",
    "MultiQuoteBypassQuoteRecommendationNote",
    "IsBypassQuoteRecommendationConfirmed"
})
public class QuoteJob extends modelBase<QuoteJob>{

    @JsonProperty("FaultId")
    private String faultId;

    @JsonProperty("FundingRouteId")
    private String fundingRouteId;

    @JsonProperty("QuotePriorityId")
    private String quotePriorityId;

    @JsonProperty("ScopeOfWorks")
    private String scopeOfWorks;

    @JsonProperty("NumberOfQuotesRequired")
    private String numberOfQuotesRequired;

    @JsonProperty("InvitationsQuoted")
    private List<InvitationsQuoted> invitationsQuoted = null;

    @JsonProperty("QuoteJobApprovalStatusId")
    private String quoteJobApprovalStatusId;

    @JsonProperty("IsBypass")
    private String isBypass;

    @JsonProperty("MultiQuoteBypassReasonId")
    private String multiQuoteBypassReasonId;

    @JsonProperty("MultiQuoteBypassNote")
    private String multiQuoteBypassNote;

    @JsonProperty("MultiQuoteBypassQuoteRecommendationNote")
    private String multiQuoteBypassQuoteRecommendationNote;

    @JsonProperty("IsBypassQuoteRecommendationConfirmed")
    private String isBypassQuoteRecommendationConfirmed;

    @JsonProperty("FaultId")
    public String getFaultId() {
        return faultId;
    }

    @JsonProperty("FaultId")
    public void setFaultId(String faultId) {
        this.faultId = faultId;
    }

    @JsonProperty("FundingRouteId")
    public String getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("FundingRouteId")
    public void setFundingRouteId(String fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    @JsonProperty("QuotePriorityId")
    public String getQuotePriorityId() {
        return quotePriorityId;
    }

    @JsonProperty("QuotePriorityId")
    public void setQuotePriorityId(String quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
    }

    @JsonProperty("ScopeOfWorks")
    public String getScopeOfWorks() {
        return scopeOfWorks;
    }

    @JsonProperty("ScopeOfWorks")
    public void setScopeOfWorks(String scopeOfWorks) {
        this.scopeOfWorks = scopeOfWorks;
    }

    @JsonProperty("NumberOfQuotesRequired")
    public String getNumberOfQuotesRequired() {
        return numberOfQuotesRequired;
    }

    @JsonProperty("NumberOfQuotesRequired")
    public void setNumberOfQuotesRequired(String numberOfQuotesRequired) {
        this.numberOfQuotesRequired = numberOfQuotesRequired;
    }

    @JsonProperty("InvitationsQuoted")
    public List<InvitationsQuoted> getInvitationsQuoted() {
        return invitationsQuoted;
    }

    @JsonProperty("InvitationsQuoted")
    public void setInvitationsQuoted(List<InvitationsQuoted> invitationsQuoted) {
        this.invitationsQuoted = invitationsQuoted;
    }

    public QuoteJob withInvitationsQuoted(List<InvitationsQuoted> invitationsQuoted) {
        this.invitationsQuoted = invitationsQuoted;
        return this;
    }

    @JsonProperty("QuoteJobApprovalStatusId")
    public String getQuoteJobApprovalStatusId() {
        return quoteJobApprovalStatusId;
    }

    @JsonProperty("QuoteJobApprovalStatusId")
    public void setQuoteJobApprovalStatusId(String quoteJobApprovalStatusId) {
        this.quoteJobApprovalStatusId = quoteJobApprovalStatusId;
    }

    @JsonProperty("IsBypass")
    public String getIsBypass() {
        return isBypass;
    }

    @JsonProperty("IsBypass")
    public void setIsBypass(String isBypass) {
        this.isBypass = isBypass;
    }

    @JsonProperty("MultiQuoteBypassReasonId")
    public String getMultiQuoteBypassReasonId() {
        return multiQuoteBypassReasonId;
    }

    @JsonProperty("MultiQuoteBypassReasonId")
    public void setMultiQuoteBypassReasonId(String multiQuoteBypassReasonId) {
        this.multiQuoteBypassReasonId = multiQuoteBypassReasonId;
    }

    @JsonProperty("MultiQuoteBypassNote")
    public String getMultiQuoteBypassNote() {
        return multiQuoteBypassNote;
    }

    @JsonProperty("MultiQuoteBypassNote")
    public void setMultiQuoteBypassNote(String multiQuoteBypassNote) {
        this.multiQuoteBypassNote = multiQuoteBypassNote;
    }

    @JsonProperty("MultiQuoteBypassQuoteRecommendationNote")
    public String getMultiQuoteBypassQuoteRecommendationNote() {
        return multiQuoteBypassQuoteRecommendationNote;
    }

    @JsonProperty("MultiQuoteBypassQuoteRecommendationNote")
    public void setMultiQuoteBypassQuoteRecommendationNote(String multiQuoteBypassQuoteRecommendationNote) {
        this.multiQuoteBypassQuoteRecommendationNote = multiQuoteBypassQuoteRecommendationNote;
    }

    @JsonProperty("IsBypassQuoteRecommendationConfirmed")
    public String getIsBypassQuoteRecommendationConfirmed() {
        return isBypassQuoteRecommendationConfirmed;
    }

    @JsonProperty("IsBypassQuoteRecommendationConfirmed")
    public void setIsBypassQuoteRecommendationConfirmed(String isBypassQuoteRecommendationConfirmed) {
        this.isBypassQuoteRecommendationConfirmed = isBypassQuoteRecommendationConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("faultId", faultId).append("fundingRouteId", fundingRouteId).append("quotePriorityId", quotePriorityId).append("scopeOfWorks", scopeOfWorks).append("numberOfQuotesRequired", numberOfQuotesRequired).append("quoteJobApprovalStatusId", quoteJobApprovalStatusId).append("isBypass", isBypass).append("multiQuoteBypassReasonId", multiQuoteBypassReasonId).append("multiQuoteBypassNote", multiQuoteBypassNote).append("multiQuoteBypassQuoteRecommendationNote", multiQuoteBypassQuoteRecommendationNote).append("isBypassQuoteRecommendationConfirmed", isBypassQuoteRecommendationConfirmed).toString();
    }

}