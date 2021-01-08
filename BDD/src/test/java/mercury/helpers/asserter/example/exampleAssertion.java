package mercury.helpers.asserter.example;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.helpers.asserter.common.AssertionFactory;

public class exampleAssertion {
	
	@Autowired AssertionFactory assertionFactory;


	public void runAsserter() {
        AssertJobStatus assertJobStatus = new AssertJobStatus("blah");
        assertionFactory.performAssertion(assertJobStatus);
	}
}
