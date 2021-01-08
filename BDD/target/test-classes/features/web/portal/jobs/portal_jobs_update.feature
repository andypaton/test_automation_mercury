@portal @portal_jobs @portal_jobs_update
@geolocation
Feature: Portal - Jobs - Update

  @mcp
  Scenario: Incomplete Update a Remote Job with Status of Complete - Validate error messages
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the user updates the job
     Then the Travel time tooltip will be displayed
      And the Status on Departure tooltip will be displayed
  
  @mcp
  Scenario: Update a Remote Job with Status of Complete and Verify Quote Form is visible
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "Complete" Status on Departure is entered
      And a Quote is Requested for the Job    
     Then the Quote Request form is displayed
      And the Quote Request form is populated correctly
      
  # [bug: MCP-13490] removed steps that are not in UAT spreadsheet
  @mcp
  @smoke1
  Scenario: Update a Remote Job with Status of Complete
        * using dataset "portal_jobs_update_001"
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "remote" job form details are updated with Complete status on departure
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the updated job "is not" displayed on the Jobs for Site page
      And the Job is updated with a "Fixed" status
      And the Resource has been marked as "Complete" for the job
#      And the Message table has been updated with "Complete" ###################### removing steps that are not in UAT spreadsheet
      And the Site Visit has been recorded
      And the JobTimelineEvent table has been updated with "Job Completed"

  # [bug: MCP-13490] removed steps that are not in UAT spreadsheet
  @mcp
  Scenario: Update a Non Remote Job with Status of Complete
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "non remote" job form details are updated with Complete status on departure
      And the user updates the job
     Then the Job is updated with a "Fixed" status
      And the Resource has been marked as "Complete" for the job
#      And the Message table has been updated with "Complete" ###################### removing steps that are not in UAT spreadsheet
      And the Site Visit has been recorded
      And the JobTimelineEvent table has been updated with "Job Completed"
  
  @uswm @ukrb @usah
  Scenario Outline: Update a Non Remote Job with Status of Complete and Request a "<fundingRoute>" Quote
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "non remote" job form details are updated with Complete status on departure
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "Fixed" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the new quote job is created
      And the JobTimelineEvent table has been updated with "Job Completed"
      And the JobTimelineEvent table has been updated with "Job linked to Quote Job"
    Examples: 
      | fundingRoute |
      | CAPEX        |
      | OPEX         |
      | OOC          |
   
  # Created separate scenario as Linked quote jobs are not created in Advocate.
  # Instead, the job type will change from Reactive to Quote.  
  @usad
  Scenario Outline: Update a Non Remote Job with Status of Complete and Request a "<fundingRoute>" Quote
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "non remote" job form details are updated with Complete status on departure
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "Awaiting Quote Request Review" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Timeline Event Summary has been updated with "Job Type changed to Quote"
      And the Timeline Event Summary has been updated with "Quote Request Raised"
    Examples: 
      | fundingRoute |
      | CAPEX        |
      | OPEX         |
      
  # scenario not valid for UKRB
  @uswm
  Scenario Outline: Update a Non Remote Job with Status of Complete and Request a "<fundingRoute>" Quote
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the "non remote" job form details are updated with Complete status on departure
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "<fundingRoute>" funding route is requested
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "Fixed" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the new quote job is created
      And the JobTimelineEvent table has been updated with "Job Completed"
      And the JobTimelineEvent table has been updated with "Job linked to Quote Job"
    Examples: 
      | fundingRoute |
      | BMI          |

  # [bug: MCP-13490] removed steps that are not in UAT spreadsheet
  @mcp
  @smoke1
  Scenario: Update a Remote Job with Status of Returning
        * using dataset "portal_jobs_update_001"
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the remote job form details are updated with Returning status on departure
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "In Progress" status
      And the updated job "is" displayed on the Jobs for Site page
      And the Resource Assignment table has been updated with the status "Returning"
#      And the Message table has been updated with "ETACreated" ###################### removing steps that are not in UAT spreadsheet
#      And the Message table has been updated with "Returning" ###################### removing steps that are not in UAT spreadsheet
      And the Site Visit has been recorded
      And the Timeline Event Summary has been updated with "Resource returning"

  # Splitting the next scenario into two as Business Team confirmed that we don't send emails to sites in Rainbow
  # [bug: MCP-13490] removed steps that are not in UAT spreadsheet
  @uswm
  Scenario: Update a Remote Job with Status of Awaiting Parts
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the remote job form details are updated with Awaiting Parts status on departure
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "In Progress" status
      And the updated job "is" displayed on the Jobs for Site page
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
#      And the Message table has been updated with "ETACreated" ###################### removing steps that are not in UAT spreadsheet
#      And the Message table has been updated with "ResourceAwaitingParts" ###################### removing steps that are not in UAT spreadsheet
      And the Site Visit has been recorded
      And the Timeline Event Summary has been updated with "Resource Awaiting Parts"
      And an email is sent for "Awaiting Parts Notification"
  
  @ukrb @usad
  Scenario: Update a Remote Job with Status of Awaiting Parts
    Given a portal user with a "Contractor Technician" profile and with "In Progress / On Site" Jobs
      And is within GEO radius
     When the user logs in    
      And the "Stop Work" button is clicked
      And the remote job form details are updated with Awaiting Parts status on departure
      And the user updates the job
     Then the Jobs for Site page is displayed
      And the Job is updated with a "In Progress" status
      And the updated job "is" displayed on the Jobs for Site page
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the Site Visit has been recorded
      And the Timeline Event Summary has been updated with "Resource Awaiting Parts"