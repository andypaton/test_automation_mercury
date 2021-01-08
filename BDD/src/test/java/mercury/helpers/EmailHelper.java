package mercury.helpers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static mercury.helpers.Constants.MAX_SYNC_TIMEOUT;
import static mercury.helpers.Constants.POLLING_INTERVAL_LONG;
import static mercury.helpers.Globalisation.localize;
import static mercury.helpers.Globalisation.toCurrency;
import static mercury.helpers.StringHelper.contains;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelperEmail;
import mercury.runtime.RuntimeState;

public class EmailHelper {

    @Autowired private DbHelperEmail dbHelperEmail;
    @Autowired private FgasPortalHelper fgasPortalHelper;
    @Autowired private RuntimeState runtimeState;
    @Autowired private TestData testData;

    private void assertEmailContent(String emailFields) throws Throwable {
        // Get appliance information from testData
        Map<String, Object> testDataMap = testData.getMap("FGAS Appliance");


        if (testDataMap.get("ApplianceInformation") != null) {
            runtimeState.scenario.write("Asserting Appliance Infomtaion is contained in the email : " + testDataMap.get("ApplianceInformation").toString());
            assertTrue("Appliance Infomtaion missing in email body", emailFields.contains(testDataMap.get("ApplianceInformation").toString()));
        }

        String gasTypeName = ("Incorrect refrigerant type displayed".equalsIgnoreCase(testData.getString("gasTypeId"))) ? testData.getString("gasTypeOtherId") : testData.getString("gasTypeId");
        if (gasTypeName != null) {
            runtimeState.scenario.write("Asserting Refrigerant Gas Type Used is contained in the email : " + gasTypeName);
            assertTrue("Refrigerant Gas Type Used missing in email body", emailFields.contains("Refrigerant Gas Type Used".concat(gasTypeName)));
        }

        Integer totalInstalled = fgasPortalHelper.getTotalInstalled();
        if (totalInstalled !=null) {
            runtimeState.scenario.write("Asserting Refrigerant Gas Installed is contained in the email : " + gasTypeName);
            assertTrue("Refrigerant Gas Type Used missing in email body", emailFields.contains("Refrigerant Gas Installed".concat(totalInstalled.toString()).concat(localize(".00 lbs"))));
        }

        testDataMap = testData.getMap("Maximum Charge");
        if (testDataMap != null) {
            if (testDataMap.get("NewAssetMaximumCharge") != null) {
                runtimeState.scenario.write("Asserting Suggested New Refrigerant Gas Maximum Charge is contained in the email : " + testDataMap.get("NewAssetMaximumCharge").toString());
                assertTrue("Suggested New Refrigerant Gas Maximum Charge missing in email body",emailFields.contains("Suggested New Refrigerant Gas Maximum Charge".concat(testDataMap.get("NewAssetMaximumCharge").toString()).concat(localize(".00 lbs"))));
            }

            if (testDataMap.get("ReasonForChangingMaximumCharge") != null) {
                runtimeState.scenario.write("Asserting Reason for Changing Maximum Charge is contained in the email : " + testDataMap.get("ReasonForChangingMaximumCharge").toString());
                assertTrue("Reason for Changing Maximum Charge missing in email body", emailFields.contains("Reason for Changing Maximum Charge".concat(testDataMap.get("ReasonForChangingMaximumCharge").toString())));
            }
        }
    }

    public void verifyFGasEmail(String title, int jobReference) throws Throwable {
        String body = await().atMost(MAX_SYNC_TIMEOUT, SECONDS).until(() -> dbHelperEmail.getCommunicatorEmail(title, jobReference, runtimeState.timestamp), notNullValue());

        runtimeState.scenario.write("Asserting content of email " + body);
        assertNotNull("Unexpected empty email", body);

        Map<String, Object> map = new HashMap<String, Object>();
        String questionAnswer = "";

        String regEx = "(?:<tr>)(?:\\s*)(?:<td>)((?:.*?)):(?:<\\/td>)(?:\\s*)(?:<td>)((?:.*?))(?:<\\/td>)";

        Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(body);

        while (m.find()) {
            String question = m.group(1);
            String answer = m.group(2);
            questionAnswer = questionAnswer.concat(question).concat(answer);
            map.put(question, answer);
        }

        assertEmailContent(questionAnswer);
    }

