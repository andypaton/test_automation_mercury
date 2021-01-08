@admin @admin_resources @admin_resources_contractor_callout
@mcp
Feature: Admin - Contractor Callout

@andy
  Scenario Outline: Configure Exception Call Out Costs to Contractor - "<Callout Type>"
    Given a user with "Mercury_Admin_Core" role has logged in
      And "Admin" is selected from the Mercury navigation menu
      And the "Resources & Users" tile is selected
      And the user selects "Resources" from the sub menu
     When an Active Contractor Resource is created
      And the resource is edited
      And a "<Callout Type>" exception is added
     Then a new rate is added for Site with "<Callout Type>" type
    Examples:
      | Callout Type |
      | Standard     |
      | Recall       |
      | Out of Hours |
      | Subsequent   |