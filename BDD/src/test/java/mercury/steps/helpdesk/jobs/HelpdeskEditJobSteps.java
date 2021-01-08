package mercury.steps.helpdesk.jobs;

import static mercury.helpers.Globalisation.MEDIUM;
import static mercury.runtime.ThreadManager.getWebDriver;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.PendingException;
import cucumber.api.java.ContinueNextStepsOnException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mercury.database.dao.SiteAssetDao;
import mercury.database.models.SiteAsset;
import mercury.databuilders.CallerContact;
import mercury.databuilders.DataGenerator;
import mercury.databuilders.TestData;
import mercury.helpers.DateHelper;
import mercury.helpers.LogJobHelper;
import mercury.helpers.OutputHelper;
import mercury.helpers.gridV3.Grid;
import mercury.helpers.gridV3.Row;
import mercury.pageobject.web.helpdesk.jobs.ConfirmJobChangesModal;
import mercury.pageobject.web.helpdesk.resources.HelpdeskAddAdditionalResourcePanel;
import mercury.pageobject.web.helpdesk.resources.HelpdeskManageResourcesPanel;
import mercury.runtime.RuntimeState;

public class HelpdeskEditJobSteps {

    @Autowired private RuntimeState runtimeState;
    @Autowired private OutputHelper outputHelper;
    @Autowired private TestData testData;
    @Autowired private SiteAssetDao siteAssetDao;
    @Autowired private HelpdeskNewSiteContactModalSteps helpdeskNewSiteContactModalSteps;
    @Autowired private LogJobHelper logJobHelper;
    @Autowired HelpdeskLogAJobSteps helpdeskLogAJobSteps;

    private static final Logger logger = LogManager.getLogger();

    @When("^the reason for changes is entered in the summary pop up$")
    public void the_reason_for_changes_is_entered_in_the_summary_pop_up() throws Throwable {
        runtimeState.confirmJobChangesModal = new ConfirmJobChangesModal(getWebDriver()).get();
        String reason = DataGenerator.generateRandomSentence();
        testData.put("reason", reason);
        runtimeState.confirmJobChangesModal.enterReason(reason);
    }

