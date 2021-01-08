package mercury.helpers;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import mercury.api.models.site.SiteRequest;
import mercury.api.models.site.SiteResponse;
import mercury.helpers.apihelper.ApiHelperSites;
import mercury.helpers.dbhelper.DbHelper;
import mercury.runtime.RuntimeState;

public class SiteCreationHelper {

    @Autowired ApiHelperSites apiHelperSites;
    @Autowired DbHelper dbHelper;
    @Autowired PropertyHelper propertyHelper;
    @Autowired
    RuntimeState runtimeState;


    public SiteResponse createNewSite(String name) throws Exception {
        //        SiteRequest siteTemplate = getSiteTemplate("newSite.json");
        SiteRequest siteTemplate = getSiteTemplate("newSite1.json");
        siteTemplate.setName(name);
        siteTemplate.setSiteCode(name); // yep, being lazy : code = name
        SiteResponse site = apiHelperSites.createNewSite(siteTemplate);
        assertTrue("No SiteId created", site.getId() != null);
        return site;
    }

    /**
     * create new site named TestAutomation_xxxxxx (appended with random 6 digit integer)
     * @return
     * @throws Exception
     */
    public SiteResponse createNewSite() throws Exception {
        int randomCode = RandomUtils.nextInt(100000, 999999);
        return createNewSite("TestAutomation_" + String.valueOf(randomCode));
    }

    private SiteRequest getSiteTemplate(String filename) throws Exception {

        String pathname = "apiTemplates/sites/" + filename;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File file = new File(classLoader.getResource(pathname).getFile());

        if (!file.exists()) {
            throw new Exception("file not found: " + pathname);
        }

        String content = new String(Files.readAllBytes(file.toPath()));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper.readValue(content, SiteRequest.class);
    }

    public void addSiteToOrganisationStructure(int siteId, String siteName) throws ClientProtocolException, IOException {
        String url = propertyHelper.getMercuryUrl();
        String resourceType = url.contains("-uswm") || url.contains("-usad") ? "MST" : "RHVAC TECHNICIAN";
        int clusterId = dbHelper.getRandomOrgTechPositionId(resourceType);
        mercury.api.models.organisationStructure.get.OrganisationStructure osResponse = apiHelperSites.getOrgStructure(clusterId);

        mercury.api.models.organisationStructure.save.OrganisationStructure organisationStructure = osResponse.getPost();
        List<mercury.api.models.organisationStructure.save.OrganisationStructureSite> sites = organisationStructure.getSites();

        mercury.api.models.organisationStructure.save.OrganisationStructureSite newSite = new mercury.api.models.organisationStructure.save.OrganisationStructureSite();
        newSite.setId(siteId);
        newSite.setName(siteName);
        newSite.setMoveSiteToId(0);
        newSite.setIsNewSite(false);
        newSite.setIsActive(true);

        sites.add(newSite);
        organisationStructure.setSites(sites);

        runtimeState.scenario.write("Saving new org structure: " + organisationStructure.toString());
        apiHelperSites.saveOrgStructure(organisationStructure);
    }
}
