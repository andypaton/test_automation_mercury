package mercury.api.models.portal.quoteJobEdit;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import mercury.api.models.modelBase;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "QuoteResourceIds",
    "QuoteJob",
    "__RequestVerificationToken"
})
public class QuoteApprove extends modelBase<QuoteApprove>{

    @JsonProperty("QuoteResourceIds")
    private List<String> quoteResourceIds = null;

    @JsonProperty("QuoteJob")
    private QuoteJob quoteJob;

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("QuoteResourceIds")
    public List<String> getQuoteResourceIds() {
        return quoteResourceIds;
    }

    @JsonProperty("QuoteResourceIds")
    public void setQuoteResourceIds(List<String> quoteResourceIds) {
        this.quoteResourceIds = quoteResourceIds;
    }

    @JsonProperty("QuoteJob")
    public QuoteJob getQuoteJob() {
        return quoteJob;
    }

    @JsonProperty("QuoteJob")
    public void setQuoteJob(QuoteJob quoteJob) {
        this.quoteJob = quoteJob;
    }

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("quoteResourceIds", quoteResourceIds).append("quoteJob", quoteJob).append("requestVerificationToken", requestVerificationToken).toString();
    }
}
