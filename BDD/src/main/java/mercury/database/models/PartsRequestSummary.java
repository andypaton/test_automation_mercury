package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PartsRequestSummary {

	@Id
	@Column(name = "HeaderId")
	private Integer headerId;
	
	@Column(name = "LineId")
	private Integer lineId;
	
	@Column(name = "JobReference")
	private Integer jobReference;
	
	@Column(name = "POID")
	private Integer pOId;

	public Integer getHeaderId() {
		return headerId;
	}

	public void setHeaderId(Integer headerId) {
		this.headerId = headerId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public Integer getJobReference() {
		return jobReference;
	}

	public void setJobReference(Integer jobReference) {
		this.jobReference = jobReference;
	}

	public Integer getpOId() {
		return pOId;
	}

	public void setpOId(Integer pOId) {
		this.pOId = pOId;
	}
}
