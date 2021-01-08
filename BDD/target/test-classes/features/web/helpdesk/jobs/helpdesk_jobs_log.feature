@helpdesk @helpdesk_jobs @helpdesk_jobs_log
Feature: Helpdesk - Jobs - Log a Job
 
  #Bug confirmed to be existing in live - Business team have agreed that they are happy for issue to exist
  @bugWalmart
  @ukrb @uswm
  Scenario: caller details autocompleted [bug: MCP-12556]
    Given a "Helpdesk Operator" has logged in
      And a search is run for an occupied site with an existing caller
      And the "Log a job" button is clicked
     When the existing Caller is entered
      And all other mandatory fields are entered
      And job is saved and edit job is selected
     Then the department, phone number, extension and caller type are auto completed
  
  @ukrb @uswm
  Scenario: fault priority autocompleted
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
     When a new job is being logged
     Then the fault priority auto completed based on site, asset subtype and fault type
     
  @ukrb @uswm
  Scenario: select site card
    Given a "Helpdesk Operator" has logged in
      And a search is run for a client caller with an "occupied" site
     When the site card is selected
     Then site details are displayed
      And the "Log a job" button is enabled
  
  @ukrb @uswm
  Scenario: identified caller - card details
    Given a "Helpdesk Operator" has logged in
      And a search is run for an occupied site with an existing caller
      And the "Log a job" button is clicked
     When the existing Caller is entered
      And all other mandatory fields are entered
      And job is saved and edit job is selected
     Then a caller card is displayed showing call duration
  ### how are active jobs calculated?
  #      And the caller card displays the number of active jobs
  
  @ukrb @uswm @sanity
  Scenario: log a job for a site - existing caller, same contact
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
      And the "Log a job" button is clicked
      And an existing Caller is entered
      And an asset, description and fault type are entered
      And the Job Contact is the same as caller     
      And the job is saved
     Then a new "Reactive" job is saved to the database
      And the Job Details page is displayed
  
  @ukrb @uswm
  Scenario: log a job for a caller - new caller, existing contact
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
      And a search is initiated for a caller
      And the "Add new caller" button is clicked
      And all mandatory fields for a new caller are entered
      And the New Caller form is saved
      And the site card is selected
      And the "Log a job" button is clicked
     When an asset, description and fault type are entered
      And the Job Contact is an existing contact
      And the job is saved
     Then a new "Reactive" job is saved to the database    
      And the Job Details page is displayed
  
  @ukrb @uswm @smoke
  Scenario: log a job for a site - existing caller, existing contact
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
      And a search is run for an existing caller
      And the caller is identified
      And the site card is selected
      And the "Log a job" button is clicked
     When caller, asset, location description, fault type and any resource or job questions are entered
      And the Job Contact is an existing contact    
      And the save button is clicked
     Then a new "Reactive" job is saved to the database
      And the Job Details page is displayed
      
  @bugRainbow
  @ukrb @uswm
  Scenario: log a job for a site - contact alternative phone number - invalid format [bug: MCP-8998]
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
     When the "Log a job" button is clicked
      And all mandatory fields are entered
      And an invalid alternative number is entered
     Then an error for invalid phone number format is displayed
      And the "Save" button is disabled
      
  @ukrb @uswm
  Scenario: save job before all mandatory fields are entered
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
     When the "Log a job" button is clicked
     Then the save button is disabled until all mandatory fields are entered
   
  @ukrb @uswm
  Scenario: display potential duplicate jobs
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" City Tech store with an existing caller
      And a job is logged
     When a duplicate job is being logged
     Then potential duplicate jobs are displayed with "Job Reference, Site Name, Created date, Status, AST/Class, Location, Fault Type, Description"
     
  @ukrb @uswm
  Scenario: duplicate job selection
    Given a "Helpdesk Operator" has logged in
     And a search is run for an "occupied" City Tech store with an existing caller
     And a job is logged
     And a duplicate job is being logged
    When a potential duplicate job is selected
    Then the draft log a job form tab remains open
     And the original job is opened on a new tab
     And the user is able to add a chase to the original job

  @ukrb @uswm
  Scenario: add new caller from log a job page
    Given a "Helpdesk Operator" has logged in
      And they are logging a new job
      And the "Add new caller" button is clicked
     When all mandatory fields for a new caller are entered
      And the New Caller form is saved
      And all other mandatory fields are entered
      And job is saved and edit job is selected
     Then the page is updated with pre-populated caller details
      And a caller card is displayed showing call duration
  
  @ukrb @uswm
  Scenario: add new caller from log a job page - invalid telephone number
    Given a "Helpdesk Operator" has logged in
      And they are logging a new job
      And the "Add new caller" button is clicked
     When all mandatory fields for a new caller are entered
      And an invalid telephone number is entered
     Then an error for invalid phone number format is displayed
      And the "Save and identify as caller" button is disabled
      
  @ukrb @uswm
  Scenario: add new caller from searchbox on site page
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
      And a search is initiated for a caller
      And the "Add new caller" button is clicked
     When all mandatory fields for a new caller are entered
      And the New Caller form is saved
     Then the page is updated with pre-populated caller details
      And a caller card is displayed showing call duration
      
  @ukrb @uswm
  Scenario: add new caller from searchbox on site page - invalid telephone number
    Given a "Helpdesk Operator" has logged in
      And a search is run for an "occupied" site
      And a search is initiated for a caller
      And the "Add new caller" button is clicked
     When all mandatory fields for a new caller are entered
      And an invalid telephone number is entered
     Then an error for invalid phone number format is displayed
      And the "Save and identify as caller" button is disabled
  
  @ukrb @uswm
  Scenario: new caller from log a job page - mandatory fields
    Given a "Helpdesk Operator" has logged in
      And they are logging a new job
     When user selects Add New Caller
     Then the following New Caller fields are mandatory "Name, Job Role, Department, Telephone"
      And the following New Caller field is optional "Extension"
  
  @ukrb @uswm
  Scenario: new caller from searchbox on site page - mandatory fields
    Given a "Helpdesk Operator" has logged in
     When a search is run for an "occupied" site
      And a search is initiated for a caller
      And the "Add new caller" button is clicked
     Then the following New Caller fields are mandatory "Name, Job Role, Department, Telephone"
      And the following New Caller field is optional "Extension" 
      
  @uswm @ukrb
  Scenario Outline: custom questions - storm damage - caused by storm damage is "<answer>" 
    Given the configuration for job question: "Has this issue been caused by Storm Damage?" is checked
      And a "Helpdesk Operator" has logged in
      And a search is run for a "Occupied" site with configured job question: "Has this issue been caused by Storm Damage?"
      And a new job is being logged
     When user selects "<answer>" answer for the job question: "Has this issue been caused by Storm Damage?"
      And the save button is clicked
     Then "Questions" tab in the newly created job displays the "Has this issue been caused by Storm Damage?" question along with user selected "<answer>" answer
    Examples: 
      | answer | 
      | Yes    | 
      | No     | 
       
  @grid @uswm
  Scenario: Logging a job which forces an incident Subtype - Total Refrigeration Outage, Fault Type - Non Operational
    Given the Linked Incident Criteria is set
      And a "Helpdesk Operator" has logged in
     When a reactive job is logged for a fault with matching linked incident criterion
     Then the Linked Incident modal is displayed with text "This job needs to be linked to an incident of the same type." and "Please select an existing incident or create a new linked incident."
      And the details of all the logged incidents for the site for the last 30 days are displayed in the Linked Incident Modal
      And the close button in the Linked Incidents modal is disabled
      
  @uswm
  Scenario: Logging a job which forces an incident Subtype - Link an Incident
    Given the Linked Incident Criteria is set
      And a "Helpdesk Operator" has logged in
      And a reactive job is logged for a fault with matching linked incident criterion for a site having logged incidents in last 30 days
     When the "Link" button is clicked
     Then the close button in the Linked Incidents modal is enabled
  
  @uswm
  Scenario: Logging a job which forces an incident Subtype - Linked incident for job
    Given the Linked Incident Criteria is set
      And a "Helpdesk Operator" has logged in
      And a reactive job is logged for a fault with matching linked incident criterion for a site having logged incidents in last 30 days
      And the top incident is linked to the job
     When the "Linked Incidents" button is clicked
     Then the Linked incident for job panel is displayed with existing logged incidents in last 30 days
      And the "Unlink" button is displayed beside the linked incident
      
  @uswm
  Scenario: Logging a job which forces an incident Subtype - Select Create New Incident action
    Given the Linked Incident Criteria is set
      And a "Helpdesk Operator" has logged in
      And a reactive job is logged for a fault with matching linked incident criterion
     When the "Create New Incident" button is clicked
     Then the log an incident page is displayed where description is prepopulated and is same as job description
      And the incident Resource Caller value is prepopulated and is same as job caller
      And the Incident Type "Refrigeration Outage" is pre-selected
  
  @uswm
  Scenario: Logging a job which forces an incident Subtype - Logging an incident
    Given the Linked Incident Criteria is set
      And a "Helpdesk Operator" has logged in
      And a reactive job is logged for a fault with matching linked incident criterion
      And the "Create New Incident" button is clicked
      And a new incident is logged
     When the "Linked Jobs" button is clicked
     Then the job is displayed in the incidents linked jobs modal
      And the job remains open in another tab