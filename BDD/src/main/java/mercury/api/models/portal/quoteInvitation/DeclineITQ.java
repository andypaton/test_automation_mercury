package mercury.api.models.portal.quoteInvitation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobRef",
    "QuoteInvitationDeclinedReasonId",
    "reason"
})
public class DeclineITQ extends modelBase<DeclineITQ> {

    @JsonProperty("jobRef")
    private String jobRef;
    @JsonProperty("QuoteInvitationDeclinedReasonId")
    private Integer quoteInvitationDeclinedReasonId;
    @JsonProperty("reason")
    private String reason;

    @JsonProperty("jobRef")
    public String getJobRef() {
        return jobRef;
    }

    @JsonProperty("jobRef")
    public void setJobRef(String jobRef) {
        this.jobRef = jobRef;
    }

    public DeclineITQ withJobRef(String jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("QuoteInvitationDeclinedReasonId")
    public Integer getQuoteInvitationDeclinedReasonId() {
        return quoteInvitationDeclinedReasonId;
    }

    @JsonProperty("QuoteInvitationDeclinedReasonId")
    public void setQuoteInvitationDeclinedReasonId(Integer quoteInvitationDeclinedReasonId) {
        this.quoteInvitationDeclinedReasonId = quoteInvitationDeclinedReasonId;
    }

    public DeclineITQ withQuoteInvitationDeclinedReasonId(Integer quoteInvitationDeclinedReasonId) {
        this.quoteInvitationDeclinedReasonId = quoteInvitationDeclinedReasonId;
        return this;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("reason")
    public void setReason(String reason) {
        this.reason = reason;
    }

    public DeclineITQ withReason(String string) {
        this.reason = string;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobRef", jobRef).append("quoteInvitationDeclinedReasonId", quoteInvitationDeclinedReasonId).append("reason", reason).toString();
    }

}