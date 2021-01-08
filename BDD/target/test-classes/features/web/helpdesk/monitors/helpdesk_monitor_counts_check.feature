@helpdesk @helpdesk_monitors @helpdesk_monitors_counts_check @wip
Feature: Helpdesk - Monitors - Counts check

  Scenario: Home Page - Validate My List and National Total counts for each Tile 
    Given a "IT" has logged in
      And the "helpdesk" page is displayed with the user profiles configured monitor tiles
     Then the My List and National total counts for each tile matches with the counts on each monitor tab     
     
  Scenario: Awaiting Acceptance - Monitor Tab - Count check
    Given City Resource can be assigned an ipad
      And a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And the count for "Awaiting Acceptance" monitor from "To Do" section has been saved
     When a new job is logged and assigned to a City resource with "no" phone, "with" email and "with" ipad and "PE" priority
     Then the resource status changed to "New Job Notification Sent"
      And the monitor count has been increased
      
  Scenario: Incidents - Reviews Monitor - Count check
    Given City Resource can be assigned an ipad
      And a "Helpdesk Operator" has logged in
      And the "Incidents" tile is selected
      And the count for "Reviews" monitor from "To Do" section has been saved
     When a new incident is logged with escalation criteria option "No"
     Then the monitor count has been increased
      
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Funding Requests - To Do Monitor - Count check
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And the count for "Funding Requests" monitor from "To Do" section has been saved
     When a new job is logged and assigned to a contractor at a vendor store
     Then the resource status changed to "Awaiting Funding Authorisation"
      And the monitor count has been increased
  
  @toggles @AutoAssign @AutoApproveContractorFundingRequests
  Scenario: Uplift Requests - To Do Monitor - Count check
    Given the system feature toggle "AutoAssign" is "enabled"
      And the system feature toggle "AutoApproveContractorFundingRequests" is "disabled"
      And a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And the count for "Uplift Requests" monitor from "To Do" section has been saved
     When a new job assigned to a contractor is logged and initial funding request is authorised
      And the contractor accepts the job and a new uplift funding request is created with known amount
     Then the resource status changed to "Awaiting Funding Authorisation"
      And the monitor count has been increased