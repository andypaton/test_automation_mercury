package mercury.helpers;

import static mercury.helpers.Constants.City2019;
import static mercury.helpers.Constants.PA55W0RD;
import static mercury.helpers.Globalisation.LOCALE;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.models.ApplicationUser;
import mercury.databuilders.TestData;
import mercury.helpers.apihelper.ApiHelper;
import mercury.helpers.dbhelper.DbHelper;
import mercury.helpers.dbhelper.DbHelperResources;
import mercury.helpers.dbhelper.DbHelperTestAutomationUsers;
import mercury.runtime.RuntimeState;

public class ResourceHelper {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private DbHelper dbHelper;
    @Autowired private ApiHelper apiHelper;
    @Autowired private PropertyHelper propertyHelper;
    @Autowired private DbHelperTestAutomationUsers dbHelperTestAutomationUsers;
    @Autowired private DbHelperResources dbHelperResources;
    @Autowired private TestData testData;
    @Autowired private RuntimeState runtimeState;

    ApplicationUser appUser;

    public void getDocumentAsResource(String documentURL, String resourceId) throws Throwable {
        String resourceUserId = dbHelper.getApplicationUserIdForResourceId(Integer.valueOf(resourceId));

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

        apiHelper.getDocumentAsUser(documentURL);
    }

    public void deletePayrollCode() {
        if (LOCALE.equals("en-US")) {
            dbHelper.deleteFromSunTCode5Table(testData.getString("payrollCode"));
        } else {
            dbHelper.deleteFromSunTCode6Table(testData.getString("payrollCode"));
        }
    }

    public void deleteResourceProfile() {
        Integer resourceProfileId = dbHelperResources.getResourceProfileId(testData.getString("resourceProfileName"));
        dbHelper.deleteResourceProfileWorkingHoursAddedByAutoTest(resourceProfileId);
        dbHelper.deleteResourceProfileLaborRateAddedByAutoTest(resourceProfileId);
        dbHelper.deleteResourceProfileAddedByAutoTest(resourceProfileId);
    }

    public void deleteUser() {
        String userId = dbHelperResources.getUserId(testData.getString("username"));
        dbHelper.deleteFromUserImpersonationTable(userId);
        dbHelper.deleteApplicationUserAddedByAutoTestUser(testData.getString("username"));
    }

    public void deleteUserProfile() {
        Integer userProfileId = dbHelperResources.getUserProfileId(testData.getString("userProfileName"));
        dbHelper.deleteFromPermissionTable(userProfileId);
        dbHelper.deleteFromUserProfileToMonitorAreaTable(userProfileId);
        dbHelper.deleteApplicationUserAddedByAutoTestUserProfile(userProfileId);
        dbHelper.deleteUserProfileAddedByAutoTest(testData.getString("userProfileName"));
    }

    public void deleteCityResource() {
        Integer resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        dbHelper.deleteResourceCallerAddedByAutoTest(resourceId);
        dbHelper.deleteResourceEmailAddressAddedByAutoTest(resourceId);
        dbHelper.deleteResourcePhoneNumberAddedByAutoTest(resourceId);
        dbHelper.deleteApplicationUserAddedByAutoTest(resourceId);
        dbHelper.deleteResourceNotificationMethodAddedByAutoTest(resourceId);
        dbHelper.deleteResourceWorkingHoursAddedByAutoTest(resourceId);
        dbHelper.deleteResourceAddedByAutoTest(testData.getString("resourceName"));
    }

    public void deleteCityResourceWithSite() {
        Integer resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        dbHelper.deleteResourceCallerAddedByAutoTest(resourceId);
        dbHelper.deleteResourceEmailAddressAddedByAutoTest(resourceId);
        dbHelper.deleteResourcePhoneNumberAddedByAutoTest(resourceId);
        dbHelper.deleteApplicationUserAddedByAutoTest(resourceId);
        dbHelper.deleteResourceNotificationMethodAddedByAutoTest(resourceId);
        dbHelper.deleteFromSiteResourceTable(resourceId);
        dbHelper.deleteFromSiteResourceSyncTable(resourceId);
        dbHelper.deleteFromRotaEntryTable(resourceId);
        dbHelper.deleteResourceWorkingHoursAddedByAutoTest(resourceId);
        dbHelper.deleteResourceAddedByAutoTest(testData.getString("resourceName"));
    }