    public String getCommunicatorEmail(String title) throws Throwable {
        String body = await().atMost(MAX_SYNC_TIMEOUT, SECONDS).pollInterval(POLLING_INTERVAL_LONG, MILLISECONDS).until(() -> dbHelperEmail.getCommunicatorEmail(title, runtimeState.timestamp), notNullValue());
        runtimeState.scenario.write("Subject: " + title);
        runtimeState.scenario.write("Body:\n " + body);
        return body;
    }

    public String getCommunicatorEmail(String title, int jobReference) throws Throwable {
        String body = await().atMost(MAX_SYNC_TIMEOUT, SECONDS).pollInterval(POLLING_INTERVAL_LONG, MILLISECONDS).until(() -> dbHelperEmail.getCommunicatorEmail(title, jobReference, runtimeState.timestamp), notNullValue());
        runtimeState.scenario.write("Subject: " + title);
        runtimeState.scenario.write("Body:\n " + body);
        return body;
    }

    public void verifyUpliftEmail(String title, int jobReference) throws Throwable {
        String body = getCommunicatorEmail(title, jobReference);
        assertNotNull("Unexpected empty email", body);

        if(title.contains("Job Cost Exceeds")) {
            assertTrue("Expected Job Cost: " + toCurrency(testData.getBigDecimal("jobCost")), body.contains("The following job cost is now at " + toCurrency(testData.getBigDecimal("jobCost"))));
            assertTrue("Expected Uplift Amount: " + toCurrency(testData.getFloat("upliftAmount")), body.contains(toCurrency(testData.getFloat("upliftAmount"))));
        } else {
            assertTrue("Expected Job number: " + jobReference, contains(body, "Job number " + jobReference + " has been assigned to your company. Please log into the Mercury Portal to accept or decline the job."));
        }
    }

    public void verifyPartsRejectedEmail(String title, int jobReference) throws Throwable {
        String body = getCommunicatorEmail(title, jobReference);
        assertNotNull("Unexpected empty email", body);
        assertTrue("Expected email body: ", contains(body, "As your parts request has been declined, the status of your job has been set to returning. Please update your ETA as soon as possible."));
        assertTrue("Expected Job Number: " + jobReference, contains(body, "Job Number:.*" + jobReference));
        assertTrue("Expected Site: " + testData.getString("siteName"), contains(body, "Site:.*" + testData.getString("siteName")));
    }

    public void verifyReturningAwaitingPartsEmail(String title, int jobReference) throws Throwable {
        String body = getCommunicatorEmail(title, jobReference);
        assertNotNull("Unexpected empty email", body);
        assertTrue("Expected email body: ", contains(body, "Returning Reason:.*Awaiting Parts Info From Supplier.*New ETA:"));
        assertTrue("Expected Job Number: " + jobReference, contains(body, "Job Number:.*" + jobReference));
        assertTrue("Expected Site: " + testData.getString("siteName"), contains(body, "Site:.*" + testData.getString("siteName")));
    }

    public void verifyAwaitingPartsEmail(String title, int jobReference) throws Throwable {
        String body = getCommunicatorEmail(title, jobReference);
        assertNotNull("Unexpected empty email", body);
        assertTrue("Expected email body: ", contains(body, ".*Awaiting Parts.*New ETA:"));
        assertTrue("Expected Job Number: " + jobReference, contains(body, "Job Number:.*" + jobReference));
        assertTrue("Expected Site: " + testData.getString("siteName"), contains(body, "Site:.*" + testData.getString("siteName")));
    }

}
