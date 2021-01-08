package mercury.helpers.apihelper;

import static mercury.helpers.Globalisation.localize;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.api.models.portal.invoiceCreateEdit.DocumentUpload;
import mercury.api.models.portal.invoiceCreateEdit.InvoiceSubmission;
import mercury.helpers.JsonHelper;
import mercury.helpers.dbhelper.DbHelperInvoices;

@Component
public class ApiHelperInvoices extends ApiHelper {

    @Autowired private DbHelperInvoices dbHelperInvoices;

    private static final Logger logger = LogManager.getLogger();

    private static final String INVOICE_FILTER_URL = "?workOrderRef=%s";
    private static final String INVOICE_HEADER_FILTER_URL = INVOICE_FILTER_URL + "&invoiceHeaderId=%s";
    private static final String INVOICE_ACCOUNTS_PAYABLE_FILTER_URL = "?accountsPayableSubmitted=%s";

    private static final String INVOICE_SUPPLIER_URL = "/Portal/Supplier";
    private static final String INVOICE_UPLOAD_INVOICE_JOBSHEET_URL = INVOICE_SUPPLIER_URL + "/UploadInvoiceAndJobSheetDocument";
    private static final String SUPPLIER_INVOICE_CREATE_EDIT_URL = INVOICE_UPLOAD_INVOICE_JOBSHEET_URL + INVOICE_HEADER_FILTER_URL + "&isConsolidated=%s";
    private static final String SUPPLIER_INVOICE_UPLOAD_URL = INVOICE_UPLOAD_INVOICE_JOBSHEET_URL + INVOICE_ACCOUNTS_PAYABLE_FILTER_URL + "&isConsolidatedInvoice=%s";
    private static final String SUPPLIER_CONSOLIDATED_INVOICE_CREATE_EDIT_URL = INVOICE_UPLOAD_INVOICE_JOBSHEET_URL + "?invoiceHeaderId=%s&isConsolidated=%s";

    private static final String INVOICE_LINE_TYPE_PAYLOAD = "[{\"id\":%d,\"description\":\"%s\",\"partCode\":\"%s\",\"forSupplier\":%s,\"forSupplyOnly\":%s,\"nominalCode\":\"%s\",\"isMandatory\":%s,\"isLabor\":%s}]";

    private static final String CREATE_INVOICE_LINE_URL = "/Portal/Supplier/CreateInvoiceLine";
    private static final String SUBMIT_INVOICE_URL = "/Portal/Supplier/SubmitInvoice?invoiceHeaderId=%d&workOrderRef=%d";

    public void updateLineType(Map<String, Object> dbData, String activeFlag, String lineType) {
        String url = propertyHelper.getMercuryUrl() + "/api/invoiceLineType/update";
        String jsonPayload = null;

        try {
            int id = Integer.valueOf(dbData.get("Id").toString());
            String partCode = dbData.get("PartCode").toString();
            String forSupplier = dbData.get("Supplier").toString();
            String forSupplyOnly = dbData.get("SupplyOnly").toString();
            String nominalCode = dbData.get("NominalCode").toString();
            String isMandatory = dbData.get("IsMandatory").toString();
            String isLabor = dbData.get("IsLabor").toString();
            String[] laborTypes = {dbHelperInvoices.getLabourLineType(), "Travel", "Overtime"};

            boolean description = dbData.get("Description").equals(localize(lineType));
            boolean typeIsMandatory = dbData.get("IsMandatory").equals(true);
            boolean typeIsLabor = dbData.get("IsLabor").equals(true);
            boolean lineTypeEqualsLaborType = Arrays.stream(laborTypes).anyMatch(localize(lineType)::equals);

            if ((description && !lineTypeEqualsLaborType && !typeIsMandatory) || (description && lineTypeEqualsLaborType && !typeIsMandatory)) {
                jsonPayload = String.format(INVOICE_LINE_TYPE_PAYLOAD, id, localize(lineType), partCode, forSupplier, forSupplyOnly, nominalCode, activeFlag, isLabor);
            } else if (description && lineTypeEqualsLaborType && typeIsMandatory && !typeIsLabor) {
                jsonPayload = String.format(INVOICE_LINE_TYPE_PAYLOAD, id, localize(lineType), partCode, forSupplier, forSupplyOnly, nominalCode, isMandatory, activeFlag);
            }

            runtimeState.scenario.write("Resetting line type : " + jsonPayload);
            restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
            int statusCode = restService.getStatusCode();
            logger.debug("Invoice line type response status code: " + statusCode);
            assertTrue("Expected status code 200 but got: " + statusCode, statusCode == 200);

        } catch (Exception e) {
            logger.debug("Ignored exception thrown while attempting to update line type: " + localize(lineType));
        }
    }

