#@bug @MCP-9941 found on uat_ukrb
@portal @notsignedoff @portal_fgas
@toggles @RefrigerantGas
@wip
Feature: Portal - Jobs - Update gas job with returning status as a City tech 

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"
  
  #@MCP-5610 
  @notsignedoff @mcp
  Scenario Outline: Return to a FGas job with Leak Check - gas not used on the return - verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @mcp
  Scenario Outline: Return to a FGas job with Leak Check - verify when gas is used the appliance information cannot be edited 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out      
     Then all "FGas Apppliance" sub section questions cannot be edited
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @mcp
  Scenario Outline: Return to a FGas job with Leak Check - verify when gas is not used but a leak check is carried out the appliance information cannot be edited 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit     
     Then all "FGas Apppliance" sub section questions cannot be edited
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @mcp
  Scenario Outline: Return to a FGas job with Leak Check - gas not used on the return, verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out
      And the update Job form is completed with status on departure "Returning"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @uswm
  Scenario Outline: Return to a FGas job with Leak Check - return cylinder to source and add Leak Check with returning status
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the "FGAS Appliance" form is populated with the following answers
      | Appliance Type | Appliance Identification | Please provide appliance details | Has receiver level been recorded? | Quantity of Balls Floating | Provide Level Indicator % | Refrigerant Type Used | 
      | PreAnswered    | PreAnswered              | PreAnswered                      | PreAnswered                       | PreAnswered                | PreAnswered               | PreAnswered           | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination        | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Source | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Returning"
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Resource Assignment table has been updated with the status "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @ukrb
  Scenario Outline: Return to a FGas job with Leak Check - returned cylinder to supplier and add Leak Check with returning status
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the "FGAS Appliance" form is populated with the following answers
      | Appliance Type | Appliance Identification | Please provide appliance details | Has receiver level been recorded? | Quantity of Balls Floating | Provide Level Indicator % | Refrigerant Type Used | 
      | PreAnswered    | PreAnswered              | PreAnswered                      | PreAnswered                       | PreAnswered                | PreAnswered               | PreAnswered           | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination          | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Supplier | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Returning"
     Then the "Portal" "Job" Update Saved page is displayed
      And the Job is updated with a "<status>" status
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Resource Assignment table has been updated with the status "Returning"
    Examples: 
      | profile       | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @uswm
  Scenario Outline: Return to a FGas job with Leak Check - gas not used on the return - follow up all Leak Site checks, add a new passing Leak Site check and complete the job
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And all Leak Checks are followed up
      | Primary Component | Primary Component Information | Sub-Component | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | PreAnswered       | PreAnswered                   | PreAnswered   | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  @notsignedoff @ukrb
  Scenario Outline: Return to a FGas job with Leak Check - gas not used on the return - Full leak check completed
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                    | 
      | Leak Check Status      | Full leak check completed | 
      | Leak Check Result Type | No leak found             | 
      And all Leak Checks are followed up
      | Primary Component | Primary Component Information | Sub-Component | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | PreAnswered       | PreAnswered                   | PreAnswered   | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the "Leak Site Information" form is populated with the following answers
      | Primary Component            | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | Condenser (factory assembly) | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then the "Portal" "Job" Update Saved page is displayed
      And the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
    Examples: 
      | profile       | jobtype  | arrival | departure | status | gasQuestionSet | 
      | City Resource | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
        
  #@MCP-5610 
  @notsignedoff @uswm
  Scenario Outline: Return to a FGas job with Leak Check - gas not used on the return - EPA leak check completed
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the "FGAS Appliance" form is populated with the following answers
      | Appliance Type | Appliance Identification | Please provide appliance details | Has receiver level been recorded? | Quantity of Balls Floating | Provide Level Indicator % | Refrigerant Type Used | 
      | PreAnswered    | PreAnswered              | PreAnswered                      | PreAnswered                       | PreAnswered                | PreAnswered               | PreAnswered           | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination        | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Source | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                   | 
      | Leak Check Status      | EPA leak check completed | 
      | Leak Check Result Type | No leak found            | 
      And all Leak Checks are followed up
      | Primary Component | Primary Component Information | Sub-Component | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | PreAnswered       | PreAnswered                   | PreAnswered   | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
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
  
  @notsignedoff @ukrb
  Scenario Outline: Return to a FGas job with Leak Check - use gas on the return - follow up all Leak Site checks, add a new passing Leak Site check and complete the job
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress / Returning" job with Gas used and with a Leak Check and a Unknown asset and is required to return
      And the user logs in    
      And the user views an "<jobtype>" "Open" job   
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit
      And the "FGAS Appliance" form is populated with the following answers
      | Appliance Type | Appliance Identification | Please provide appliance details | Has receiver level been recorded? | Quantity of Balls Floating | Provide Level Indicator % | Refrigerant Type Used | 
      | PreAnswered    | PreAnswered              | PreAnswered                      | PreAnswered                       | PreAnswered                | PreAnswered               | PreAnswered           | 
      And the "Refrigerant Source" form is populated with the following answers
      | Refrigerant Source | Full or Partial Cylinder | Gas in Cylinder (lbs) | Fully Used | Gas Installed (lbs) | Destination          | 
      | Recovered Gas      | Partial                  | 19                    | No         | 2                   | Returned to Supplier | 
      And the Gas "Leak Check Questions" sub section questions are answered with
      | Question               | Answer                    | 
      | Leak Check Status      | Full leak check completed | 
      | Leak Check Result Type | No leak found             | 
      And all Leak Checks are followed up
      | Primary Component | Primary Component Information | Sub-Component | Leak Site Status               | Initial Verification Test | Follow Up Verification Test | 
      | PreAnswered       | PreAnswered                   | PreAnswered   | Leak site repaired - Tightened | Soap Bubbles - Passed     | Soap Bubbles - Passed       | 
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
      