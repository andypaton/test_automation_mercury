#@MCP-5098 @MCP-5093
#@bug @MCP-9941 found on uat_ukrb
@rework
@portal @portal_fgas
@toggles @RefrigerantGas
@mcp
@wip
Feature: Portal - Jobs - Update gas job with Leak Check or Repair as a City tech 

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"

  #@MCP-5610 
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
      | Has receiver level been recorded? | Yes                                      | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination        | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Source | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question          | Answer                   | 
      | Leak Check Status | Leak check not performed | 
     Then the "Leak Check Questions" questions are configured
      | Question               | State     | 
      | Leak Check Method      | Invisible | 
      | Leak Check Result Type | Invisible | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff
  Scenario Outline: As a "<profile>", partially complete the update job form with the Leak Site Information and verify that the drop downs are displayed according to the business rules
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
     Then the "Leak Check Questions" form is populated with the following answers
      | Leak Check Status                | Leak Check Method | Leak Check Result Type | Have you completed any other leak checks during this visit? | 
      | EPA leak check completed         | Visible           | Visible                | No                                                          | 
      | Partial leak check performed     | Visible           | Visible                | No                                                          | 
      | Level 2 leak check performed     | Visible           | Visible                | No                                                          | 
      | Level 3 leak check performed     | Visible           | Visible                | No                                                          | 
      | Preliminary leak check performed | Visible           | Visible                | No                                                          | 
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff
  Scenario Outline: As a "<profile>", partially complete the update job form with the Leak Site Information and verify Status on Departure is Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | EPA leak check completed                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question                  | Answer                             | 
      | Primary Component         | Condensing Unit (factory assembly) | 
      | Initial Verification Test | Failed Test                        | 
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff
  Scenario Outline: As a "<profile>", partially complete the update job form with the Leak Site Information and verify Status on Departure is Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                                            | 
      | Leak Check Status      | EPA leak check completed                          | 
      | Leak Check Result Type | Leak site(s) located - One or more not accessible | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question                    | Answer                             | 
      | Primary Component           | Condensing Unit (factory assembly) | 
      | Initial Verification Test   | Halide Leak Detector - Passed      | 
      | Follow Up Verification Test | Failed Test                        | 
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff
  Scenario Outline: As a "<profile>", partially complete the update job form with the Leak Site Information and verify Status on Departure is Enabled
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the Gas "Leak Site Information" sub section questions are answered with
      | Question                    | Answer                             | 
      | Primary Component           | Condensing Unit (factory assembly) | 
      | Leak Site Status            | Leak site repaired - Brazed        | 
      | Initial Verification Test   | Failed Test                        | 
      | Follow Up Verification Test | Halide Leak Detector - Passed      | 
     Then on the form "Update Job" the question "Status on Departure" is answered with "Please select a status"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff 
  Scenario Outline: As a "<profile>", partially complete the update job form with the Leak Site Information and verify Status on Departure is Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component                  | Initial Verification Test     | Follow Up Verification Test | 
      | Condensing Unit (factory assembly) | Halide Leak Detector - Passed | Failed Test                 | 
      | Condenser (factory assembly)       | Soap Bubbles - Passed         | Soap Bubbles - Passed       | 
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848 
  @notsignedoff
  Scenario Outline: As a "<profile>", complete the update job form with the Leak Site Information, save the job with status Returning and verify the details have been saved correctly
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information 
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question              | Answer                                   | 
      | Appliance Type        | Commercial Refrigeration - Remote System | 
      | Refrigerant Type Used | UnAnswered                               | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component                  | Initial Verification Test     | Follow Up Verification Test | 
      | Condensing Unit (factory assembly) | Halide Leak Detector - Passed | Failed Test                 | 
      | Condenser (factory assembly)       | Soap Bubbles - Passed         | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Returning"
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-6848  @MTA-920
  @notsignedoff
  Scenario Outline: As a "<profile>", complete the update job form with the Leak Site Information, save the job with status Returning and verify the details have been saved correctly
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question              | Answer                                   | 
      | Appliance Type        | Commercial Refrigeration - Remote System | 
      | Refrigerant Type Used | UnAnswered                               | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Timeline Event Summary has been updated with "Complete"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-6848  @MTA-923
  @notsignedoff
  Scenario Outline: As a "<profile>", complete the update job form with the Leak Site Information, save the job with Status on Departure is Awaiting Parts and verify the details have been saved correctly
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "Allocated" jobs only assigned to "Single" resource with status "ETA Advised To Site" or "ETA Provided" with Gas in Plant with Asset
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit      
      And a Leak Check or Repair is carried out
      And the Gas "FGAS Appliance" sub section questions are answered with
      | Question              | Answer                                   | 
      | Appliance Type        | Commercial Refrigeration - Remote System | 
      | Refrigerant Type Used | UnAnswered                               | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Awaiting Parts"
      And "1" new part with a price between between 0.01 and 250.00 is ordered for "Trade Counter Collection" on the "Same Day"
     Then the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Awaiting Parts Review"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  
