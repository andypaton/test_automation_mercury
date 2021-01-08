package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "GasQuestions")
public class GasQuestion {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "QuestionText")
    private String questionText;

    @Column(name = "ErrorMessage")
    private String errorMessage;

    @Column(name = "DestColumn")
    private String destColumn;

    @Column(name = "QType")
    private String qType;

    @Column(name = "QuesSeq")
    private Integer quesSeq;

    @Column(name = "Level")
    private Integer level;

    @Column(name = "DisplayFunction")
    private String displayFunction;

    @Column(name = "MaxValueFunction")
    private String maxValueFunction;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDestColumn() {
        return destColumn;
    }

    public void setDestColumn(String destColumn) {
        this.destColumn = destColumn;
    }

    public String getQType() {
        return qType;
    }

    public void setQType(String qType) {
        this.qType = qType;
    }

    public Integer getQuesSeq() {
        return quesSeq;
    }

    public void setQuesSeq(Integer quesSeq) {
        this.quesSeq = quesSeq;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDisplayFunction() {
        return displayFunction;
    }

    public void setDisplayFunction(String displayFunction) {
        this.displayFunction = displayFunction;
    }

    public String getMaxValueFunction() {
        return maxValueFunction;
    }

    public void setMaxValueFunction(String maxValueFunction) {
        this.maxValueFunction = maxValueFunction;
    }
}
