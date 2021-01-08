@portal @portal_funding_requests @portal_funding_requests_initial_funding_request
Feature: Portal - Funding Requests - Initial Funding Request

  @bugRainbow
  @mcp
  Scenario: RFM views Initial Funding Requests Awaiting Approval [bug: MCP-13654]
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
     When the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     Then the "Funding Requests" table on the "Jobs Awaiting Initial Funding Request Approval" page displays correctly
      And a search box is present on the "Jobs Awaiting Initial Funding Request Approval" page
      And the "Jobs Awaiting Initial Funding Request Approval" table can be sorted on all columns
  
  @mcp 
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM the Jobs Awaiting Initial Funding Request Approval Grid displays the latest job data correctly
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
     When the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     Then the "Jobs Awaiting Initial Funding Request Approval" table on the "Initial Funding Requests" page displays the latest job row correctly
     
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request can be approved
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is approved with random funding route
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "New Job Notification Sent - Initial Funding Request"
      And the Timeline Event Summary has been updated with "Funding Approved"

  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request can be rejected with a random reason
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with a random reason
      | Duplicate Job            |
      | Works No Longer Required |
      | Warranty Job             |
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected"
      And the Timeline Event Summary has been updated with "Funding Declined"

  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request can be rejected selecting always chargeable contractor without zero price as an Alternative Resource
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Select Alternative Resource"
      And a "Contractor who is always chargeable and without zero price in the pricebook" is selected as an alternative resource
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected"
      And the Timeline Event Summary has been updated with "Funding Declined"
      And the Timeline Event Summary has been updated with "Resource Added - Awaiting Funding Authorization"
      And the Timeline Event Summary has been updated with "Awaiting Funding Authorization"
      
  #//ToDo - Uncomment the last assertion step and modify code if necessary after receiving clarification on MCP-10601
  @ukrb
  @toggles @AutoAssign @AutoApproveContractorFundingRequests 
  Scenario: Verify as a RFM an Initial Funding Request can be rejected selecting always chargeable Contractor with zero price as an Alternative Resource
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Select Alternative Resource"
      And a "Contractor who is always chargeable and with zero price in the pricebook" is selected as an alternative resource
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected"
      And the Timeline Event Summary has been updated with "Funding Declined"
      And the Timeline Event Summary has been updated with "Resource Added - Awaiting Funding Authorization"
      And the Timeline Event Summary has been updated with "Awaiting Funding Authorization"
      And the Timeline Event Summary has been updated with "Funding Approved"
      #And the Timeline Event Summary has been updated with "Email notification sent to"
      
  #//ToDo - Uncomment the last assertion step and modify code if necessary after receiving clarification on MCP-10601
  @ukrb
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request can be rejected selecting non-chargeable Contractor as an Alternative Resource
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Select Alternative Resource"
      And a "Contractor who is non-chargeable" is selected as an alternative resource
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected"
      And the Timeline Event Summary has been updated with "Funding Declined"
      And the Timeline Event Summary has been updated with "Resource Added - New Job Notification Sent"
     #And the Timeline Event Summary has been updated with "Email notification sent to"
  
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request can be rejected selecting a City Resource as an Alternative Resource
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Select Alternative Resource"
      And a "City Technician" is selected as an alternative resource
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected"
      And the Timeline Event Summary has been updated with "Funding Declined"
      And the Timeline Event Summary has been updated with "Resource Added - New Job Notification Sent"
            
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Verify as a RFM an Initial Funding Request without additional resources can be rejected with Request a Quote
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Request a Quote" 
      And the funding route is selected
      And the potential insurance question is answered
      And the quote priority is selected
      And the quote scope of works is entered
      And the number of quotes required is set to Funding Route minimum
      And the quote resources are populated
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected - Request a Quote"
      And the Timeline Event Summary has been updated with "Funding Declined"
      And the Timeline Event Summary has been updated with "Job Type changed to Quote"
      And the Timeline Event Summary has been updated with "Quote Request Raised"
      And the Timeline Event Summary has been updated with "Quote Request Approver Set"
      And the JobTimelineEvent table has been updated with "Resources Invited to Quote"
      And the JobTimelineEvent table has been updated with "Invitation To Quote Notification"

  @mcp
  Scenario: Verify as a RFM an Initial Funding Request with additional resources can be rejected with Request a Quote
    Given a portal user with a "RFM" permission and with "Logged / Allocated / Awaiting Funding Authorisation" Jobs with additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
      And the funding request is rejected with "Request a Quote" 
      And the funding route is selected
      And the potential insurance question is answered
      And the quote priority is selected
      And the quote scope of works is entered
      And the number of quotes required is set to Funding Route minimum
      And the quote resources are populated
      And the "Submit" button is clicked
     Then the Resource Assignment table has been updated with the status "Funding Request Rejected - Request a Quote"
      And the Timeline Event Summary has been updated with "Job linked to Quote Job"
      
  #intermittentBug @MCP-12998 bug on uat_ukrb
  @mcp @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: RFM views an Initial Funding Request Awaiting Approval
    Given a portal user with a "RFM" permission and with "Logged / Awaiting Funding Authorisation" Jobs with out additional resources
      And the user logs in
      And the "Initial Funding Requests" sub menu is selected from the "Funding Requests" top menu
     When the user "Views" a "Initial Funding Request"
     Then the job details are displayed
      And the funding request details are displayed
      And all resource assignment details are displayed