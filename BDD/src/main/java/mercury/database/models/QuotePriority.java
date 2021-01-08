package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class QuotePriority {

	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "Name") 
    private String name;

    @Column(name = "DueInDays") 
    private Integer dueInDays;
    
    @Column(name = "QuoteRequestPriority") 
    private Boolean quoteRequestPriority;
    
    @Column(name = "RetrospectiveQuote") 
    private Boolean retrospectiveQuote;

	@Column(name = "FundingRouteId")
    private Integer fundingRouteId;
    
    @Column(name = "PriorityValue") 
    private Integer priorityValue;
    
    @Column(name = "ImmediateCallout") 
    private Boolean immediateCallout;
    
    @Column(name = "MinimumQuotesRequired")
    private Integer minimumQuotesRequired;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDueInDays() {
		return dueInDays;
	}

	public void setDueInDays(Integer dueInDays) {
		this.dueInDays = dueInDays;
	}

	public Boolean getQuoteRequestPriority() {
		return quoteRequestPriority;
	}

	public void setQuoteRequestPriority(Boolean quoteRequestPriority) {
		this.quoteRequestPriority = quoteRequestPriority;
	}

	public Boolean getRetrospectiveQuote() {
		return retrospectiveQuote;
	}

	public void setRetrospectiveQuote(Boolean retrospectiveQuote) {
		this.retrospectiveQuote = retrospectiveQuote;
	}

	public Integer getFundingRouteId() {
		return fundingRouteId;
	}

	public void setFundingRouteId(Integer fundingRouteId) {
		this.fundingRouteId = fundingRouteId;
	}

	public Integer getPriorityValue() {
		return priorityValue;
	}

	public void setPriorityValue(Integer priorityValue) {
		this.priorityValue = priorityValue;
	}

	public Boolean getImmediateCallout() {
		return immediateCallout;
	}

	public void setImmediateCallout(Boolean immediateCallout) {
		this.immediateCallout = immediateCallout;
	}

	public Integer getMinimumQuotesRequired() {
		return minimumQuotesRequired;
	}

	public void setMinimumQuotesRequired(Integer minimumQuotesRequired) {
		this.minimumQuotesRequired = minimumQuotesRequired;
	}


}
