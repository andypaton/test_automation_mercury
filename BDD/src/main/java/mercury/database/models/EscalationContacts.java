package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EscalationContacts {

	@Id
	@Column(name = "Id")	
	Integer id;

	@Column(name = "EscalationEmailHistoryId")		
	Integer escalationEmailHistoryId;

	@Column(name = "RecipientId")	
	Integer recipientId;

	@Column(name = "RecipientEmailAddress")  
	String recipientEmailAddress;

	@Column(name = "CreatedOn")	
	String createdOn;

	@Column(name = "CreatedBy")	
	String createdBy;

	@Column(name = "Name")	
	String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEscalationEmailHistoryId() {
		return escalationEmailHistoryId;
	}

	public void setEscalationEmailHistoryId(Integer escalationEmailHistoryId) {
		this.escalationEmailHistoryId = escalationEmailHistoryId;
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public String getRecipientEmailAddress() {
		return recipientEmailAddress;
	}

	public void setRecipientEmailAddress(String recipientEmailAddress) {
		this.recipientEmailAddress = recipientEmailAddress;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
