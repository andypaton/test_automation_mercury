package mercury.helpers.asserter.common;

public interface AssertTask {

    boolean execute();

    String getTaskName();

    String getTaskFailureMessage();
}
