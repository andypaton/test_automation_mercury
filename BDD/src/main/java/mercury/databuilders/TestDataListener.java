package mercury.databuilders;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.dao.TestAutomationLockDao;
import mercury.database.models.TestAutomationLock;

public class TestDataListener {

    @Autowired TestAutomationLockDao testAutomationLockDao;

    private String getType(String tag) {
        String type = null;
        switch (tag) {
        case "jobReference":
            type = "job";
            break;
        }
        return type;
    }

    private TestAutomationLock getLock(String type, Object value) {
        TestAutomationLock lock =  new TestAutomationLock();
        lock.setReference((int) value);
        lock.setType(type);
        lock.setReason("Test");  //Only tests will add test Data
        return lock;
    }

    /**
     * Adds the tag and value to the Test_Automation_Locks table
     *
     * @param tag
     * @param value
     */
    public void listen(String tag, Object value) {
        if (!(value instanceof Integer)) {
            return;
        }

        // Do Database update here
        String type = getType(tag);

        if (type == null ){ return; }

        // Create lock
        TestAutomationLock lock = getLock(type, value);
        testAutomationLockDao.create(lock);
    }

    /**
     * Removes a tag and value from the Test_Automation_Locks table
     *
     * @param tag
     * @param value
     */
    public void unListen(String tag, Object value) {
        if (!(value instanceof Integer)) {
            return;
        }
        // Do Database update here
        String type = getType(tag);

        if (type == null ){ return; }

        // Delete lock
        TestAutomationLock lock = getLock(type, value);
        testAutomationLockDao.delete(lock);
    }

    /**
     * Removes all tags and values from the Test_Automation_Locks table
     *
     * @param jsonTestData
     */
    public void unListenAll(String jsonTestData) {

    }
}
