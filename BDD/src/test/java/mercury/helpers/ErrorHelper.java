package mercury.helpers;

import static mercury.helpers.Globalisation.CURRENCY_SYMBOL;
import static mercury.helpers.Globalisation.LOCALE;
import static mercury.helpers.Globalisation.TAX_RATE;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mercury.databuilders.TestData;

@Component
public class ErrorHelper {

    @Autowired private TestData testData;
    @Autowired private PropertyHelper propertyHelper;

    private static final String US_PHONE_FORMAT = "Phone numbers have to be in following format: (123)-123-1234 or 123-123-1234 or 1234-123-1234";
    private static final String UK_PHONE_FORMAT = "Phone numbers must have 10 or more digits";

    /**
     * Create the correct expected error message, if no error message found then return what was passed to the method.
     * @param message
     * @return
     */
    public String generateErrorMessage(String message) {
        String mercuryUrl = propertyHelper.getMercuryUrl();
        String value = mercuryUrl.contains("ukrb") ? "vat" : "tax";

        switch(message) {
        case "The tax amount can't be more than the allowed % of the credit net amount":
            message = "The " + value + " amount " + CURRENCY_SYMBOL + testData.getString("taxAmount") + " can't be more than " + new BigDecimal(TAX_RATE).setScale(0) + "% of the credit net amount " + CURRENCY_SYMBOL + testData.getString("netAmount");
            return message;

        case "The unit tax amount can't be more than the allowed % of the unit price":
            message = "The unit " + value + " " + testData.getString("unitTaxAmount") + " for part [" + testData.getString("orderPartNumber") + " - " + testData.getString("orderDescription") + "] can't be more than " + new BigDecimal(TAX_RATE).setScale(0) + "% of the unit price " + testData.getString("partUnitPrice");
            return message;

        case "The invoice tax amount can't be more than the allowed % of the invoice net value":
            message = "The invoice " + value + " amount " + CURRENCY_SYMBOL + testData.getString("invoiceTaxAmount") + " can't be more than " + new BigDecimal(TAX_RATE).setScale(0) + "% of the invoice net value " + CURRENCY_SYMBOL + testData.getString("orderValue");
            return message;

        case "The invoice net value can't be greater than order value":
            String orderValue = testData.getString("orderValue");
            Double val1 = Double.valueOf(orderValue) + 0.05; //When saving the invoice details, the system adds 5p leeway to the order value for supply only parts orders - MCP20978
            Double val2 = Double.valueOf(orderValue);
            Double orderVal = testData.getString("profileName").equalsIgnoreCase("Supply Only") ? val1 : val2; 
            message = "The invoice net value " + CURRENCY_SYMBOL + testData.getString("invoiceNetValue") + " can't be greater than " + CURRENCY_SYMBOL + orderVal;
            return message;

        case "Invalid phone number format":
            message = LOCALE.equals("en-US") ? US_PHONE_FORMAT : UK_PHONE_FORMAT;
            return message;

        case "The unit tax should not be more than the allowed % of the unit net value":
            message = "The unit " + value + " should not be more than " + new BigDecimal(TAX_RATE).setScale(0) + "% of the unit net value";
            return message;

        case "Quantity can not be greater than the quantity available":
            message = "Quantity can not be greater than the quantity available to invoice which is " + testData.getInt("toInvoiceQuantity");
            return message;

        case "The total tax value of the lines should equal the tax value of the credit note header":
            message = "The total " + value + " value of the lines should equal the " + value + " value of the credit note header";
            return message;

        default:
            return message;
        }
    }

}
