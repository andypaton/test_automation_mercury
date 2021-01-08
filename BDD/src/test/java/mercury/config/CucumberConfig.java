package mercury.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import mercury.api.models.resource.Resource;
import mercury.database.dao.AdditionalApproverRuleDao;
import mercury.database.dao.GasApplianceTypeDao;
import mercury.database.dao.GasQuestionDao;
import mercury.database.dao.HelpdeskFaultDao;
import mercury.database.dao.InitialApproverRuleDao;
import mercury.database.dao.JobTimelineEventDao;
import mercury.database.dao.JobViewDao;
import mercury.database.dao.MessageDao;
import mercury.database.dao.PartCodeDao;
import mercury.database.dao.PartsRequestSummaryDao;
import mercury.database.dao.ProjectQuoteSummaryDao;
import mercury.database.dao.QuoteApprovalScenariosDao;
import mercury.database.dao.QuoteDao;
import mercury.database.dao.QuoteLineDao;
import mercury.database.dao.QuotePriorityDao;
import mercury.database.dao.ResourceAssignmentDao;
import mercury.database.dao.ResourceDao;
import mercury.database.dao.SiteVisitCylinderDetailsDao;
import mercury.database.dao.SiteVisitGasDetailsDao;
import mercury.database.dao.SiteVisitsDao;
import mercury.database.dao.UserJobDao;
import mercury.database.models.BrandWorkingHours;
import mercury.database.models.JobView;
import mercury.database.models.QuoteApprovalScenarios;
import mercury.database.models.Reason;
import mercury.database.models.ResourceWorkingHours;
import mercury.database.models.SiteContractorAsset;
import mercury.database.models.SiteView;
import mercury.databuilders.CallerContact;
import mercury.databuilders.NewJob;
import mercury.databuilders.TestData;
import mercury.databuilders.TestDataListener;
import mercury.databuilders.Toggles;
import mercury.databuilders.UpdateJob;
import mercury.databuilders.UpdateJobBuilder;
import mercury.databuilders.User;
import mercury.helpers.AzureStorageHelper;
import mercury.helpers.DbUtilityHelper;
import mercury.helpers.DownloadHelper;
import mercury.helpers.EmailHelper;
import mercury.helpers.JobCreationHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.PropertyHelper;
import mercury.helpers.QuoteCreationHelper;
import mercury.helpers.ResourceHelper;
import mercury.helpers.SiteCreationHelper;
import mercury.helpers.ToggleHelper;
import mercury.helpers.TzHelper;
import mercury.helpers.asserter.common.AssertionFactory;
import mercury.helpers.dbhelper.DbHelperAssertions;
import mercury.helpers.pdfhelper.PdfHelper;
import mercury.helpers.pdfhelper.PdfHelperOCRInvoice;
import mercury.rest.RestAssuredHelper;
import mercury.runtime.RuntimeState;
import mercury.spring.ApplicationContextProvider;
import mercury.telemetry.AppInsightConfig;

@Configuration
@ComponentScan(basePackages={"mercury"})
public class CucumberConfig {

    @Bean
    RuntimeState runtimeState() {
        return new RuntimeState();
    }

    @Bean
    OutputHelper outputHelper() {
        return new OutputHelper();
    }

    @Bean
    PropertyHelper propertyHelper() {
        return new PropertyHelper();
    }

    @Bean
    CallerContact callerContact() {
        return new CallerContact();
    }

    @Bean
    SiteView siteView() {
        return new SiteView();
    }

    @Bean
    NewJob job() {
        return new NewJob();
    }

    @Bean
    UpdateJob updateJob() {
        return new UpdateJob();
    }

    @Bean
    User user() {
        return new User();
    }

    @Bean
    BrandWorkingHours brandWorkingHours() {
        return new BrandWorkingHours();
    }

    @Bean
    ResourceWorkingHours resourceWorkingHours() {
        return new ResourceWorkingHours();
    }

    @Bean
    SiteContractorAsset siteContractorAsset() {
        return new SiteContractorAsset();
    }

    @Bean
    AssertionFactory assertionFactory() {
        return new AssertionFactory();
    }

    @Bean
    SiteVisitsDao siteVisitsDao() {
        return new SiteVisitsDao();
    }

    @Bean
    MessageDao messageDao() {
        return new MessageDao();
    }

    @Bean
    QuoteDao quoteDao() {
        return new QuoteDao();
    }

