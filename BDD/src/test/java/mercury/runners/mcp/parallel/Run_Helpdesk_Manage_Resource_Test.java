package mercury.runners.mcp.parallel;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import cucumber.api.CucumberOptions;
import mercury.runners.CustomCucumber;
import mercury.runners.RunnerHelper;
import mercury.steps.WebSuiteClassRule;

@RunWith(CustomCucumber.class)
@CucumberOptions(
        features = "classpath:features",
        strict=true,
        glue = "mercury.steps",
        tags = {
                "~@wip"
                ,"@helpdesk_manage_resource"
        },
        plugin = {"progress",
                "json:target/results/helpdesk_manage_resource/cucumber.json"
        },
        dryRun = false
        )
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
public class Run_Helpdesk_Manage_Resource_Test {

    static {
        RunnerHelper.setUpProperties();
    }


    @ClassRule
    public static WebSuiteClassRule webSuiteClassRule = new WebSuiteClassRule();

}