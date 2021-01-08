package mercury.api.models.mercuryportal.api.quoteQueriesSubmission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__RequestVerificationToken",
    "JobRef",
    "QuoteApprovalScenarioId",
    "QueryId",
    "Response",
    "SubmitAction"
})
public class QuoteQueryResponse extends modelBase<QuoteQueryResponse> {

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;
    
    @JsonProperty("JobRef")
    private Integer jobRef;
    
    @JsonProperty("QuoteApprovalScenarioId")
    private String quoteApprovalScenarioId;
    
    @JsonProperty("QueryId")
    private String queryId;
    
    @JsonProperty("Response")
    private String response;
    
    @JsonProperty("SubmitAction")
    private String submitAction;

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    public QuoteQueryResponse withRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
        return this;
    }

    @JsonProperty("JobRef")
    public Integer getJobRef() {
        return jobRef;
    }

    @JsonProperty("JobRef")
    public void setJobRef(Integer jobRef) {
        this.jobRef = jobRef;
    }

    public QuoteQueryResponse withJobRef(Integer jobReference) {
        this.jobRef = jobReference;
        return this;
    }

    @JsonProperty("QuoteApprovalScenarioId")
    public String getQuoteApprovalScenarioId() {
        return quoteApprovalScenarioId;
    }

    @JsonProperty("QuoteApprovalScenarioId")
    public void setQuoteApprovalScenarioId(String quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
    }

    public QuoteQueryResponse withQuoteApprovalScenarioId(String quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
        return this;
    }

    @JsonProperty("QueryId")
    public String getQueryId() {
        return queryId;
    }

    @JsonProperty("QueryId")
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public QuoteQueryResponse withQueryId(String queryId) {
        this.queryId = queryId;
        return this;
    }

    @JsonProperty("Response")
    public String getResponse() {
        return response;
    }

    @JsonProperty("Response")
    public void setResponse(String response) {
        this.response = response;
    }

    public QuoteQueryResponse withResponse(String response) {
        this.response = response;
        return this;
    }

    @JsonProperty("SubmitAction")
    public String getSubmitAction() {
        return submitAction;
    }

    @JsonProperty("SubmitAction")
    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public QuoteQueryResponse withSubmitAction(String submitAction) {
        this.submitAction = submitAction;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken).append("jobRef", jobRef).append("quoteApprovalScenarioId", quoteApprovalScenarioId).append("queryId", queryId).append("response", response).append("submitAction", submitAction).toString();
    }

}

