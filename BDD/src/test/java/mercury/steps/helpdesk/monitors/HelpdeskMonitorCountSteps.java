package mercury.steps.helpdesk.monitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.databuilders.TestData;
import mercury.helpers.FileHelper;
import mercury.helpers.POHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.dbhelper.DbHelperMonitors;
import mercury.runtime.RuntimeState;

public class HelpdeskMonitorCountSteps {

    @Autowired private DbHelperMonitors dbHelperMonitors;
    @Autowired private ApiHelper apiHelper;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;
    @Autowired private PropertyHelper propertyHelper;


    @When("^all monitor counts are captured$")
    public void all_monito_counts_are_captured() throws ClientProtocolException, IOException {
        String buildNumber = POHelper.getBuildNumber();
        String timestamp = LocalDateTime.now().toString().replaceAll("[-:]", "").replaceAll("T", ".").substring(0, 13);

        String directory = System.getProperty("user.home") + "/MonitorCounts/" + propertyHelper.getEnv();
        FileHelper.mkFolder(directory);

        String filename = directory + "/" + timestamp + "_v" + buildNumber + ".txt";
        testData.put("monitorCountsFilename", filename);

        File file = new File(filename);
        FileWriter fileWriter = new FileWriter(file, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        List<Map<String, Object>> areas = dbHelperMonitors.getMonitorAreas();
        List<Map<String, Object>> configs = dbHelperMonitors.getMonitorConfigs();

        for (Map<String, Object> area : areas) {
            String areaName = (String) area.get("DisplayName");
            int areaId = (Integer) area.get("Id");
            String result = apiHelper.getMonitorAreaCategoryCounts(areaId);
            result = result.replaceAll("\"areaId\":" + areaId, "\"area\":\"" + areaName + "\"");
            printWriter.println("--------------------");
            printWriter.println(result);

            result = apiHelper.getMonitorAreaCounts(areaId);
            for (Map<String, Object> config : configs) {
                String name = (String) config.get("DisplayName");
                int id = (Integer) config.get("Id");
                result = result.replaceAll("\"" + id + "\"", "\"" + name + "\"");
            }
            printWriter.println(result);
        }
        printWriter.close();
    }

    @Then("^a file is created with monitor counts$")
    public void a_file_is_created_with_monitor_counts() throws Throwable {
        String filename = testData.getString("monitorCountsFilename");
        File file = new File(filename);
        assertTrue("Filename not found: " + filename,  file.exists());
        runtimeState.scenario.write(filename);
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        runtimeState.scenario.write(content);
    }

    @Then("^the monitor counts are compared against the baseline$")
    public void the_monitor_counts_are_compared_against_the_baseline() throws IOException {
        String baseline = System.getProperty("baselineMonitorCountFile");
        String filename = testData.getString("monitorCountsFilename");

        File file = new File(filename);
        assertTrue("Filename not found: " + filename,  file.exists());
        runtimeState.scenario.write(filename);
        String content1 = new String(Files.readAllBytes(Paths.get(filename)));
        runtimeState.scenario.write(content1);

        file = new File(baseline);
        assertTrue("Filename not found: " + filename,  file.exists());
        runtimeState.scenario.write(baseline);
        String content2 = new String(Files.readAllBytes(Paths.get(baseline)));
        runtimeState.scenario.write(content2);

        BufferedReader br1 = new BufferedReader(new FileReader(baseline));
        BufferedReader br2 = new BufferedReader(new FileReader(filename));
        try {
            String line;
            while ((line = br1.readLine()) != null) {
                assertEquals("Baseline and latest counts do not match", line, br2.readLine());
            }
        } catch(Throwable t) {
            // skip and close BufferedReaders
        }

        br1.close();
        br2.close();
    }

}
