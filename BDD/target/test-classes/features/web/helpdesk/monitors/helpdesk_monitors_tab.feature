@helpdesk @helpdesk_monitors @helpdesk_monitors_tab
Feature: Helpdesk - Monitors - Monitors Tab

  # commented out code to assert counts due to bug MCP-7255 
  @mcp
  Scenario Outline: Monitor Tab - Counts - "<tile>" tile
    Given a "IT" has logged in
     When the "<tile>" tile is selected
     Then the My List "<section>" count matches the number of rows displayed
      And the first monitor in the "<section>" section is displayed by default
      And the monitor displays "<sections>" sections
    Examples: 
      | tile            | sections                  | section  | 
      | Jobs            | Settings, To Do, For Info | To Do    |
      | Incidents       | Settings, To Do, For Info | To Do    |
      | Admin           | Settings, For Info        | For Info |  
      | Landlord Jobs   | Settings, To Do, For Info | To Do    |
      | One Stop Shop   | Settings, To Do, For Info | To Do    |
      | Health & Safety | Settings, To Do, For Info | To Do    |
 
 # Separating this scenario because Bureau monitor is not active for Walmart client
  @ukrb
  Scenario: Monitor Tab - Counts - Bureau tile
    Given a "IT" has logged in
     When the "Bureau" tile is selected
     Then the My List "To Do" count matches the number of rows displayed
      And the first monitor in the "To Do" section is displayed by default
      And the monitor displays "Settings, To Do" sections
       
  # commented out code to assert counts due to bug MCP-7255        
  @uswm
  Scenario: Monitor Tab - Counts - Focus tile
    Given a "Helpdesk Operator" has logged in
     When the "Focus" tile is selected
     Then the My List "To Do" count matches the number of rows displayed
      And the first monitor in the "To Do" section is displayed by default
      And the monitor displays "Settings, To Do" sections
  
  @mcp     
  Scenario: Monitor Tab - Monitor key
    Given a "Helpdesk Operator" has logged in
     When the "Jobs" tile is selected
     Then the monitor key is displayed
  
  @mcp
  Scenario Outline: Monitor Tab - Active monitors
    Given a "IT" has logged in
      And the "<tile>" tile is selected
     Then the "To Do" section displays all configured active monitors for "<tile>" 
      And the "For Info" section displays all configured active monitors for "<tile>"
    Examples: 
      | tile            | 
      | Jobs            | 
      | Incidents       | 
      | Health & Safety |
      | One Stop Shop   |
      | Landlord Jobs   |
      
  @uswm    
  Scenario: Monitor Tab - Active monitors
    Given a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
     Then the "To Do" section displays all configured active monitors for "Focus"
      