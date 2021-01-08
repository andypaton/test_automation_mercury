package mercury.api.models.fundingrequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "code",
    "name",
    "reasonCategory",
    "createdOn",
    "createdBy",
    "updatedOn",
    "updatedBy",
    "active"
})
public class NoAmountReason  extends modelBase<NoAmountReason> {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("reasonCategory")
    private String reasonCategory;
    
    @JsonProperty("createdOn")
    private String createdOn;
    
    @JsonProperty("createdBy")
    private String createdBy;
    
    @JsonProperty("updatedOn")
    private String updatedOn;
    
    @JsonProperty("updatedBy")
    private String updatedBy;
    
    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public NoAmountReason withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    public NoAmountReason withCode(String code) {
        this.code = code;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public NoAmountReason withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("reasonCategory")
    public String getReasonCategory() {
        return reasonCategory;
    }

    @JsonProperty("reasonCategory")
    public void setReasonCategory(String reasonCategory) {
        this.reasonCategory = reasonCategory;
    }

    public NoAmountReason withReasonCategory(String reasonCategory) {
        this.reasonCategory = reasonCategory;
        return this;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public NoAmountReason withCreatedOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public NoAmountReason withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @JsonProperty("updatedOn")
    public String getUpdatedOn() {
        return updatedOn;
    }

    @JsonProperty("updatedOn")
    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public NoAmountReason withUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    @JsonProperty("updatedBy")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updatedBy")
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public NoAmountReason withUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    public NoAmountReason withActive(Boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("code", code).append("name", name).append("reasonCategory", reasonCategory).append("createdOn", createdOn).append("createdBy", createdBy).append("updatedOn", updatedOn).append("updatedBy", updatedBy).append("active", active).toString();
    }

}