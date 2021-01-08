package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PPMJob {

    @Id
    @Column(name = "PPMID")
    private Integer ppmid;

    @Column(name = "Status")
    private String status;

    @Column(name = "CalloutStatus")
    private String calloutStatus;

    public Integer getPpmId() {
        return ppmid;
    }

    public String getCalloutStatus() {
        return calloutStatus;
    }

    public String getStatus() {
        return status;
    }

}
