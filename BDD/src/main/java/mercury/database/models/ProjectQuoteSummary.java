package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProjectQuoteSummary {

	@Id
	@Column(name = "ProjectQuoteId")
	Integer projectQuoteId;
	
	@Column(name = "ProjectHeaderId")
	Integer projectHeaderId;
	
	@Column(name = "QuoteHeaderId")
	Integer quoteHeaderId;

	public Integer getProjectQuoteId() {
		return projectQuoteId;
	}
	public void setProjectQuoteId(Integer projectQuoteId) {
		this.projectQuoteId = projectQuoteId;
	}

	public Integer getProjectHeaderId() {
		return projectHeaderId;
	}
	public void setProjectHeaderId(Integer projectHeaderId) {
		this.projectHeaderId = projectHeaderId;
	}

	public Integer getQuoteHeaderId() {
		return quoteHeaderId;
	}
	public void setQuoteHeaderId(Integer quoteHeaderId) {
		this.quoteHeaderId = quoteHeaderId;
	}
	
	
}
