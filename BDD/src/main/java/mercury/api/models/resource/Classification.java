package mercury.api.models.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "classificationId", "priority" })
public class Classification {

    @JsonProperty("classificationId")
    private Integer classificationId;
    
    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("classificationId")
    public Integer getClassificationId() {
        return classificationId;
    }

    @JsonProperty("classificationId")
    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("classificationId", classificationId).append("priority", priority).toString();
    }

}