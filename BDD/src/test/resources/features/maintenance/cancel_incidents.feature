@maintenance
Feature: Cancel Incidents

  @cancelIncidents
  Scenario Outline: Cancel "<monitor>" incidents via the API
    Given all except the latest "50" incidents on the "<monitor>" monitor are selected for cancellation
     When the selected incidents are cancelled via the api
     Then the selected incidents shall no longer exist on the Incidents "<monitor>" monitor
    Examples:
      | monitor             |
      | Pending To Do       |
      | Initial Escalations |
      | Reviews             |
      | Follow ups          |
