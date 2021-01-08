@portal @portal_fgas @rework
@mcp @geolocation
@wip
Feature: Portal - Jobs - Update job as a contractor

#  Background: System Feature Toggles are set for US Gas Regulations
#    Given the system feature toggle "RefrigerantGas" is "enabled"
#      And the system sub feature toggle "US Regulations" is "enabled"
#      And the system sub feature toggle "UK Regulations" is "disabled"
      
  #@MCP-5610
  Scenario: Return to a job where both Gas was used and a Leak check was carried out and do not use gas on the return, verify status on depature is set to Returning
    Given a portal user with profile "Contractor Technician"
      And gas questions for "FGAS Appliance" gas type
      And an asset with gas
      And has "reactive" "In Progress" jobs only assigned to "Single" resource with status "Returning" without Gas used and without a Leak Check and is required to return
      And is within GEO radius
      And has started the job
      And the user logs in    
      And the "Stop Work" button is clicked    
     When the Update Job form is complete with basic information
      And the update Job form is completed with status on departure "Complete"
      And the user updates the job
     Then  the Job is updated with a "Fixed" status
      And the Resource Assignment table has been updated with the status "Complete"    