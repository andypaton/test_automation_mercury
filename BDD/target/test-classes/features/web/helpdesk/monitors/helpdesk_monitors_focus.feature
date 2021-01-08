@helpdesk @helpdesk_monitors @helpdesk_monitors_focus
Feature: Helpdesk - Monitors - Focus

  @usad
  Scenario: To Do/Chase Monitor 
    Given a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
      And All teams are selected from Settings
     When the "Chase" monitor is selected from "To Do" section
     Then the Chase monitor will display the jobs with "active" chase event
      And the Chase monitor will display the jobs with "multiple active" chase event
      And the Chase monitor will not display the jobs with "resolved" chase event
  
  @usad
  Scenario: To Do/Chase Monitor - job will display as many times as there are active chases 
    Given a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
      And All teams are selected from Settings
      And the "Chase" monitor is selected from "To Do" section
     When the jobs with multiple active chase event has been searched 
     Then the job will display as many times as there are active chases 
  
  @uswm @usad @deprecated @wip
  Scenario: For Info/Scheduled Callbacks monitor
    Given a "Helpdesk Operator" has logged in
      And the user has jobs with a scheduled callback event
      And the "Focus" tile is selected
      And All teams are selected from Settings
     When the "Scheduled Call Back" monitor is selected from "For Info" section
     Then the "Scheduled Call Back" monitor will display the jobs with scheduled callback event
      And the Scheduled Call-Backs monitor will not display the jobs for which scheduled call back date and time has passed
      And the "Scheduled Call Back" monitor will not display the jobs with resource status "ETA Provided"
      And the "Scheduled Call Back" monitor will not display the jobs with resource status "Declined"
      
  @uswm @usad
  Scenario: To Do/ETA Expired monitor
    Given a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
      And All teams are selected from Settings
     When the "ETA Expired" monitor is selected from "To Do" section
     Then the "ETA Expired" monitor will display the jobs where provided ETA date and time has been elapsed
      And the "ETA Expired" monitor will not display the jobs with resource status "On Site"
    
  @grid @uswm @deprecated @wip
  Scenario: To Do/SLA Near-Missed monitor
    Given the monitor "SLA Near/Missed" from "Focus" area is enabled
      And a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
      And All teams are selected from Settings
     When the "SLA Near/Missed" monitor is selected from "To Do" section
     Then the "SLA Near/Missed" monitor will display the jobs sorted by priority and then Response/Repair Due time 
      And the "SLA Near/Missed" monitor will display the jobs based on the priority
      And the SLA Near/Missed monitor will not display the invalid jobs
      
  @uswm @usad
  Scenario: To Do/ETA Greater than Response time monitor
    Given a "Helpdesk Operator" has logged in
      And the "Focus" tile is selected
      And All teams are selected from Settings
     When the "ETA greater than response/repair time" monitor is selected from "To Do" section
     Then the ETA greater than response/repair time monitor will display the jobs where the ETA is outwith the configured response time
      And the ETA greater than response/repair time monitor will not display the "P3" jobs
      And the ETA greater than response/repair time monitor will not display the "Quote" jobs
      And the ETA greater than response/repair time monitor will not display the "Onsite Event" jobs