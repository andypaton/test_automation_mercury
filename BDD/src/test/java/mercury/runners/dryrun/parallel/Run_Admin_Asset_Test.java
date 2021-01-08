package mercury.runners.dryrun.parallel;

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
                ,"@admin_assets"
        },
        plugin = {"progress",
                "json:target/results/admin_assets/cucumber.json"
        },
        dryRun = true
        )
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
public class Run_Admin_Asset_Test {

    static {
        RunnerHelper.setUpProperties();
    }


    @ClassRule
    public static WebSuiteClassRule webSuiteClassRule = new WebSuiteClassRule();

}