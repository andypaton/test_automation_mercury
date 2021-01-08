@portal @portal_jobs @portal_jobs_feedback
Feature: Portal - Jobs - Feedback
  
  @uswm @ukrb @usah     
  Scenario: Verify as a RFM Feedback page displays Grid headers, search box and Split RFMs button
     Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
     When the "Feedback" sub menu is selected from the "Jobs" top menu
     Then the "Combined Feedback" table on the "Feedback" page displays correctly
      And a search box is present on the "Feedback" page
      And "Split RFMs" button is displayed on the "Feedback" page
      And the "Combined Feedback" table can be sorted on all columns

  @uswm @ukrb @usah
  Scenario Outline: View Feedback Response page - "<RESOURCE>"
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
     When the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     Then the feedback response page is displayed with job details
      And the feedback response page is displayed with feedback details
      And the "Job Timeline" table on the "Feedback Response" page displays correctly
    Examples:
      | RESOURCE            |
      | RFM                 |
      | Divisional Manager  |
      
      
  @uswm
  Scenario: View Feedback Response page - Operations Director
    Given a "Operations Director" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
     When the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     Then the feedback response page is displayed with job details
      And the feedback response page is displayed with feedback details
      And the "Job Timeline" table on the "Feedback Response" page displays correctly
        
  @uswm @ukrb @usah               
  Scenario: RFM selects Reply to Store via App in feedback response - Send button is disabled when no response text is entered
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     When the "Reply to Store" button is clicked
     Then the "Send" button in the "Reply to Store" modal is disabled
     
  @uswm @ukrb @usah
  Scenario: RFM selects Reply to Store via App in feedback response
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     When the "Reply to Store" button is clicked
      And the feedback response is given through the Reply to Store action
     Then the "RFM response to Store Feedback" event details are displayed in the Job Timeline table
      And the "Spoke to Store" button in the "Feedback Response" page is disabled
      And a tick symbol is displayed on the "Reply to Store" button
     
  @uswm @ukrb @usah
  Scenario: RFM selects Spoke to Store in feedback response - Send button is disabled when no response text is entered
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     When the "Spoke to Store" button is clicked
     Then the "Send" button in the "Spoke to Store" modal is disabled
     
  @uswm @ukrb @usah
  Scenario: RFM selects Spoke to Store in feedback response
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
     When the "Spoke to Store" button is clicked
      And the feedback response is given through the Spoke to Store action
     Then the "RFM response to Store Feedback" event details are displayed in the Job Timeline table
      And the "Reply to Store" button in the "Feedback Response" page is disabled
      And a tick symbol is displayed on the "Spoke to Store" button
      
  @uswm @ukrb @usah
  Scenario: RFM selects Reply to Store via App in feedback response - Count in the feedback table is reduced
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
      And the "Reply to Store" button is clicked
      And the feedback response is given through the Reply to Store action
     When the "Back" button is clicked
     Then the items count in the "Combined Feedback" table is reduced by "1"
      And the job reference is not in the Combined Feedback table
      
  @uswm @ukrb @usah     
  Scenario: RFM selects Spoke to Store in feedback response - Count in the feedback table is reduced
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
      And the job awaiting feedback response in the "Combined Feedback" table is searched for and opened
      And the "Spoke to Store" button is clicked
      And the feedback response is given through the Spoke to Store action
     When the "Back" button is clicked
     Then the items count in the "Combined Feedback" table is reduced by "1"
      And the job reference is not in the Combined Feedback table
     
  @uswm @ukrb @usah               
  Scenario: Verify as a RFM Split RFMs button functionality in the Feedback page
    Given a "RFM" with a job Awaiting Feedback Response
      And the user logs in
      And the "Feedback" sub menu is selected from the "Jobs" top menu
     When the "Split RFMs" button is clicked
     Then the Combined Feedback table is split according to the RFMs names whose absence is covered by the user
     