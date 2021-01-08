package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SystemSubFeatureToggle {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "FeatureId")
    Integer featureId;

    @Column(name = "SubFeature")
    String subFeature;

    @Column(name = "Active", columnDefinition="BIT")
    private Boolean active;

    @Column(name = "GroupId")
    private Integer groupId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public String getSubFeature() {
        return subFeature;
    }

    public void setSubFeature(String subFeature) {
        this.subFeature = subFeature;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "{id = " + id + ", featureId = " + featureId + ", active = " + active + ", group = " + groupId + ", subFeature = " + subFeature + "} ";
    }

}