    @Bean
    ResourceDao resourceDao() {
        return new ResourceDao();
    }

    @Bean
    JobTimelineEventDao jobTimelineEventDao() {
        return new JobTimelineEventDao();
    }

    @Bean
    JobViewDao jobViewDao() {
        return new JobViewDao();
    }

    @Bean
    TestData testData() {
        return new TestData();
    }

    @Bean
    TestDataListener testDataListener() {
        return new TestDataListener();
    }

    @Bean
    Toggles toggles() {
        return new Toggles();
    }

    @Bean
    Resource resource() {
        return new Resource();
    }

    @Bean
    PartCodeDao partCodeDao() {
        return new PartCodeDao();
    }
    @Bean
    UserJobDao userJobDao() {
        return new UserJobDao();
    }

    @Bean
    PartsRequestSummaryDao partsRequestSummaryDao() {
        return new PartsRequestSummaryDao();
    }

    @Bean
    ProjectQuoteSummaryDao projectQuoteSummaryDao() {
        return new ProjectQuoteSummaryDao();
    }

    @Bean
    QuoteLineDao quoteLineDao() {
        return new QuoteLineDao();
    }

    @Bean
    JobCreationHelper jobCreationHelper() {
        return new JobCreationHelper();
    }

    @Bean
    QuoteCreationHelper quoteCreationHelper() {
        return new QuoteCreationHelper();
    }
    @Bean
    SiteCreationHelper siteCreationHelper() {
        return new SiteCreationHelper();
    }

    @Bean
    Reason reason() {
        return new Reason();
    }

    @Bean
    QuotePriorityDao quotePriorityDao() {
        return new QuotePriorityDao();
    }

    @Bean
    TzHelper tzHelper() {
        return new TzHelper();
    }

    @Bean
    QuoteApprovalScenariosDao quoteApprovalScenariosDao() {
        return new QuoteApprovalScenariosDao();
    }

    @Bean
    QuoteApprovalScenarios quoteApprovalScenarios() {
        return new QuoteApprovalScenarios();
    }

    @Bean
    JobView jobView() {
        return new JobView();
    }

    @Bean
    DbHelperAssertions dbHelperAssertions() {
        return new DbHelperAssertions();
    }

    @Bean
    ResourceAssignmentDao resourceAssignmentDao() {
        return new ResourceAssignmentDao();
    }

    @Bean
    HelpdeskFaultDao helpdeskFaultDao() {
        return new HelpdeskFaultDao();
    }

    @Bean
    AdditionalApproverRuleDao additionalApproverRuleDao() {
        return new AdditionalApproverRuleDao();

    }

    @Bean
    InitialApproverRuleDao initialApproverRuleDao() {
        return new InitialApproverRuleDao();

    }

    @Bean
    DbUtilityHelper dbUtilityHelper() {
        return new DbUtilityHelper();
    }

    @Bean
    ToggleHelper toggleHelper() {
        return new ToggleHelper();
    }

    @Bean
    ResourceHelper resourceHelper() {
        return new ResourceHelper();
    }

    @Bean
    GasQuestionDao gasQuestionDao() {
        return new GasQuestionDao();
    }

    @Bean
    SiteVisitGasDetailsDao siteVisitGasDetailsDao() {
        return new SiteVisitGasDetailsDao();
    }

    @Bean
    SiteVisitCylinderDetailsDao siteVisitCylinderDetailsDao() {
        return new SiteVisitCylinderDetailsDao();
    }

    @Bean
    GasApplianceTypeDao gasApplianceTypeDao() {
        return new GasApplianceTypeDao();
    }

    @Bean
    EmailHelper emailHelper() {
        return new EmailHelper();
    }

    @Bean
    PdfHelper pdfHelper() {
        return new PdfHelper();
    }

    @Bean
    PdfHelperOCRInvoice pdfHelperOCRInvoice() {
        return new PdfHelperOCRInvoice();
    }

    @Bean
    AppInsightConfig appInsightConfig() {
        return new AppInsightConfig();
    }

    @Bean
    ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    UpdateJobBuilder updateJobBuilder() {
        return new UpdateJobBuilder();
    }

    @Bean
    DownloadHelper downloadHelper() {
        return new DownloadHelper();
    }

    @Bean
    AzureStorageHelper azureStorageHelper() {
        return new AzureStorageHelper();
    }

    @Bean
    RestAssuredHelper restAssuredHelper() {
        return new RestAssuredHelper();
    }

}
