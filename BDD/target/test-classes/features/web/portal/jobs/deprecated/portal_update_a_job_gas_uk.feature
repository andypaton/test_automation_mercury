@portal @notsignedoff @portal_fgas
@toggles @RefrigerantGas @USRegulations @UKRegulations
@wip @deprecated
@mcp
Feature: Portal - Jobs - Update with Gas usage - UK Regulations

  Background: System Feature Toggles are set for UK Gas Regulations
    Given the system feature toggle "RefrigerantGas" is "enabled"
      And the system sub feature toggle "US Regulations" is "disabled"
      And the system sub feature toggle "UK Regulations" is "enabled"
  
  #@MCP-5702
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question             | Answer | 
      | Refrigerant Gas Used | Yes    | 
      | Gas Type             | Other  | 
  #    | Gas Leakage Code | I - Pack – HT  High side |
      And the leak test repair has been checked
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet      | 
      | City Resource | reactive | No      | Yes       | Fixed  | In Plant with Asset | 
  
  #@MCP-5702
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with with Gas in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with out Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question             | Answer | 
      | Refrigerant Gas Used | Yes    | 
      | Gas Type             | Other  | 
      And the leak test repair has been checked
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet         | 
      | City Resource | reactive | No      | Yes       | Fixed  | In Plant without Asset | 
  
  #@MCP-5702
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question             | Answer | 
      | Refrigerant Gas Used | Yes    | 
      And the leak test repair has been checked
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet          | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant with Asset | 
  
  #@MCP-5702 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question                 | Answer | 
      | Refrigerant Gas Used     | Yes    | 
      | Was Gas Added to a Rack? | No     | 
      And the leak test repair has been checked
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet             | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant without Asset | 
  
  #@MCP-5702
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question                 | Answer     | 
      | Refrigerant Gas Used     | Yes        | 
      | Was Gas Added to a Rack? | UnAnswered | 
      | Gas Type                 | UnAnswered | 
      | Gas Leakage Code         | UnAnswered |
      | Gas Leakage Check Method | UnAnswered |
      | Gas Primary Component        | UnAnswered |
      | Action                   | UnAnswered |
      | Fault Code               | UnAnswered |
      | Bottle Number            | UnAnswered |
      | Quantity                 | UnAnswered |
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the Refrigerant Gas errors are displayed
      | Question             | Answer | 
      | Refrigerant Gas Used | Yes    | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet             | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant without Asset | 
  
  #@MCP-5702 
  @wip
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant is selected
     Then the Refrigerant Gas questions are answered
      | Question                 | Answer | 
      | Refrigerant Gas Used     | Yes    | 
      | Was Gas Added to a Rack? | No     | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet        | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant, no Rack | 

  #@MCP-5702 
  @wip 
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And is within GEO radius
      And the user logs in    
      And the "Stop Work" button is clicked  
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question                 | Answer | 
      | Refrigerant Gas Used     | Yes    | 
      | Was Gas Added to a Rack? | No     | 
      And the leak test repair has been checked
      And the update Job form is completed with status on departure "Awaiting Parts"
#     Then the "Portal" "Job" Update Saved page is displayed
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet             | 
#      | Contractor Technician | reactive | No      | Yes       | Fixed  |  Not in Plant without Asset |
      | Contractor Technician | reactive | No      | Yes       | In Progress  |  Not in Plant, no Rack |           
      
  #@MCP-5702 
  @wip
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with out Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with out Asset
      And is within GEO radius
      And the user logs in    
      And the "Stop Work" button is clicked  
      And the Update Job form is complete with basic information
     When the Refrigerant Gas questions are answered
      | Question                 | Answer | 
      | Refrigerant Gas Used     | Yes    | 
      | Was Gas Added to a Rack? | No     | 
      And the leak test repair has been checked
      And the "Complete" Status on Departure is entered
      And the Asset Condition is entered
      And the Root Cause Category is entered
      And the Root Cause is entered
      And the Notes are entered
      And the Root Cause Description is entered
      And a Quote is Requested for the Job
      And the Scope of Work is entered
      And a non-urgent quote with a "OPEX" funding route is requested
      And the user updates the job
#     Then the "Portal" "Job" Update Saved page is displayed
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet             | 
#      | Contractor Technician | reactive | No      | Yes       | Fixed  |  Not in Plant without Asset |
      | Contractor Technician | reactive | No      | Yes       | In Progress  |  Not in Plant, no Rack |    
      