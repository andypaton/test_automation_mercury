package mercury.helpers.asserter.dbassertions;

import static org.junit.Assert.assertNotNull;

import mercury.database.dao.PartsRequestSummaryDao;
import mercury.database.models.PartsRequestSummary;
import mercury.helpers.asserter.common.AssertTask;

public class AssertPartsRequestSummary  implements AssertTask {

    private PartsRequestSummaryDao partsRequestSummaryDao;

    private String failureMessage;
    private Integer jobReference;
    private String supplierCode;
    private String partNumber;
    private String manfRef;
    private String model;
    private String partDescription;
    private String serialNumber;
    private Float unitPrice;
    private Integer quantity;
    private Boolean newPart;

    public AssertPartsRequestSummary(PartsRequestSummaryDao partsRequestSummaryDao, Integer jobReference, String supplierCode, String partNumber, String manfRef, String model,
            String partDescription, String serialNumber, Float unitPrice, Integer quantity, Boolean newPart) {
        this.partsRequestSummaryDao = partsRequestSummaryDao;
        this.jobReference =  jobReference;
        this.supplierCode =supplierCode;
        this.partNumber =partNumber;
        this.manfRef = manfRef;
        this.model = model;
        this.partDescription = partDescription;
        this.serialNumber = serialNumber;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.newPart = newPart;
    }

    @Override
    public boolean execute() {
        PartsRequestSummary partRequesSummary;
        try {
            partRequesSummary = partsRequestSummaryDao.getPartRequestSummary(
                    jobReference,
                    supplierCode,
                    partNumber,
                    manfRef,
                    model,
                    partDescription,
                    serialNumber,
                    unitPrice,
                    quantity,
                    newPart);

            assertNotNull("Unexpected null recordset: could not find Part Request summary", partRequesSummary);

        } catch (Throwable t) {
            failureMessage = t.getMessage();
            return false;
        }

        return true;
    }

    @Override
    public String getTaskName() {
        return this.getClass().getName();
    }

    @Override
    public String getTaskFailureMessage() {
        return failureMessage;
    }
}
