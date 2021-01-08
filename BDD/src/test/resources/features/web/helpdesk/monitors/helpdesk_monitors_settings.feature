@helpdesk @helpdesk_monitors @helpdesk_monitors_settings
Feature: Helpdesk - Monitors - Settings  
 
  @mcp @bugWalmart
  Scenario: User default settings [bug: MCP-14439]
    Given a "Helpdesk Operator" from a team has logged in
     When a random tile is selected
     Then the users settings are defaulted to include logged in user's team and all Asset Types
      And the monitors only display rows matching the settings
      And the My List count for the active monitor matches the number of rows in the monitor table

  # Excluding this scenario for Advocate as there is only one Team "7-Eleven" set for this Environment.
  @uswm @ukrb
  Scenario: Add Team
    Given a "Helpdesk Operator" from a team has logged in
     When a random tile is selected
      And another team is added to Settings
     Then the monitors display rows for "both" teams
      And the My List count for the active monitor matches the number of rows in the monitor table

  @mcp
  Scenario: Remove all Teams
    Given a "Helpdesk Operator" from a team has logged in
     When a random tile is selected
      And the default team is removed from Settings
     Then the monitors display rows for "All" teams
      And the My List count for the active monitor matches the number of rows in the monitor table

  # Excluding this scenario for Advocate as there is only one Team "7-Eleven" set for this Environment.
  @uswm @ukrb
  Scenario: Replace Team
    Given a "Helpdesk Operator" from a team has logged in
     When a random tile is selected
      And another team is added to Settings
      And the default team is removed from Settings
     Then the monitors only display rows for the "newly added" team
      And the My List count for the active monitor matches the number of rows in the monitor table

  @mcp
  Scenario: Add Asset Type
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And All teams are selected from Settings
      And multiple Asset Types from the displayed monitor are added to Settings
     Then the monitors only displays rows for the matching Asset Types
      And the My List count for the active monitor matches the number of rows in the monitor table
 
  @mcp
  Scenario: Remove all Asset Types
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And an Asset Type from the displayed monitor is added to Settings
      And all Asset Types are removed from Settings
     Then the monitors display rows for "All" Asset Types
      And the My List count for the active monitor matches the number of rows in the monitor table

  @mcp
  Scenario: Remove Asset Type
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
      And All teams are selected from Settings
      And multiple Asset Types from the displayed monitor are added to Settings
      And an Asset Type is removed from Settings
     Then the monitors only displays rows for the matching Asset Types
      And the My List count for the active monitor matches the number of rows in the monitor table
