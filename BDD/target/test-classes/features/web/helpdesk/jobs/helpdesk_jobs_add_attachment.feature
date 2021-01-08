@helpdesk @helpdesk_jobs @helpdesk_jobs_add_attachment
@mcp
Feature: Helpdesk - Jobs - Add Attachment

  @grid @smoke
  Scenario: Add an attachment to a job via edit
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
     When the "Edit" action is selected
      And the "Attachment" button is clicked
      And a "Invoice" file is chosen to upload
      And the attachment type is set to "Invoice"
      And the "Attach" button is clicked
     Then a "Thank you, the file has been uploaded." alert is displayed
      And the file is visible in the attachments grid
      And the "Attachments" counter is increased

  Scenario: Add an attachment to a job via Job View
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
      And the "Attachment" button is clicked
     When a "Invoice" file is chosen to upload
      And the attachment type is set to "Invoice"
      And the "Attach" button is clicked
     Then a "Thank you, the file has been uploaded." alert is displayed
      And the file is visible in the attachments grid
      And the "Attachments" counter is increased
      
  Scenario: Add an attachment over 4MB in size
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
      And the "Attachment" button is clicked
     When a "Large" file is chosen to upload
      And the attachment type is set to "Invoice"
      And the "Attach" button is clicked
     Then a "The file size limit is 4 MB" alert is displayed
      And the file is not visible in the attachments grid
      
  Scenario: Add an attachment with an invalid file type
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
      And the "Attachment" button is clicked
     When a "Invalid" file is chosen to upload      
      And the attachment type is set to "Invoice"
      And the "Attach" button is clicked
     Then a "You are only able to upload the following file types: doc,docx,pdf,jpg,jpeg,gif,png,msg,xls,xlsx" alert is displayed
      And the file is not visible in the attachments grid
      
  Scenario: Add an attachment with no attachment type chosen
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
      And the "Attachment" button is clicked
     When a "Invoice" file is chosen to upload
      And the "Attach" button is clicked
     Then a "Please select a file type" alert is displayed
      And the file is not visible in the attachments grid
      
  Scenario: Adding an attachment when no file chosen
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job
      And the "Attachment" button is clicked
     When the attachment type is set to "Invoice" 
      And the "Attach" button is clicked
     Then a "Please select a file" alert is displayed
      And the file is not visible in the attachments grid
      
  Scenario: Deleting an attachment from the grid
    Given a "Helpdesk Operator" has logged in 
      And a search is run for a logged job with attachments
      And the "Attachment" button is clicked
     When the "Delete" button is clicked
     Then the file is not visible in the attachments grid
      And the "Attachments" counter is decreased

         