    public void deleteCityResourceWithJobAssigned() {
        Integer resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        Integer resourceAssignmentId = dbHelperResources.getResourceAssignmentId(resourceId);
        List<Integer> resourceAssignmentEventIds = null;
        if(resourceAssignmentId != null) {
            resourceAssignmentEventIds = dbHelperResources.getResourceAssignmentEventIds(resourceAssignmentId);
        }
        dbHelper.deleteResourceCallerAddedByAutoTest(resourceId);
        dbHelper.deleteResourceEmailAddressAddedByAutoTest(resourceId);
        dbHelper.deleteResourcePhoneNumberAddedByAutoTest(resourceId);
        dbHelper.deleteApplicationUserAddedByAutoTest(resourceId);
        dbHelper.deleteResourceNotificationMethodAddedByAutoTest(resourceId);
        dbHelper.deleteFromSiteResourceTable(resourceId);
        dbHelper.deleteFromSiteResourceSyncTable(resourceId);
        dbHelper.deleteFromRotaEntryTable(resourceId);
        dbHelper.deleteResourceWorkingHoursAddedByAutoTest(resourceId);
        if(resourceAssignmentEventIds != null) {
            for (int resourceAssignmentEventId : resourceAssignmentEventIds) {
                dbHelper.deleteResourceAssignmentStatusChangeAddedByAutoTest(resourceAssignmentEventId);
            }
        }
        if(resourceAssignmentId != null) {
            dbHelper.deleteResourceAssignmentEventAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteOriginatingResourceAssignmentAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteSiteVisitsAddedByAutoTest(resourceAssignmentId);
        }
        dbHelper.deleteResourceAssignmentAddedByAutoTest(resourceId);
        dbHelper.deleteResourceAddedByAutoTest(testData.getString("resourceName"));
    }

    public void deleteContractorResource() {
        Integer resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        Integer resourceAssignmentId = dbHelperResources.getResourceAssignmentId(resourceId);
        Integer resourceAssignmentEventId = null;
        if(resourceAssignmentId != null) {
            resourceAssignmentEventId = dbHelperResources.getResourceAssignmentEventId(resourceAssignmentId);
        }
        dbHelper.deleteResourceCallerAddedByAutoTest(resourceId);
        dbHelper.deleteResourceEmailAddressAddedByAutoTest(resourceId);
        dbHelper.deleteResourcePhoneNumberAddedByAutoTest(resourceId);
        dbHelper.deleteApplicationUserAddedByAutoTest(resourceId);
        dbHelper.deleteResourceNotificationMethodAddedByAutoTest(resourceId);
        dbHelper.deleteAssetClassificationSiteContractorMappingAddedByAutoTest(resourceId);
        if(resourceAssignmentEventId != null) {
            dbHelper.deleteResourceAssignmentStatusChangeAddedByAutoTest(resourceAssignmentEventId);
        }
        if(resourceAssignmentId != null) {
            dbHelper.deleteResourceAssignmentEventAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteAdditionalResourceRequirementAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteFundingRequestAddedByAutoTest(resourceAssignmentId);
        }
        dbHelper.deleteResourceAssignmentAddedByAutoTest(resourceId);
        dbHelper.deleteResourceAddedByAutoTest(testData.getString("resourceName"));
    }

