package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "job",
    "resourceQuotes"
})
public class JobDetails extends modelBase<JobDetails>  {

    @JsonProperty("job")
    private Job job;
    
    @JsonProperty("resourceQuotes")
    private List<ResourceQuote> resourceQuotes = null;

    @JsonProperty("job")
    public Job getJob() {
        return job;
    }

    @JsonProperty("job")
    public void setJob(Job job) {
        this.job = job;
    }

    public JobDetails withJob(Job job) {
        this.job = job;
        return this;
    }

    @JsonProperty("resourceQuotes")
    public List<ResourceQuote> getResourceQuotes() {
        return resourceQuotes;
    }

    @JsonProperty("resourceQuotes")
    public void setResourceQuotes(List<ResourceQuote> resourceQuotes) {
        this.resourceQuotes = resourceQuotes;
    }

    public JobDetails withResourceQuotes(List<ResourceQuote> resourceQuotes) {
        this.resourceQuotes = resourceQuotes;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("job", job).append("resourceQuotes", resourceQuotes).toString();
    }

}
