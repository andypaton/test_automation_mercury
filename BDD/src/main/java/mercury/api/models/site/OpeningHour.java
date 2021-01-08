package mercury.api.models.site;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "is24Hr", "isClosed", "opens", "closes" })
public class OpeningHour {

    @JsonProperty("id")
    private Object id;
    
    @JsonProperty("is24Hr")
    private Boolean is24Hr;
    
    @JsonProperty("isClosed")
    private Boolean isClosed;
    
    @JsonProperty("opens")
    private Object opens;
    
    @JsonProperty("closes")
    private Object closes;

    
    @JsonProperty("id")
    public Object getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Object id) {
        this.id = id;
    }

    @JsonProperty("is24Hr")
    public Boolean getIs24Hr() {
        return is24Hr;
    }

    @JsonProperty("is24Hr")
    public void setIs24Hr(Boolean is24Hr) {
        this.is24Hr = is24Hr;
    }

    @JsonProperty("isClosed")
    public Boolean getIsClosed() {
        return isClosed;
    }

    @JsonProperty("isClosed")
    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    @JsonProperty("opens")
    public Object getOpens() {
        return opens;
    }

    @JsonProperty("opens")
    public void setOpens(Object opens) {
        this.opens = opens;
    }

    @JsonProperty("closes")
    public Object getCloses() {
        return closes;
    }

    @JsonProperty("closes")
    public void setCloses(Object closes) {
        this.closes = closes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("is24Hr", is24Hr).append("isClosed", isClosed).append("opens", opens).append("closes", closes).toString();
    }

}