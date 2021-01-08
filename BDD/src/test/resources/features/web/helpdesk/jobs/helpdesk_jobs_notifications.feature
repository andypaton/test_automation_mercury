@helpdesk @helpdesk_jobs @helpdesk_jobs_notifications
Feature: Helpdesk - Jobs - Notifications


  ### NOTE: For notifications to be sent to ipad a token must exist in table ApplicationUserMobileApplication
  
  @loggedByApi
  @mcp
  Scenario Outline: Notification for a City resource with "<phone>" phone, with email and "<ipad>" ipad
    Given City Resource can be assigned an ipad
      And a "Helpdesk Operator" has logged in
     When a new job is logged and assigned to a City resource with "<phone>" phone, "with" email and "<ipad>" ipad
      And the job is viewed
     Then the resource status is "<resourceStatus>"
      And the timeline displays a new "<event>" event - ios
      And resource type table restored to default value
    Examples: 
      | phone  | ipad | resourceStatus            | event                                                                    | 
      | mobile | with | New Job Notification Sent | Notification and text message sent                                       | 
      | other  | with | New Job Notification Sent | Notification sent, No text message sent as no number configured          | 
      | mobile | no   | Call Required             | Resource Notified by SMS, No IPad notification was sent - Not configured | 
      | other  | no   | Call Required             | Email notification sent                                                  | 
 
  #environment tag removed because all environments have AutoApproveContractorFundingRequests set to disabled
  @loggedByApi
  @toggles @AutoApproveContractorFundingRequests
  Scenario: Notification for a Contractor with "other" phone and with email
    Given a "Helpdesk Operator" has logged in
      And the system feature toggle "AutoApproveContractorFundingRequests" is "enabled"
     When a new job is logged and assigned to a Contractor with "other" phone and "with" email
      And the job is viewed
     Then the resource status is "New Job Notification Sent"
      And the timeline displays a new "Email notification sent" event

      