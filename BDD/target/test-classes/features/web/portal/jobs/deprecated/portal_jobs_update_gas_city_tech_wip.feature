@portal @notsignedoff @wip @portal_fgas
@toggles @RefrigerantGas
@mcp
Feature: WIP - Portal - Jobs - Update a gas job as a City tech

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"

  #@MCP-5610 
  @wip @rework
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
  #     When the Refrigerant Gas questions are answered
  #      | Question                               | Answer | 
  #      # | Refrigerant Gas used during this visit | Yes    | 
  #      | Gas Type                               | Other  | 
     When the Gas "Leak Check Questions" sub section questions are answered with
      | Question | Answer | 
  # | Refrigerant Gas used during this visit | Yes    | 
      And the leak test repair has been checked
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Gas sub section answers have been recorded
      | subSection            | 
      | Appliance             | 
      | Refrigerant Source    | 
      | Refrigerant Installed | 
      | Leak Check Questions  | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @wip @rework
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Refrigerant Gas questions are answered
      | Question | Answer | 
  # | Refrigerant Gas used during this visit | Yes    |
      | Refrigerant Type Used | R-404A | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      | Gas in Cylinder (lbs)    | Greater       | 
      And the following "Refrigerant Source" are added
      | Partial | OffSiteLocation | 
      | true    | true            | 
      | true    | false           | 
      | false   | false           | 
      | false   | true            | 
      And "Refrigerant Installed" has been fully used
      And the leak test repair has been checked
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Gas sub section answers have been recorded
      | subSection            | 
      | Appliance             | 
      | Refrigerant Source    | 
      | Refrigerant Installed | 
      | Leak Check Questions  | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and verify Refrigerant Source is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question | Answer | 
  #      | Refrigerant Type Used | R-404A | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
     Then the "Refrigerant Source" answers are displayed on update job page
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and Refrigerant Source is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer | 
      | Has receiver level been recorded? | Yes    | 
  #      | Refrigerant Type Used               | R-404A | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
     Then the "Refrigerant Source" answers are displayed on update job page
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and Refrigerant Installed is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
     Then the "Refrigerant Installed" is displayed
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and Refrigerant Installed is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
     Then the "Refrigerant Installed" is displayed
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and Refrigerant Installed is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
     Then the "Refrigerant Installed" is displayed
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form and Refrigerant Source is displayed 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question | Answer usage is displayed | 
  #      | Refrigerant Type Used | R-404A                    | 
      And the Gas "Refrigerant Source" sub section questions are answered with
      | Question                 | Answer        | 
      | Refrigerant Source       | Recovered Gas | 
      | Full or Partial Cylinder | Partial       | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | EPA leak check completed                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
     Then the "Refrigerant Source" answers are displayed on update job page
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form with the Leak Site Information and verify that the Modal information is displayed correctly on the update Job Page
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question                                                    | Answer                                            | 
      | Leak Check Status                                           | EPA leak check completed                          | 
      | Leak Check Result Type                                      | Leak site(s) located - One or more not accessible | 
      | Have you completed any other leak checks during this visit? | Yes                                               | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
     Then the "Leak Site Information" answers are displayed on update job page
  #The above step fails because the information is not all displayed on the main update job page
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6633 
  @notsignedoff
  Scenario Outline: Partially complete the update job form with the Leak Site Information and verify that the Modal information is displayed correctly on the update Job Page
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question                                                    | Answer                                            | 
      | Leak Check Status                                           | EPA leak check completed                          | 
      | Leak Check Result Type                                      | Leak site(s) located - One or more not accessible | 
      | Have you completed any other leak checks during this visit? | Yes                                               | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
  #     Then the "Leak Site Information" answers are displayed on update job page
  #The above step fails because the information is not all displayed on the main update job page
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6633 
  @notsignedoff
  Scenario Outline: Partially complete the update job form with the Leak Site Information and verify that the Modal information is displayed correctly on the update Job Page
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination        | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Source | 
  #      | Off Site Inventory | Partial                  | 19                 | No         | 2             | Returned to Source |
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question                                                    | Answer                                            | 
      | Leak Check Status                                           | EPA leak check completed                          | 
      | Leak Check Result Type                                      | Leak site(s) located - One or more not accessible | 
      | Have you completed any other leak checks during this visit? | Yes                                               | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question          | Answer                             | 
      | Primary Component | Condensing Unit (factory assembly) | 
  #     Then the "Leak Site Information" answers are displayed on update job page
      And the Reason for Returning is entered
      And the Return ETA Date is entered
      And the Return ETA Window is entered
      And the user updates the job
      And the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form with the Appliance and verify that the drop downs are displayed according to the business rules
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
     Then the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | 
      | Recovered Gas      | Partial                  | 25                    | 
      | Recovered Gas      | Partial                  | 25                    | 
      | Recovered Gas      | Partial                  | 25                    | 
  #      | Recovered Gas      | Partial                  | 25 |
  #      | Recovered Gas      | Partial                  | 25 |
  #      | Recovered Gas      | Partial                  | 25 |
  #      | Recovered Gas      | Partial                  | 25 |
     Then the "Refrigerant Source" answers are displayed on update job page    
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form with the Appliance and verify that the drop downs are displayed according to the business rules
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | 
      | Recovered Gas      | Partial                  | 25                    | 
      And the Gas "Refrigerant Installed" sub section questions are answered with 
      | Question            | Answer | 
      | Fully Installed     | false  | 
      | Gas Installed (lbs) | Random | 
  # Can be a number, max or random
      And the Gas "Refrigerant Surplus" sub section questions are answered with                    
      | Question    | Answer             | 
      | Destination | Returned to Source | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form with the Appliance and verify that the drop downs are displayed according to the business rules
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | 
      | Off Site Inventory | Partial                  | 25                    | 
      And the Gas "Refrigerant Installed" sub section questions are answered with 
      | Question            | Answer | 
      | Fully Installed     | false  | 
      | Gas Installed (lbs) | Random | 
  # Can be a number, max or random
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610
  @notsignedoff @rework
  Scenario Outline: Partially complete the update job form with the Appliance and verify that the drop downs are displayed according to the business rules
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit 
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question                          | Answer                                   | 
      | Appliance Type                    | Commercial Refrigeration - Remote System | 
      | Appliance Identification          | Appliance not on list                    | 
      | Has receiver level been recorded? | Yes                                      | 
      | Refrigerant Type Used             | R-404A                                   | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | 
      | Recovered Gas      | Partial                  | 25                    | 
      And the Gas "Refrigerant Installed" sub section questions are answered with 
      | Question        | Answer | 
      | Fully Installed | True   | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @rework
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
  #     When the Refrigerant Gas questions are answered
  #      | Question                               | Answer | 
  #      # | Refrigerant Gas used during this visit | Yes    | 
  #      | Gas Type                               | Other  | 
     When the Gas sub section questions are answered
      | subSection | 
  #      | Appliance             |
  #      | Refrigerant Source    |
  #      | Refrigerant Installed |
      | Leak Check Questions | 
      And the leak test repair has been checked
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Site Visit has been recorded
      And the Site Visit Gas Usage and Leak Checks have been recorded
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the on Status on Departure section is completed with status "Complete"    
      And the user updates the job
     Then the Refrigerant Gas errors are displayed
      | Question             | Answer | 
      | Refrigerant Gas Used | Yes    | 
      And the Gas sub section errors are displayed
      | subSection            | 
      | Appliance             | 
      | Refrigerant Source    | 
      | Refrigerant Installed | 
      | Leak Check Questions  | 
  
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet             | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant without Asset | 
  
  #@MCP-5702 
  @wip @rework
  Scenario Outline: Update a Non Remote Job with "<profile>" profile with Status on Departure Complete with Gas not in Plant with Asset
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas not in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
     Then the Refrigerant Gas questions are answered
      | Question                 | Answer | 
      | Refrigerant Gas Used     | Yes    | 
      | Was Gas Added to a Rack? | No     | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet        | 
      | City Resource | reactive | No      | Yes       | Fixed  | Not in Plant, no Rack | 
  
  
