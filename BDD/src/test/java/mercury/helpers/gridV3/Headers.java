package mercury.helpers.gridV3;

import java.util.List;

public class Headers {

    public static List<String> headers;
    public static List<String> subHeaders;

    protected static List<String> headerDataFields;

    public List<String> getHeaders() {
        return headers;
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public List<String> getSubHeaders() {
        return subHeaders;
    }

    public void addSubHeader(String header) {
        subHeaders.add(header);
    }

    public List<String> getHeaderDataFields() {
        return headerDataFields;
    }

    public void addHeaderDataField(String headerDataField) {
        headerDataFields.add(headerDataField);
    }

}
