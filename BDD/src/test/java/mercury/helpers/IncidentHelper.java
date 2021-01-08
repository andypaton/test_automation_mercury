package mercury.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.helpers.gridV3.Row;
import mercury.runtime.RuntimeState;

@Component
public class IncidentHelper {

    @Autowired private RuntimeState runtimeState;

    public void outputIncidentTimelineRow(Row row) throws Exception {
        runtimeState.scenario.write("Checking Row " + row.getCell("Description") + " :" + row.getCell("User") + " :" + row.getCell("Time") + " :" + row.getCell("Type"));
    }
}