    @When("^the changes are confirmed$")
    public void the_changes_are_confirmed() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobPage = runtimeState.confirmJobChangesModal.confirm();
    }

    @When("^an additional resource is added$")
    public void an_additional_resource_is_added() throws Throwable {
        runtimeState.helpdeskManageResourcesPanel = new HelpdeskManageResourcesPanel(getWebDriver()).get();
        if(runtimeState.helpdeskManageResourcesPanel.isOutstandingPOLabelDisplayed()) {
            runtimeState.helpdeskAddAdditionalResourcePanel.clickResourceProfileRadioButton();
            runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomResourceProfile();
            outputHelper.takeScreenshots();
        } else {
            runtimeState.helpdeskManageResourcesPanel.clickAddAdditionalResource();
            runtimeState.helpdeskAddAdditionalResourcePanel = new HelpdeskAddAdditionalResourcePanel(getWebDriver()).get();
            runtimeState.helpdeskAddAdditionalResourcePanel.selectRandomCreationReason();
            runtimeState.helpdeskAddAdditionalResourcePanel.sendAdditionalRequestDescription(DataGenerator.GenerateRandomString(7, 10, 3, 3, 0, 0));
            runtimeState.helpdeskAddAdditionalResourcePanel.searchAndSelectRandomConfiguredResource("Contractor");
            outputHelper.takeScreenshots();
            runtimeState.helpdeskManageResourcesPanel.save();
        }
    }

    @When("^the job contact is updated$")
    public void the_job_contact_is_updated() throws Throwable {
        List<String> contacts = runtimeState.helpdeskLogJobPage.getSiteContacts();
        runtimeState.scenario.write("Existing contacts: " + contacts.toString());
        int index = contacts.size() -1;
        String oldContact = runtimeState.helpdeskLogJobPage.getSiteContact();
        testData.put("contactName", oldContact);
        runtimeState.helpdeskLogJobPage.selectRandomSiteContact();
        String newContact = runtimeState.helpdeskLogJobPage.getSiteContact();
        int cnt = 0;
        while (oldContact.equalsIgnoreCase(newContact) && cnt < index) {
            runtimeState.helpdeskLogJobPage.selectRandomSiteContact();
            newContact = runtimeState.helpdeskLogJobPage.getSiteContact();
            cnt++;
        }
        outputHelper.takeScreenshots();
        runtimeState.scenario.write("New contact: " + newContact);
        testData.put("updatedContact", newContact);
    }

    @When("^the caller is changed$")
    public void the_caller_is_changed() throws Throwable {
        testData.put("originalCaller", runtimeState.helpdeskLogJobPage.getCaller());
        outputHelper.takeScreenshots();

        if (runtimeState.helpdeskLogJobPage.getCallers().size() > 1) {
            String updatedCaller = testData.getString("originalCaller");
            while (updatedCaller.equals(testData.getString("originalCaller"))) {
                updatedCaller = runtimeState.helpdeskLogJobPage.selectRandomCaller();
            }
        } else {
            CallerContact callerContact = new CallerContact.Builder().build();

            runtimeState.newCallerPage = runtimeState.helpdeskLogJobPage.clickAddNewCaller();
            runtimeState.newCallerPage.enterName(callerContact.getName());
            runtimeState.newCallerPage.enterJobRole(callerContact.getJobTitle());
            runtimeState.newCallerPage.enterDepartment(callerContact.getDepartment());
            runtimeState.newCallerPage.enterTelephone(callerContact.getTelephone());
            runtimeState.newCallerPage.enterExtension(callerContact.getExtension());

            outputHelper.takeScreenshots();
            runtimeState.helpdeskLogJobPage = runtimeState.newCallerPage.save();
        }

        testData.put("caller", runtimeState.helpdeskLogJobPage.getCaller());
    }

    @When("^Edit Caller is clicked$")
    public void edit_caller_is_clicked() throws Throwable {
        runtimeState.editCallerModal = runtimeState.helpdeskLogJobPage.selectEditCaller();
        testData.put("editCaller", true);
    }

    @When("^the caller is edited$")
    public void the_caller_is_edited() throws Throwable {
        runtimeState.editCallerModal = runtimeState.helpdeskLogJobPage.selectEditCaller();
        outputHelper.takeScreenshots();

        String originalJobRole = runtimeState.editCallerModal.getJobRole();
        testData.put("originalJobRole", originalJobRole);
        runtimeState.editCallerModal.enterJobRole("Dancer");
        testData.put("jobRole", runtimeState.editCallerModal.getJobRole());

        String originalDepartment = runtimeState.editCallerModal.getDepartment();
        testData.addStringTag("originalDepartment", originalDepartment);
        runtimeState.editCallerModal.enterDepartment(DataGenerator.generateRandomDepartment());
        testData.put("department", runtimeState.editCallerModal.getDepartment());

        String originalTelephone = runtimeState.editCallerModal.getTelephone();
        testData.put("originalTelephone",originalTelephone);
        runtimeState.editCallerModal.enterTelephone(DataGenerator.generatePhoneNumber());
        testData.put("telephone", runtimeState.editCallerModal.getTelephone());

        String originalExtension = runtimeState.editCallerModal.getExtension();
        testData.put("originalExtension", originalExtension);
        runtimeState.editCallerModal.enterExtension(String.valueOf(RandomUtils.nextInt(1000, 9999)));
        testData.put("extension", runtimeState.editCallerModal.getExtension());

        outputHelper.takeScreenshots();

        runtimeState.scenario.write("Updating Job Role from [" + originalJobRole + "] to [" + runtimeState.editCallerModal.getJobRole() + "]");
        runtimeState.scenario.write("Updating Department from [" + originalDepartment + "] to [" + runtimeState.editCallerModal.getDepartment() + "]");
        runtimeState.scenario.write("Updating Telephone from [" + originalTelephone + "] to [" + runtimeState.editCallerModal.getTelephone() + "]");
        runtimeState.scenario.write("Updating Extension from [" + originalExtension + "] to [" + runtimeState.editCallerModal.getExtension() + "]");
        runtimeState.editCallerModal.update();
    }

    @When("^the job contact is set to inactive$")
    public void the_job_contact_is_set_to_inactive() throws Throwable {
        String contactName = runtimeState.helpdeskLogJobPage.getSiteContact();
        testData.put("contactName",  contactName);
        runtimeState.helpdeskLogJobPage.setSiteContactInactive(contactName);
    }

    @When("^a job contact is added$")
    public void a_job_contact_is_added() throws Throwable {
        if (runtimeState.helpdeskLogJobPage.isAddJobContactButtonDisplayed()) {
            logJobHelper.jobWithNoPreviousSiteContact();
        } else {
            logJobHelper.jobWithPreviousSiteContact();
        }
    }

    @When("^Cancel changes are confirmed$")
    public void cancel_changes_are_confirmed() throws Throwable {
        outputHelper.takeScreenshots();
        runtimeState.helpdeskJobPage = runtimeState.cancelChangesAlert.confirm();
    }

    @ContinueNextStepsOnException
    @Then("^the job contact is not updated on the Contacts tab$")
    public void the_job_contact_is_not_updated() throws Throwable {
        runtimeState.helpdeskContactsTab = runtimeState.helpdeskJobPage.selectContactsTab();
        Grid grid = runtimeState.helpdeskContactsTab.getGrid();
        String contact = testData.getString("updatedContact").split("\\(")[0].trim();
        runtimeState.scenario.write("Asserting " + contact + " is not listed as one of the contacts");
        for (Row row : grid.getRows()) {
            assertNotEquals("Unexpected job contact: " + contact.replaceAll("\\W+", ""), row.getCell("Job Contact").getText().trim(), contact.replaceAll("\\W+", ""));
        }
    }

    @And("^the caller is shown in the caller field$")
    public void the_caller_is_shown_in_the_caller_field() throws Throwable {
        if (runtimeState.helpdeskLogJobPage.getCaller().equalsIgnoreCase("select")) {
            if (runtimeState.helpdeskLogJobPage.selectCaller(runtimeState.helpdeskLogJobPage.getSiteContact(0)).isEmpty()) {
                runtimeState.helpdeskLogJobPage.selectRandomCaller();
            }
        }
    }

    @ContinueNextStepsOnException
    @Then("^the job contact has been added to the Contacts tab$")
    public void the_job_contact_has_been_added_to_the_Contacts_grid() throws Exception {
        runtimeState.helpdeskContactsTab = runtimeState.helpdeskJobPage.selectContactsTab();
        Grid grid = runtimeState.helpdeskContactsTab.getGrid();
        String contact = testData.getString("contactName").split("\\(")[0].trim();
        runtimeState.scenario.write("Asserting " + contact + " is listed as one of the contacts");
        boolean found = false;
        for (Row row : grid.getRows()) {
            if (row.getCell("Job Contact").getText().trim().equals(contact)) {
                found = true;
            }
        }
        assertTrue("Contact not found: " + contact, found);
    }

    @ContinueNextStepsOnException
    @Then("^the job contact has been updated on the Contacts tab$")
    public void the_job_contact_has_been_updated_on_the_Contacts_grid() throws Exception {
        runtimeState.helpdeskContactsTab = runtimeState.helpdeskJobPage.selectContactsTab();
        Grid grid = runtimeState.helpdeskContactsTab.getGrid();
        String originalContact = testData.getString("contactName").split("\\(")[0].trim();
        String updatedContact = testData.getString("updatedContact").split("\\(")[0].trim();
        runtimeState.scenario.write("Asserting " + originalContact + " is not listed as one of the contacts");
        runtimeState.scenario.write("Asserting " + updatedContact + " is listed as one of the contacts");
        boolean foundOriginalContact = false;
        boolean foundUpdatedContact = false;
        for (Row row : grid.getRows()) {
            if (row.getCell("Job Contact").getText().trim().equals(originalContact)) {
                foundOriginalContact = true;
            }
            if (row.getCell("Job Contact").getText().trim().equals(updatedContact)) {
                foundUpdatedContact = true;
            }
        }
        assertFalse("Contact not updated from: " + originalContact, foundOriginalContact);
        assertTrue("Contact not updated to: " + updatedContact, foundUpdatedContact);
    }

    @ContinueNextStepsOnException
    @Then("^the job contact is displayed as inactive on the Contacts tab$")
    public void the_job_contact_is_displayed_as_inactive() throws Throwable {
        runtimeState.helpdeskContactsTab = runtimeState.helpdeskJobPage.selectContactsTab();
        Grid grid = runtimeState.helpdeskContactsTab.getGrid();
        String contact = testData.getString("contactName").split("\\(")[0].trim();
        runtimeState.scenario.write("Asserting " + contact + " is inactive");
        boolean found = false;
        for (Row row : grid.getRows()) {
            logger.debug("found Job Contact: " + row.getCell("Job Contact"));
            if (row.getCell("Job Contact").getText().trim().contains(contact)) {
                found = true;
                assertEquals("Expected " + contact + " Active = No", row.getCell("Active").getText().trim(), "No");
            }
        }
        assertTrue("Contact not found: " + contact, found);
    }

    @And("^the fault type is updated to a priority with \"([^\"]*)\" callout$")
    public void the_fault_type_is_updated_to_a_priority_with_priority(String callout) throws Throwable {
        int immediateCallout = "immediate".equalsIgnoreCase(callout) ? 1 : 0;
        SiteAsset assetSubtypeClassification = siteAssetDao.getAssetSubtypeClassification(immediateCallout, testData.getInt("siteId"));
        String subtypeClassification = assetSubtypeClassification.getAssetTypeName() + " > "
                + assetSubtypeClassification.getAssetSubTypeName();

        if(!assetSubtypeClassification.getAssetClassificationName().isEmpty()) {
            subtypeClassification = subtypeClassification + " > "
                    + assetSubtypeClassification.getAssetClassificationName();
        }
        String faultType = assetSubtypeClassification.getFaultTypeName();

        runtimeState.helpdeskLogJobPage.removeAsset(" ");
        runtimeState.scenario.write("Updating to subtypeClassification: " + subtypeClassification);
        runtimeState.helpdeskLogJobPage.addSubtypeClassification(subtypeClassification);
        runtimeState.scenario.write("Updating to fault: " + faultType);
        runtimeState.helpdeskLogJobPage.selectFault(faultType);

        if (runtimeState.helpdeskLogJobPage.isSameAsCallerButtonVisible()) {
            if (runtimeState.helpdeskLogJobPage.isButtonEnabled("Same as caller")) {
                runtimeState.helpdeskLogJobPage.clickSameAsCaller();
            }
        }
        LogJobHelper.answerJobQuestions(runtimeState, testData);
    }

    @ContinueNextStepsOnException
    @Then("^the Edit Job page is displayed$")
    public void the_Edit_Job_page_is_displayed() throws Throwable {
        assertTrue(runtimeState.helpdeskLogJobPage.getHeadline().contains("Edit Job"));
    }

    @And("^notes are entered on the page$")
    public void notes_are_entered_on_the_page() {
        String notes = "Test notes entered on " + DateHelper.dateAsString(new Date(), "dd MMM yyyy");
        testData.addStringTag("notes", notes);
        if (getWebDriver().getCurrentUrl().contains("logjob")) {
            runtimeState.addJobNotesModal.addJobNotes(notes);
        } else {
            runtimeState.helpdeskTimelineTab.addJobNotes(notes);
        }
    }

    @And("^the \"([^\"]*)\" counter is ((?:increased|decreased))$")
    public void count_of_item_is_updated(String item, String updated) throws Throwable {
        switch (item) {
        case "Notes":
            int countOfNotesInJob = runtimeState.addJobNotesModal.getBadgeCount();
            runtimeState.addJobNotesModal.clickCloseButton();
            int badgeCount = runtimeState.helpdeskLogJobPage.getAddNoteButtonBadgeNumber();
            runtimeState.scenario.write("Asserting count of notes is : " + countOfNotesInJob);
            assertEquals("Number displayed on Add Notes button is incorrect", badgeCount, countOfNotesInJob);
            break;

        case "Attachments":
            runtimeState.helpdeskAddAttachmentsModal.clickCloseButton();
            int attachmentsCount = new Integer(0);
            if (getWebDriver().getCurrentUrl().contains("Helpdesk#!/logjob/")) {
                attachmentsCount = runtimeState.helpdeskLogJobPage.getAttachmentButtonBadgeNumber();
            } else {
                attachmentsCount = runtimeState.helpdeskJobPage.getAttachmentCount();
            }

            if ("increased".equalsIgnoreCase(updated)) {
                runtimeState.scenario.write("Asserting count of attachments has increased to : " + String.valueOf(testData.getInt("attachmentsCount") + 1));
                assertEquals("Attachments Badgecount is incorrect", attachmentsCount, testData.getInt("attachmentsCount") + 1);
                break;
            } else {
                runtimeState.scenario.write("Asserting count of attachments has decreased to : " + String.valueOf(testData.getInt("attachmentsCount") - 1));
                assertEquals("Attachments Badgecount is incorrect", attachmentsCount, testData.getInt("attachmentsCount") - 1);
                break;
            }

        default:
            throw new Exception("Unexpected item: " + item);
        }

    }

    @And("^the private checkbox is clicked$")
    public void private_checkbox_is_clicked() {
        if (getWebDriver().getCurrentUrl().contains("logjob")) {
            runtimeState.addJobNotesModal.clickPrivateCheckbox();
        } else {
            runtimeState.helpdeskTimelineTab.clickPrivateCheckbox();
        }
    }

    @ContinueNextStepsOnException
    @And("^the file ((?:is|is not)) visible in the attachments grid$")
    public void the_file_will_be_visible_in_the_attachments_grid(String visible) throws Throwable {

        mercury.helpers.gridV3.Grid atachmentsGrid = runtimeState.helpdeskAddAttachmentsModal.getGrid();
        List<mercury.helpers.gridV3.Row> row = atachmentsGrid.getRows();
        if ("IS".equalsIgnoreCase(visible)) {

            String user = testData.getString("userName").toLowerCase();

            List<String> users = new ArrayList<>();
            users.add(user);

            assertEquals("Invalid File Name", testData.getString("fileName"), row.get(0).getCell("File Name").getText());
            assertEquals("Invalid File Type", testData.getString("fileType"), row.get(0).getCell("File Type").getText());
            assertTrue("Invalid User", users.contains(row.get(0).getCell("User").getText().toLowerCase()));

            Date dateExpected = DateHelper.stringAsDate(testData.getString("dateCreated").replace("HO - ", ""), MEDIUM);
            Date dateActual = DateHelper.stringAsDate(row.get(0).getCell("Date Created").getText().replace("HO - ", ""), MEDIUM);
            long diff = DateHelper.getTimeDifferenceBetweenTwoDatesInMinutes(dateActual, dateExpected);
            assertTrue("Actual Date Created: " +  dateActual.toString() + "\nto be after: " + dateExpected.toString() + "\nDifference (minutes): " + diff, diff >= 0);

        } else {
            if (row.size() > 0) {
                String fileData = testData.getString("fileName") + testData.getString("fileType")
                + testData.getString("loginUserName").toLowerCase()
                + testData.getString("dateCreated");

                List<String> dataFiles = new ArrayList<>();
                dataFiles.add(fileData);

                String rowData = row.get(0).getCell("File Name").getText() + row.get(0).getCell("File Type").getText()
                        + row.get(0).getCell("User").getText().toLowerCase()
                        + row.get(0).getCell("Date Created").getText();
                assertFalse("File data should not be visible in the grid", dataFiles.contains(rowData));
            }
        }
        outputHelper.takeScreenshots();
    }

    @And("^there are files visible in the attachments grid$")
    public void there_are_files_visible_in_the_attachments_grid() {
        mercury.helpers.gridV3.Grid atachmentsGrid = runtimeState.helpdeskAddAttachmentsModal.getGrid();
        List<mercury.helpers.gridV3.Row> row = atachmentsGrid.getRows();
        assertTrue("There are no attachments in grid", row.size() > 0);
    }

    @And("^a \"([^\"]*)\" file is chosen to upload$")
    public void file_is_chosen_to_be_uploaded(String docType) throws Throwable {
        String filename = new String();
        switch (docType) {
        case "Credit Note":
        case "Electrical Certificate":
        case "E-Mail":
        case "Fax":
        case "Gas Certificate":
        case "Job Sheet":
        case "Photo":
        case "Purchase Order":
        case "Supporting Documents":
            throw new PendingException("No Template File for " + docType);
            // TODO: add to this switch when we have document templates for the above

        case "Invalid":
            filename = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\InvalidFileType.txt";
            runtimeState.helpdeskAddAttachmentsModal.setAttachmentFileName(filename);
            testData.addStringTag("fileName", "InvalidFileType.txt");
            break;
        case "Invoice":
            filename = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf";
            runtimeState.helpdeskAddAttachmentsModal.setAttachmentFileName(filename);
            testData.addStringTag("fileName", "OCRinvoiceTemplate.pdf");
            break;
        case "Large":
            filename = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\largequote.pdf";
            runtimeState.helpdeskAddAttachmentsModal.setAttachmentFileName(filename);
            testData.addStringTag("fileName", "largequote.pdf");
            break;
        case "Quote":
            filename = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\smallquote.pdf";
            runtimeState.helpdeskAddAttachmentsModal.setAttachmentFileName(filename);
            testData.addStringTag("fileName", "smallquote.pdf");
            break;
        case "Single File":
            filename = System.getProperty("user.dir") + "\\src\\test\\resources\\portalfiles\\OCRinvoiceTemplate.pdf";
            runtimeState.helpdeskAddAttachmentsModal.setAttachmentFileName(filename);
            testData.addStringTag("fileName", "OCRinvoiceTemplate.pdf");
            break;
        default:
            throw new Exception("Unexpected file type: " + docType);
        }
    }

    @And("^the attachment type is set to \"([^\"]*)\"$")
    public void the_attachment_type_is_set_to(String fileType) throws Throwable {
        testData.addStringTag("fileType", fileType);
        runtimeState.helpdeskAddAttachmentsModal.selectAttachmentType(fileType);
    }

    @ContinueNextStepsOnException
    @Then("^user is able to make the changes to all available fields$")
    public void user_is_able_to_make_the_changes_to_all_available_fields() throws Throwable {

        assertTrue("Looks like Caller dropdown is not editable for the user", runtimeState.helpdeskLogJobPage.isCallerListBoxEnabled());
        assertTrue("Looks like Subtype/Classification dropdown is not editable for the user", runtimeState.helpdeskLogJobPage.isSubTypeClassificationListBoxEnabled());
        assertTrue("Looks like Location within site dropdown is not editable for the user", runtimeState.helpdeskLogJobPage.isLocationWithinSiteListBoxEnabled());
        assertTrue("Looks like Job Description edit box is not editable for the user", runtimeState.helpdeskLogJobPage.isJobDescriptionEditBoxEnabled());
        assertTrue("Looks like Fault Type dropdown is not editable for the user", runtimeState.helpdeskLogJobPage.isFaultTypeListBoxEnabled());
        assertTrue("Looks like Site Contact dropown is not editable for the user", runtimeState.helpdeskLogJobPage.isSiteContactListBoxEnabled());
    }

    @ContinueNextStepsOnException
    @Then("^the Deferral Question is not present$")
    public void the_Deferral_Question_is_not_present() throws Throwable {
        assertFalse("Expected Deferral Question is present on Job", runtimeState.helpdeskLogJobPage.isDeferralQuestionDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^the site contact is updated$")
    public void the_site_contact_is_updated() throws Throwable {

        String newCaller = testData.getString("siteContactName") + " ("+ testData.getString("siteContactDepartment") + ")";

        assertEquals("Looks like site contact name is not updated", newCaller, runtimeState.helpdeskLogJobPage.getStoredSiteContact(0));
        assertEquals("Looks like site contact job role is not updated", testData.getString("siteContactJobRole"), runtimeState.helpdeskLogJobPage.getSiteContactJobRole());
        assertEquals("Looks like site contact telephone is not updated", testData.getString("siteContactTelephone"), runtimeState.helpdeskLogJobPage.getSiteContactTelephone());
        assertTrue("Active checkbox is not clickable", runtimeState.helpdeskLogJobPage.isActiveCheckboxClickable(newCaller));
    }

    @ContinueNextStepsOnException
    @Then("^a confirmation summary pop up is displayed$")
    public void a_confirmation_summary_pop_up_is_displayed() throws Throwable {
        runtimeState.confirmJobChangesModal = new ConfirmJobChangesModal(getWebDriver()).get();
        assertTrue("Back button is not displayed", runtimeState.confirmJobChangesModal.isBackButtonDisplayed());
        assertTrue("Confirm button is not displayed", runtimeState.confirmJobChangesModal.isConfirmButtonDisplayed());
    }

    @ContinueNextStepsOnException
    @Then("^user can navigate back to the edit job page$")
    public void user_can_navigate_back_to_the_edit_job_page() throws Throwable {
        runtimeState.confirmJobChangesModal.back();
        assertTrue("Edit job page is not displayed", runtimeState.helpdeskLogJobPage.isPageLoaded());
    }

    @When("^a new site contact is added to the job and saved$")
    public void a_new_site_contact_is_added_to_the_job_and_saved() throws Throwable {
        helpdeskNewSiteContactModalSteps.user_adds_a_new_contact();
        helpdeskLogAJobSteps.the_job_is_saved();
        the_reason_for_changes_is_entered_in_the_summary_pop_up();
        the_changes_are_confirmed();
    }

    @ContinueNextStepsOnException
    @Then("^the new site contact is displayed on the Contacts tab$")
    public void the_new_site_contact_is_displayed_on_the_contacts_tab() throws Throwable {
        runtimeState.helpdeskContactsTab = runtimeState.helpdeskJobPage.selectContactsTab();
        Grid grid = runtimeState.helpdeskContactsTab.getGrid();
        String contact = testData.getString("siteContactName").trim();
        runtimeState.scenario.write(
                "Asserting " + contact
                + " is present in the Contacts tab");
        boolean found = false;
        for (Row row : grid.getRows()) {
            logger.debug("found Job Contact: " + row.getCell("Job Contact"));
            if (row.getCell("Job Contact").getText().trim().contains(contact)) {
                found = true;
                assertEquals("Unexpected phone number", row.getCell("Phone Number").getText().trim(), testData.getString("siteContactTelephone"));
            }
        }
        assertTrue("Contact not found: " + contact, found);
    }
}
