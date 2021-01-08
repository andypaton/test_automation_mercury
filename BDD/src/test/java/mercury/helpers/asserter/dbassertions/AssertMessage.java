package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.List;

import mercury.database.dao.MessageDao;
import mercury.database.models.Message;
import mercury.helpers.asserter.common.AssertTask;

public class AssertMessage implements AssertTask {
    
	private MessageDao messageDao;

	private int jobReference;
	private String routingKey;
	private String eventtype;
	private Integer resourceAssignmentStatus;
	private Integer resourceAssignmentReason;

	private String failureMessage;
		
    public AssertMessage(MessageDao messageDao, Integer  jobReference, String routingKey, String eventtype, Integer resourceAssignmentStatus, Integer resourceAssignmentReason) {
    	this.messageDao = messageDao;
    	this.jobReference = jobReference;
        this.routingKey = routingKey;
        this.eventtype = eventtype;
        this.resourceAssignmentStatus = resourceAssignmentStatus;
        this.resourceAssignmentReason = resourceAssignmentReason;
    }

    @Override
    public boolean execute() {
    	List<Message> message;	
        try {        		
    		message = messageDao.getMessages(jobReference, routingKey, eventtype,resourceAssignmentStatus, resourceAssignmentReason);		
    		assertNotNull("Unexpected null record set", message);		
    		assertFalse("Unexpected empty record set", message.isEmpty());

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }
        
        return true;
    }

    @Override
    public String getTaskName() {
        return this.getClass().getName();
    }

    @Override
    public String getTaskFailureMessage() {
        return failureMessage;
    }
}
