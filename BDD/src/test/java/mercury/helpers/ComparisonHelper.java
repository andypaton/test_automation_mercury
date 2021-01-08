package mercury.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.GridHelper;

import org.springframework.stereotype.Component;

@Component
public class ComparisonHelper {

    /**
     * Compare data from a page grid and the database via assertions
     *
     * @param grid - Data from a grid on a web page, this is what the test is verifying
     * @param dbData - Data from the database, this is what the test should expect.
     * @param compareField - Field/Column to sort that data by before performing the compare.
     * @throws Exception
     */
    public void sortAndAssertDataInGridAndDatabase(Grid grid, List<Map<String, Object>> dbData, String compareField) throws Exception {
        assertEquals("Unexpected number of records", dbData.size(), grid.getRows().size());

        // Sorting the data in ascending order of resource
        List<LinkedHashMap<String, Object>> gridRows = GridHelper.getGridAsMapList(grid);
        Collections.sort(gridRows, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                return ((String) o1.get(compareField)).compareTo((String) o2.get(compareField));
            }
        });
        Collections.sort(dbData, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                return ((String) o1.get(compareField)).compareTo((String) o2.get(compareField));
            }
        });

        assertEquals("Unexpected data in the grid ", StringHelper.normalize(dbData), gridRows);
    }
}