    public void deleteContractorResourceWithJobAssigned() {
        Integer resourceId = dbHelperResources.getResourceId(testData.getString("resourceName"));
        Integer resourceAssignmentId = dbHelperResources.getResourceAssignmentId(resourceId);
        List<Integer> resourceAssignmentEventIds = null;
        if(resourceAssignmentId != null) {
            resourceAssignmentEventIds = dbHelperResources.getResourceAssignmentEventIds(resourceAssignmentId);
        }
        dbHelper.deleteResourceCallerAddedByAutoTest(resourceId);
        dbHelper.deleteResourceEmailAddressAddedByAutoTest(resourceId);
        dbHelper.deleteResourcePhoneNumberAddedByAutoTest(resourceId);
        dbHelper.deleteApplicationUserAddedByAutoTest(resourceId);
        dbHelper.deleteResourceNotificationMethodAddedByAutoTest(resourceId);
        dbHelper.deleteAssetClassificationSiteContractorMappingAddedByAutoTest(resourceId);
        if(resourceAssignmentEventIds != null) {
            for (int resourceAssignmentEventId : resourceAssignmentEventIds) {
                dbHelper.deleteResourceAssignmentStatusChangeAddedByAutoTest(resourceAssignmentEventId);
            }
        }
        if(resourceAssignmentId != null) {
            dbHelper.deleteResourceAssignmentEventAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteAdditionalResourceRequirementAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteOriginatingResourceAssignmentAddedByAutoTest(resourceAssignmentId);
            dbHelper.deleteFundingRequestAddedByAutoTest(resourceAssignmentId);
        }
        dbHelper.deleteResourceAssignmentAddedByAutoTest(resourceId);
        dbHelper.deleteResourceAddedByAutoTest(testData.getString("resourceName"));
    }

    public void deleteResource() {
        List<Integer> resourceIdList = dbHelperResources.getResourceIdsCreatedByAutoTest();
        if (!resourceIdList.isEmpty()) {
            String resourceIds = resourceIdList.toString();
            resourceIds = resourceIds.substring(1, resourceIds.length()-1);
            dbHelper.deleteResourceCallersAddedByAutoTest(resourceIds);
            dbHelper.deleteResourceEmailAddressesAddedByAutoTest(resourceIds);
            dbHelper.deleteResourcePhoneNumbersAddedByAutoTest(resourceIds);
            dbHelper.deleteApplicationUsersAddedByAutoTest(resourceIds);
            dbHelper.deleteResourceNotificationMethodsAddedByAutoTest(resourceIds);
            dbHelper.deleteAssetClassificationSiteContractorMappingAddedByAutoTest(resourceIds);
            dbHelper.deleteFromSiteResourceTable(resourceIds);
            dbHelper.deleteFromSiteResourceSyncTable(resourceIds);
            dbHelper.deleteFromRotaEntryTable(resourceIds);
            dbHelper.deleteResourceWorkingHoursAddedByAutoTest(resourceIds);

            List<Integer> resourceAssignmentIdList = dbHelperResources.getResourceAssignmentIdsCreatedByAutoTest(resourceIds);
            String resourceAssignmentIds = null;
            List<Integer> resourceAssignmentEventIdList = null;

            if (!resourceAssignmentIdList.isEmpty()) {
                resourceAssignmentIds = resourceAssignmentIdList.toString();
                resourceAssignmentIds = resourceAssignmentIds.substring(1, resourceAssignmentIds.length()-1);
                resourceAssignmentEventIdList = dbHelperResources.getResourceAssignmentEventIds(resourceAssignmentIds);
            }

            if (resourceAssignmentEventIdList != null) {
                String resourceAssignmentEventIds = resourceAssignmentEventIdList.toString();
                resourceAssignmentEventIds = resourceAssignmentEventIds.substring(1, resourceAssignmentEventIds.length()-1);
                dbHelper.deleteResourceAssignmentStatusChangeAddedByAutoTest(resourceAssignmentEventIds);
            }

            if (!resourceAssignmentIdList.isEmpty()) {
                resourceAssignmentIds = resourceAssignmentIdList.toString();
                resourceAssignmentIds = resourceAssignmentIds.substring(1, resourceAssignmentIds.length()-1);
                dbHelper.deleteResourceAssignmentEventsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteOriginatingResourceAssignmentsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteSiteVisitsAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteAdditionalResourceRequirementAddedByAutoTest(resourceAssignmentIds);
                dbHelper.deleteFundingRequestAddedByAutoTest(resourceAssignmentIds);
            }
            dbHelper.deleteResourceAssignmentAddedByAutoTest(resourceIds);
            dbHelper.deleteResourcesAddedByAutoTest();
        }
    }

