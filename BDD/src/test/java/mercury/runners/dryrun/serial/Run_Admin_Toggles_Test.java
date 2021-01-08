package mercury.runners.dryrun.serial;

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
                ,"@admin_toggles" // ie. toggles updated via Admin web page
        },
        plugin = {"progress",
                "json:target/results/admin_toggles/cucumber.json"
        },
        dryRun = true
        )
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
public class Run_Admin_Toggles_Test {

    static {
        RunnerHelper.setUpProperties();
    }


    @ClassRule
    public static WebSuiteClassRule webSuiteClassRule = new WebSuiteClassRule();

}