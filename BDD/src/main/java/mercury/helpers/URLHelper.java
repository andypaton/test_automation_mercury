package mercury.helpers;

import java.net.URI;
import java.net.URISyntaxException;

public class URLHelper {
    
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain;
    }

}