    public void enterResourceWorkingHours() throws InterruptedException {
        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Monday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Monday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Monday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Monday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Tuesday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Tuesday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Tuesday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Tuesday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Wednesday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Wednesday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Wednesday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Wednesday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Thursday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Thursday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Thursday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Thursday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Friday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Friday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Friday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Friday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Saturday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Saturday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Saturday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Saturday", "17:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Sunday", 1)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursStartTime("Sunday", "09:00");
        }

        if (runtimeState.adminAddNewResourcePage.isWorkingHoursEmpty("Sunday", 2)) {
            runtimeState.adminAddNewResourcePage.enterWorkingHoursEndTime("Sunday", "17:00");
        }
    }

    public void answerResourceQuestions() throws Throwable {
        String[] yesNo = {"Yes", "No"};
        Random random = new Random();
        int randomIndex;

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Can this resource attach assets?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource attach assets?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Is this resource a VIP?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource a VIP?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Does this resource have an iPad?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this resource have an iPad?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Is this resource always chargeable?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is this resource always chargeable?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Can this resource be auto-assigned to reactive jobs?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Can this resource be auto-assigned to reactive jobs?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Is a reference required?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Is a reference required?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Does this Contractor use Technicians?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this Contractor use Technicians?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Invoicing and credit Notes active?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Invoicing and credit Notes active?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Does this resource receive update e-mails?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Does this resource receive update e-mails?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Visible For Parts Ordering?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Visible For Parts Ordering?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourcePage.isLabelDisplayed("Will this resource have Purchase Order Imports?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourcePage.answerResourceQuestions("Will this resource have Purchase Order Imports?", yesNo[randomIndex]);
        }
    }

    public void answerResourceProfileQuestions() throws Throwable {
        String[] yesNo = {"Yes", "No"};
        Random random = new Random();
        int randomIndex;

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Can this resource profile receive job callouts?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Can this resource profile receive job callouts?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Can this resource profile be allocated permanent sites?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Can this resource profile be allocated permanent sites?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Can this resource profile receive parts delivery?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Can this resource profile receive parts delivery?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Should this resource profile follow the planned reactive rota?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Should this resource profile follow the planned reactive rota?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Does this resource profile require a valid electrical certificate?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Does this resource profile require a valid electrical certificate?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Can this resource profile be automatically allocated to a job with outstanding parts via 'Additional Resource Required'(ARR) assigned parts?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Can this resource profile be automatically allocated to a job with outstanding parts via 'Additional Resource Required'(ARR) assigned parts?", yesNo[randomIndex]);
        }

        if(LOCALE.equals("en-US")) {
            if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Are labor rates stored by state?")) {
                randomIndex = random.nextInt(yesNo.length);
                runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Are labor rates stored by state?", yesNo[randomIndex]);
                if(yesNo[randomIndex].equalsIgnoreCase("Yes")) {
                    testData.put("labourRatesByState", true);
                } else {
                    testData.put("labourRatesByState", false);
                }
            }
        } else {
            if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Are labour rates stored by state?")) {
                randomIndex = random.nextInt(yesNo.length);
                runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Are labour rates stored by state?", yesNo[randomIndex]);
                if(yesNo[randomIndex].equalsIgnoreCase("Yes")) {
                    testData.put("labourRatesByState", true);
                } else {
                    testData.put("labourRatesByState", false);
                }
            }
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Will this resource profile receive portal daily actions notifications?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Will this resource profile receive portal daily actions notifications?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Should this resource profile be asked the Gas Safety Advice Notice question?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Should this resource profile be asked the Gas Safety Advice Notice question?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Should contact information be shown to Stores?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Should contact information be shown to Stores?", yesNo[randomIndex]);
        }

        if(runtimeState.adminAddNewResourceProfilePage.isLabelDisplayed("Will this resource profile receive emails when a job is logged against a site under litigation?")) {
            randomIndex = random.nextInt(yesNo.length);
            runtimeState.adminAddNewResourceProfilePage.answerResourceProfileQuestions("Will this resource profile receive emails when a job is logged against a site under litigation?", yesNo[randomIndex]);
        }
    }
}
