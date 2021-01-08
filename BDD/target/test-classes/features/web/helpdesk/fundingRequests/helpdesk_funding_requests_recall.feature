@helpdesk @helpdesk_funding_requests @helpdesk_funding_requests_recall
Feature: Helpdesk - Funding Requests - Recall

  # RecallResources toggle is enabled on Walmart only
  @uswm
  @toggles @RecallResources
  Scenario: Recall completed P1 job within 14 days
    Given the system feature toggle "RecallResources" is "enabled"
      And a "Helpdesk Manager" has logged in
      And there is a completed P1 job within last 14 days assigned to a contractor
     When a duplicate job is created
      And Funding requests are displayed
     Then the following alert is displayed: "Recall Job! The recommended resource can be called out at no charge"
      And the previous funding request is displayed
      And the timeline displays a "Funding Approved" event with Notes "Recall Amount Authorized: $0.00"
      