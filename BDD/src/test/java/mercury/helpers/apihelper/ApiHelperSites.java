package mercury.helpers.apihelper;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import mercury.api.models.site.SiteRequest;
import mercury.api.models.site.SiteResponse;

@Component
public class ApiHelperSites extends ApiHelper{

    private static final Logger logger = LogManager.getLogger();


    public SiteResponse createNewSite(SiteRequest request) throws ClientProtocolException, IOException, InterruptedException {

        String url = propertyHelper.getMercuryUrl() + "/site/api/create";

        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(request);

        logger.debug("createNewSite request: " + jsonPayload);

        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);

        assertTrue("Unexpected status returned from createNewSite request", restService.getStatusCode() == 200);

        SiteResponse response = mapper.readValue(restService.getResponseString(), SiteResponse.class);
        logger.debug("createNewSite response: " + restService.getResponseString());

        return response;
    }

    public mercury.api.models.organisationStructure.get.OrganisationStructure getOrgStructure(int id) throws ClientProtocolException, IOException {

        String url = propertyHelper.getMercuryUrl() + "/FieldManagement/api/OrganisationStructure/Details/%d";

        restService.sendGetRequest(String.format(url, id), mercuryCookie);
        assertTrue(restService.getStatusCode() == 200);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(restService.getResponseString(), mercury.api.models.organisationStructure.get.OrganisationStructure.class);
    }

    public void saveOrgStructure(mercury.api.models.organisationStructure.save.OrganisationStructure organisationStructure) throws ClientProtocolException, IOException {

        String url = propertyHelper.getMercuryUrl() + "/FieldManagement/api/OrganisationStructure/Save";

        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(organisationStructure);

        restService.sendPostJsonRequest(url, jsonPayload, mercuryCookie);
        assertTrue(restService.getStatusCode() == 200);
    }
}