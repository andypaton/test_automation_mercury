package mercury.helpers;

import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.PA55W0RD;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.api.models.portal.invoiceCreateEdit.DocumentUpload;
import mercury.api.models.portal.invoiceCreateEdit.InvoiceSubmission;
import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.apihelper.ApiHelperInvoices;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.runtime.RuntimeState;

@Component
public class InvoiceCreationHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private ApiHelperInvoices apiHelperInvoices;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private ApiHelper apiHelper;
    @Autowired private DbHelper dbHelper;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DocumentUpload documentUpload;
    @Autowired private RuntimeState runtimeState;
    @Autowired private DbHelperInvoices dbHelperInvoices;
    @Autowired private TestData testData;
    @Autowired private InvoiceSubmission invoiceSubmission;

    private DocumentUpload documentUploadBuilder(String verificationRequestToken, Integer orderReference, Integer jobReference) {
        documentUpload.setRequestVerificationToken(verificationRequestToken);
        documentUpload.setWorkOrderRef(orderReference.toString());
        documentUpload.setJobSheetFilePath("");
        documentUpload.setInvoiceFilePath("");
        documentUpload.setWorkOrderType("43");
        documentUpload.setJobRef(jobReference.toString());
        return documentUpload;
    }

    private DocumentUpload consolidatedInvoiceDocumentUploadBuilder(String verificationRequestToken) {
        documentUpload.setRequestVerificationToken(verificationRequestToken);
        documentUpload.setJobSheetFilePath("");
        documentUpload.setInvoiceFilePath("");
        return documentUpload;
    }

    public void addInvoiceDocument(Integer orderReference, Integer resourceId, Integer jobReference, String userProfile) throws Exception {
        String response, requestVerificationToken;
        String isConsolidated = "False";
        String accountsPayableSubmitted = "False";

        String resourceUserId = userProfile != null ? dbHelper.getApplicationUserIdForResourceId(resourceId, userProfile) : dbHelper.getApplicationUserIdForResourceId(resourceId);

        for (Map<String, Object> dbData : dbHelperTestAutomationUsers.getITUsers()) {
            String userName = dbData.get("UserName").toString();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                apiHelper.setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, resourceUserId);
                break;
            } catch (AssertionError e) {
                logger.debug("Login failed for " + userName + ". Trying next user ...");
            }
        }

        response = apiHelperInvoices.getUploadInvoiceDocument(orderReference, 0, isConsolidated, resourceId);
        requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
        testData.addStringTag("requestVerificationToken", requestVerificationToken);
        DocumentUpload documentUpload = documentUploadBuilder(requestVerificationToken, orderReference, jobReference);
        apiHelperInvoices.uploadInvoiceDocument(documentUpload, accountsPayableSubmitted, isConsolidated);
        runtimeState.scenario.write("The invoice has been uploaded for job reference: " + jobReference);
        String invoiceNumber = orderReference + "/" + RandomStringUtils.randomAlphanumeric(3);
        dbHelperInvoices.updateInvoiceDetails(orderReference, invoiceNumber);
        runtimeState.scenario.write("The invoice number is: " + invoiceNumber);
    }

    public void addConsolidatedInvoiceDocument(List<Integer> PPMScheduleRefs, Integer resourceId, String userProfile) throws Exception {
        String response, requestVerificationToken;
        String isConsolidated = "True";
        String accountsPayableSubmitted = "False";

        String resourceUserId = userProfile != null ? dbHelper.getApplicationUserIdForResourceId(resourceId, userProfile) : dbHelper.getApplicationUserIdForResourceId(resourceId);

        for (Map<String, Object> dbData : dbHelperTestAutomationUsers.getITUsers()) {
            String userName = dbData.get("UserName").toString();
            String password = userName.equalsIgnoreCase(runtimeState.domainUser) ? City2019 : PA55W0RD;
            try {
                apiHelper.setupImpersonationCookiesAppUser(propertyHelper.getMercuryUrl(), userName, password, resourceUserId);
                break;
            } catch (AssertionError e) {
                logger.debug("Login failed for " + userName + ". Trying next user ...");
            }
        }

        response = apiHelperInvoices.getUploadConsolidatedInvoiceDocument(0, isConsolidated, resourceId);
        requestVerificationToken = StringUtils.substringBetween(response, "__RequestVerificationToken\" type=\"hidden\" value=\"", "\"");
        testData.put("requestVerificationToken", requestVerificationToken);
        DocumentUpload documentUpload = consolidatedInvoiceDocumentUploadBuilder(requestVerificationToken);
        apiHelperInvoices.uploadConsolidatedInvoiceDocument(documentUpload, accountsPayableSubmitted, isConsolidated);

        String supplierId = dbHelperInvoices.getSupplierCode(resourceId);
        Integer invoiceHeaderId = dbHelperInvoices.getInvoiceId(supplierId);
        String invoiceNumber = invoiceHeaderId + "/" + RandomStringUtils.randomAlphanumeric(3);
        dbHelperInvoices.updateConsolidatedInvoiceDetails(supplierId, invoiceNumber, invoiceHeaderId, testData.getFloat("totalInvoiceAmount"));
        testData.put("invoiceNumber", invoiceNumber);
        testData.put("invoiceHeaderId", invoiceHeaderId);
        runtimeState.scenario.write("The consolidated invoice has been uploaded. ");
        runtimeState.scenario.write("The invoice number is: " + invoiceNumber);
    }

    public void deleteInvoiceDocuments(Integer orderReference) throws Exception {
        dbHelperInvoices.deleteInvoiceDocuments(orderReference);
        runtimeState.scenario.write("Deleting the invoice documents for Order Reference: " + orderReference);
    }

    private InvoiceSubmission invoiceSubmissionBuilder(String verificationRequestToken, String queried, Integer headerId) {
        invoiceSubmission.setRequestVerificationToken(verificationRequestToken);
        invoiceSubmission.setIsQueried(queried);
        invoiceSubmission.setInvoiceHeaderId(headerId.toString());
        return invoiceSubmission;
    }

    public void addLinesAndSubmitTheInvoice(Integer orderReference, Integer resourceId, Integer jobReference) throws Exception {
        // Add invoice lines
        apiHelperInvoices.addInvoiceLine(orderReference, testData.getString("requestVerificationToken"));
        List<Map<String, Object>> dbInvoiceLineData = dbHelperInvoices.getHeaderIdAndNetAmount(String.valueOf(orderReference));
        int headerId = Integer.valueOf(dbInvoiceLineData.get(0).get("HeaderId").toString());
        Float unitPrice = Float.valueOf(dbInvoiceLineData.get(0).get("UnitPrice").toString());
        dbHelperInvoices.updateInvoiceLineDetails(headerId, 2, "Test automation", 1, Math.round(unitPrice));

        // Submit the invoice
        InvoiceSubmission invoiceSubmission = invoiceSubmissionBuilder(testData.getString("requestVerificationToken"), "False", headerId);
        apiHelperInvoices.submitInvoice(headerId, orderReference, invoiceSubmission);
        runtimeState.scenario.write("The invoice has been submitted for job reference: " + jobReference);
    }

}
