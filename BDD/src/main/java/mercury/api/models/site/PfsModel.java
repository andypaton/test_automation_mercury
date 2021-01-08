package mercury.api.models.site;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "pfsIslands", "hasGradeDown", "pfsUndoStack", "_pfsFaultList", "workingGradeArray", "gradeNozzleCount", "gradeFaultyNotWorkingNozzleCount", "gradeFaultyStillWorkingNozzleCount",
        "gradeWorkingNozzleCount" })
public class PfsModel {

    @JsonProperty("pfsIslands")
    private List<Object> pfsIslands = null;
    
    @JsonProperty("hasGradeDown")
    private Boolean hasGradeDown;
    
    @JsonProperty("pfsUndoStack")
    private List<Object> pfsUndoStack = null;
    
    @JsonProperty("_pfsFaultList")
    private Object pfsFaultList;
    
    @JsonProperty("workingGradeArray")
    private List<Object> workingGradeArray = null;
    
    @JsonProperty("gradeNozzleCount")
    private List<Object> gradeNozzleCount = null;
    
    @JsonProperty("gradeFaultyNotWorkingNozzleCount")
    private List<Object> gradeFaultyNotWorkingNozzleCount = null;
    
    @JsonProperty("gradeFaultyStillWorkingNozzleCount")
    private List<Object> gradeFaultyStillWorkingNozzleCount = null;
    
    @JsonProperty("gradeWorkingNozzleCount")
    private List<Object> gradeWorkingNozzleCount = null;
    


    @JsonProperty("pfsIslands")
    public List<Object> getPfsIslands() {
        return pfsIslands;
    }

    @JsonProperty("pfsIslands")
    public void setPfsIslands(List<Object> pfsIslands) {
        this.pfsIslands = pfsIslands;
    }

    @JsonProperty("hasGradeDown")
    public Boolean getHasGradeDown() {
        return hasGradeDown;
    }

    @JsonProperty("hasGradeDown")
    public void setHasGradeDown(Boolean hasGradeDown) {
        this.hasGradeDown = hasGradeDown;
    }

    @JsonProperty("pfsUndoStack")
    public List<Object> getPfsUndoStack() {
        return pfsUndoStack;
    }

    @JsonProperty("pfsUndoStack")
    public void setPfsUndoStack(List<Object> pfsUndoStack) {
        this.pfsUndoStack = pfsUndoStack;
    }

    @JsonProperty("_pfsFaultList")
    public Object getPfsFaultList() {
        return pfsFaultList;
    }

    @JsonProperty("_pfsFaultList")
    public void setPfsFaultList(Object pfsFaultList) {
        this.pfsFaultList = pfsFaultList;
    }

    @JsonProperty("workingGradeArray")
    public List<Object> getWorkingGradeArray() {
        return workingGradeArray;
    }

    @JsonProperty("workingGradeArray")
    public void setWorkingGradeArray(List<Object> workingGradeArray) {
        this.workingGradeArray = workingGradeArray;
    }

    @JsonProperty("gradeNozzleCount")
    public List<Object> getGradeNozzleCount() {
        return gradeNozzleCount;
    }

    @JsonProperty("gradeNozzleCount")
    public void setGradeNozzleCount(List<Object> gradeNozzleCount) {
        this.gradeNozzleCount = gradeNozzleCount;
    }

    @JsonProperty("gradeFaultyNotWorkingNozzleCount")
    public List<Object> getGradeFaultyNotWorkingNozzleCount() {
        return gradeFaultyNotWorkingNozzleCount;
    }

    @JsonProperty("gradeFaultyNotWorkingNozzleCount")
    public void setGradeFaultyNotWorkingNozzleCount(List<Object> gradeFaultyNotWorkingNozzleCount) {
        this.gradeFaultyNotWorkingNozzleCount = gradeFaultyNotWorkingNozzleCount;
    }

    @JsonProperty("gradeFaultyStillWorkingNozzleCount")
    public List<Object> getGradeFaultyStillWorkingNozzleCount() {
        return gradeFaultyStillWorkingNozzleCount;
    }

    @JsonProperty("gradeFaultyStillWorkingNozzleCount")
    public void setGradeFaultyStillWorkingNozzleCount(List<Object> gradeFaultyStillWorkingNozzleCount) {
        this.gradeFaultyStillWorkingNozzleCount = gradeFaultyStillWorkingNozzleCount;
    }

    @JsonProperty("gradeWorkingNozzleCount")
    public List<Object> getGradeWorkingNozzleCount() {
        return gradeWorkingNozzleCount;
    }

    @JsonProperty("gradeWorkingNozzleCount")
    public void setGradeWorkingNozzleCount(List<Object> gradeWorkingNozzleCount) {
        this.gradeWorkingNozzleCount = gradeWorkingNozzleCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("pfsIslands", pfsIslands).append("hasGradeDown", hasGradeDown).append("pfsUndoStack", pfsUndoStack).append("pfsFaultList", pfsFaultList)
                .append("workingGradeArray", workingGradeArray).append("gradeNozzleCount", gradeNozzleCount).append("gradeFaultyNotWorkingNozzleCount", gradeFaultyNotWorkingNozzleCount)
                .append("gradeFaultyStillWorkingNozzleCount", gradeFaultyStillWorkingNozzleCount).append("gradeWorkingNozzleCount", gradeWorkingNozzleCount)
                .toString();
    }

}