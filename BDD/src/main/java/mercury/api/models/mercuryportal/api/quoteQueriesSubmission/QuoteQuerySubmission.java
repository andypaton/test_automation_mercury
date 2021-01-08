package mercury.api.models.mercuryportal.api.quoteQueriesSubmission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobRef",
    "quoteApprovalScenarioId",
    "queryReasonId",
    "queryText"
})
public class QuoteQuerySubmission extends modelBase<QuoteQuerySubmission> {

    @JsonProperty("jobRef")
    private Integer jobRef;
    
    @JsonProperty("quoteApprovalScenarioId")
    private Integer quoteApprovalScenarioId;
    
    @JsonProperty("queryReasonId")
    private Integer queryReasonId;
    
    @JsonProperty("queryText")
    private String queryText;

    @JsonProperty("jobRef")
    public Integer getJobRef() {
        return jobRef;
    }

    @JsonProperty("jobRef")
    public void setJobRef(Integer jobRef) {
        this.jobRef = jobRef;
    }

    public QuoteQuerySubmission withJobRef(Integer jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("quoteApprovalScenarioId")
    public Integer getQuoteApprovalScenarioId() {
        return quoteApprovalScenarioId;
    }

    @JsonProperty("quoteApprovalScenarioId")
    public void setQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
    }

    public QuoteQuerySubmission withQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
        return this;
    }

    @JsonProperty("queryReasonId")
    public Integer getQueryReasonId() {
        return queryReasonId;
    }

    @JsonProperty("queryReasonId")
    public void setQueryReasonId(Integer queryReasonId) {
        this.queryReasonId = queryReasonId;
    }

    public QuoteQuerySubmission withQueryReasonId(Integer queryReasonId) {
        this.queryReasonId = queryReasonId;
        return this;
    }

    @JsonProperty("queryText")
    public String getQueryText() {
        return queryText;
    }

    @JsonProperty("queryText")
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public QuoteQuerySubmission withQueryText(String queryText) {
        this.queryText = queryText;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobRef", jobRef).append("quoteApprovalScenarioId", quoteApprovalScenarioId).append("queryReasonId", queryReasonId).append("queryText", queryText).toString();
    }

}