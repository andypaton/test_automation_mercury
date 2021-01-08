package mercury.runners;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import cucumber.api.CucumberOptions;
import mercury.steps.WebSuiteClassRule;

@RunWith(CustomCucumber.class)
@CucumberOptions(
        features = "classpath:features",
        strict=false,
        glue = "mercury.steps",
        tags = {
                "~@wip",
                "~@datasetup",
                "~@maintenance",
                "@andy"
        },
        plugin = {"pretty",
                "html:target/reports/test-report",
                "json:target/results/cucumber.json",
                "junit:target/results/cucumber.xml"
        },
        dryRun = false,
        monochrome = true
        )
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
public class RunCukeTest {

    static {
        RunnerHelper.setUpProperties();
    }


    @ClassRule
    public static WebSuiteClassRule webSuiteClassRule = new WebSuiteClassRule();

}