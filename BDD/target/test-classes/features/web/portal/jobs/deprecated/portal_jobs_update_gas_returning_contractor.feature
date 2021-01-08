@portal @portal_fgas
@toggles @RefrigerantGas @rework
@mcp @geolocation
@wip
Feature: Portal - Jobs - Update gas job with returning status as a contractor 

  Background: System Feature Toggles are set for Refrigerant Gas
    Given the system feature toggle "RefrigerantGas" is "enabled"

  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile               | jobtype  | arrival | departure | status | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and without a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
     Then on the form "Update Job" the question "Status on Departure" is answered with "Returning"
    Examples: 
      | profile               | jobtype  | arrival | departure | status | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and and verify when gas is used the appliance information cannot be edited 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is carried out
     Then all "FGas Apppliance" sub section questions cannot be edited
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and and verify when gas is not used but a leak check is carried out the appliance information cannot be edited 
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked     
      And the Update Job form is complete with basic information
     When Refrigerant gas is used during this visit     
     Then all "FGas Apppliance" sub section questions cannot be edited
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is not carried out
      And the update Job form is completed with status on departure "Returning"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Returning"
      And the Timeline Event Summary has been updated with "Resource returning"
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, verify status on depature is set to Returning
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "On Site" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
      And the Update Job form is complete with basic information
     When Refrigerant gas is not used during this visit
      And a Leak Check or Repair is not carried out
      And the update Job form is completed with status on departure "Awaiting Parts"
     Then the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Awaiting Parts"
      And the Timeline Event Summary has been updated with "Resource Awaiting Parts"
    Examples: 
      | profile               | jobtype  | arrival | departure | status      | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | In Progress | FGAS Appliance | 
  
  #@MCP-5610 
  Scenario Outline: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, follow up all Leak Site checks, add a new passing Leak Site check and complete the job
    Given a portal user with profile "<profile>"
      And gas questions for "<gasQuestionSet>" gas type
      And has "<jobtype>" "In Progress" jobs only assigned to "Single" resource with status "Returning" with Gas used and with a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked   
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
     Then the Site Visit Gas Usage and Leak Checks have been recorded
      And the Job is updated with a "<status>" status
      And the Resource Assignment table has been updated with the status "Complete"
      And the Timeline Event Summary has been updated with "Complete"
    Examples: 
      | profile               | jobtype  | arrival | departure | status | gasQuestionSet | 
      | Contractor Technician | reactive | No      | Yes       | Fixed  | FGAS Appliance | 
  