    public String getUploadInvoiceDocument(Integer orderReference, Integer invoiceHeaderId, String isConsolidated, Integer resourceId) throws Exception {
        String url = propertyHelper.getMercuryUrl() + SUPPLIER_INVOICE_CREATE_EDIT_URL;
        url = String.format(url, orderReference, invoiceHeaderId, isConsolidated);

        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("getUploadInvoiceDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public String getUploadConsolidatedInvoiceDocument(Integer invoiceHeaderId, String isConsolidated, Integer resourceId) throws Exception {
        String url = propertyHelper.getMercuryUrl() + SUPPLIER_CONSOLIDATED_INVOICE_CREATE_EDIT_URL;
        url = String.format(url, invoiceHeaderId, isConsolidated);

        String response = restService.sendGetRequest(url, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode() , restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length()) ));
            logger.debug("getUploadInvoiceDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public Map<String, Object> createFileMap(String fieldNames, String fileNamesWithPath, String fileNames) {
        Map<String, Object> FileMap = new HashMap<String, Object>();
        FileMap.put("fieldNames", fieldNames);
        FileMap.put("fileNamesWithPath", fileNamesWithPath);
        FileMap.put("fileNames", fileNames);
        return FileMap;
    }

    public String uploadInvoiceDocument(DocumentUpload documentUpload, String accountsPayableSubmitted, String isConsolidated) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUPPLIER_INVOICE_UPLOAD_URL;
        url = String.format(url, accountsPayableSubmitted, isConsolidated);

        Map<String, Object> formDataMap = JsonHelper.toMap(documentUpload.toJsonString());

        List<Map<String, Object>> fileData = new ArrayList<Map<String, Object>>();
        fileData.add(createFileMap("InvoiceFileUpload", System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf", "smallquote-portrait.pdf"));
        fileData.add(createFileMap("JobSheetFileUpload", System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf", "smallquote-portrait.pdf"));

        String response = restService.sendPostFilesRequestWithRedirection(url, formDataMap, fileData, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode(), restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length())));
            logger.debug("UploadInvoiceDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public String uploadConsolidatedInvoiceDocument(DocumentUpload documentUpload, String accountsPayableSubmitted, String isConsolidated) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + SUPPLIER_INVOICE_UPLOAD_URL;
        url = String.format(url, accountsPayableSubmitted, isConsolidated);

        Map<String, Object> formDataMap = JsonHelper.toMap(documentUpload.toJsonString());

        List<Map<String, Object>> fileData = new ArrayList<Map<String, Object>>();
        fileData.add(createFileMap("InvoiceFileUpload", System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote-portrait.pdf", "smallquote-portrait.pdf"));

        String response = restService.sendPostFilesRequestWithRedirection(url, formDataMap, fileData, appUserCookieStore);

        try {
            assertTrue("Expected status code 200, but was " + restService.getStatusCode(), restService.getStatusCode() == 200);
        } catch (AssertionError e) {
            logger.debug("response " + response.substring(0, Math.min(500, response.length())));
            logger.debug("UploadConsolidatedInvoiceDocument Response code " + restService.getStatusCode());
            throw e;
        }
        return response;
    }

    public String addInvoiceLine(Integer orderRef, String requestVerificationToken) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl() + CREATE_INVOICE_LINE_URL;
        String payload = "[{\"id\":%d,\"WorkOrderRef\":\"%s\",\"HeaderId\":\"%s\",\"InvoiceLineTypeId\":%s,\"PartNumber\":%s,\"Description\":\"%s\",\"Quantity\":%d,\"UnitPrice\":%d,\"UnitTaxAmount\":%d }]";

        payload = String.format(payload, 0, orderRef, 0, 1, 0, "test desc", 1, 10, 0);
        String response = restService.sendPostRequest(url, payload, appUserCookieStore);
        return response;
    }

    public String submitInvoice(Integer headerId, Integer orderRef, InvoiceSubmission invoiceSubmission) throws Exception {

        String url = propertyHelper.getMercuryUrl() + SUBMIT_INVOICE_URL;
        url = String.format(url, headerId, orderRef);

        String formData =  JsonHelper.convertJsonToHttpQueryString(propertyHelper.getJsonHelperURL(), invoiceSubmission.toJsonString());
        logger.debug("Form data to submit the invoice " + formData);
        String response = restService.sendPostRequestWithRedirection(url, formData,  appUserCookieStore);
        return response;
    }

}
