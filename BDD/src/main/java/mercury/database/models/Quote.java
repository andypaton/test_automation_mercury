package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Quote")
public class Quote {

	@Id
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "JobId") 
    private Integer jobId;

    @Column(name = "QuoteRequestApproverId") 
    private Integer quoteRequestApproverId;

    @Column(name = "QuoteApproverId") 
    private Integer quoteApproverId;

    @Column(name = "ContractorToQuoteId") 
    private Integer contractorToQuoteId;

    @Column(name = "FundingRouteId") 
    private Integer fundingRouteId;

    @Column(name = "QuotePriorityId") 
    private Integer quotePriorityId;

    @Column(name = "JobProcessTypeId") 
    private Integer jobProcessTypeId;

    @Column(name = "IsApproved") 
    private String isApproved;

    @Column(name = "IsQuoteRequestHighPriority") 
    private String isQuoteRequestHighPriority;

    @Column(name = "CreatedOn") 
    private java.sql.Timestamp createdOn;

    @Column(name = "CreatedBy") 
    private String createdBy;

    @Column(name = "UpdatedOn") 
    private java.sql.Timestamp updatedOn;

    @Column(name = "UpdatedBy") 
    private String updatedBy;



    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }


    public Integer getJobId() {
      return jobId;
    }

    public void setJobId(Integer jobId) {
      this.jobId = jobId;
    }


    public Integer getQuoteRequestApproverId() {
      return quoteRequestApproverId;
    }

    public void setQuoteRequestApproverId(Integer quoteRequestApproverId) {
      this.quoteRequestApproverId = quoteRequestApproverId;
    }


    public Integer getQuoteApproverId() {
      return quoteApproverId;
    }

    public void setQuoteApproverId(Integer quoteApproverId) {
      this.quoteApproverId = quoteApproverId;
    }


    public Integer getContractorToQuoteId() {
      return contractorToQuoteId;
    }

    public void setContractorToQuoteId(Integer contractorToQuoteId) {
      this.contractorToQuoteId = contractorToQuoteId;
    }


    public Integer getFundingRouteId() {
      return fundingRouteId;
    }

    public void setFundingRouteId(Integer fundingRouteId) {
      this.fundingRouteId = fundingRouteId;
    }


    public Integer getQuotePriorityId() {
      return quotePriorityId;
    }

    public void setQuotePriorityId(Integer quotePriorityId) {
      this.quotePriorityId = quotePriorityId;
    }


    public Integer getJobProcessTypeId() {
      return jobProcessTypeId;
    }

    public void setJobProcessTypeId(Integer jobProcessTypeId) {
      this.jobProcessTypeId = jobProcessTypeId;
    }


    public String getIsApproved() {
      return isApproved;
    }

    public void setIsApproved(String isApproved) {
      this.isApproved = isApproved;
    }


    public String getIsQuoteRequestHighPriority() {
      return isQuoteRequestHighPriority;
    }

    public void setIsQuoteRequestHighPriority(String isQuoteRequestHighPriority) {
      this.isQuoteRequestHighPriority = isQuoteRequestHighPriority;
    }


    public java.sql.Timestamp getCreatedOn() {
      return createdOn;
    }

    public void setCreatedOn(java.sql.Timestamp createdOn) {
      this.createdOn = createdOn;
    }


    public String getCreatedBy() {
      return createdBy;
    }

    public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
    }


    public java.sql.Timestamp getUpdatedOn() {
      return updatedOn;
    }

    public void setUpdatedOn(java.sql.Timestamp updatedOn) {
      this.updatedOn = updatedOn;
    }


    public String getUpdatedBy() {
      return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
      this.updatedBy = updatedBy;
    }

}
