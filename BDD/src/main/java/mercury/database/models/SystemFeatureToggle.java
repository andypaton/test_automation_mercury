package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SystemFeatureToggle {

    @Id
    @Column(name = "Id")
    Integer id;

    @Column(name = "Feature")
    String feature;

    @Column(name = "Active", columnDefinition="BIT")
    private Boolean active;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "{id = " + id + ", active = " + active + ", feature = " + feature +"}";
    }

}
