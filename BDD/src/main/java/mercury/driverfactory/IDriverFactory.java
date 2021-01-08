package mercury.driverfactory;

import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;

public interface IDriverFactory {
	WebDriver getDriver(DriverConfiguration configuration) throws Exception, IOException, ParseException;

	void DestroyWebDriver();
}
