package mercury.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;
import mercury.helpers.dbhelper.DbHelper;

@Component
public class AssetHelper {


    @Autowired private DbHelper dbHelper;
    @Autowired private TestData testData;

    public void deleteAsset() {
        int assetId = dbHelper.getAssetId(testData.getString("assetTag"));
        dbHelper.deleteFromAssetNoteTable(assetId);
        dbHelper.deleteFromAssetTimelineEventTable(assetId);
        dbHelper.deleteFromAssetTable(testData.getString("assetTag"));
    }

}
