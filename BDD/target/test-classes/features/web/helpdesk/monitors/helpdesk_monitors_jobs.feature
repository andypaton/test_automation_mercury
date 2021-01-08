@helpdesk @helpdesk_monitors @helpdesk_monitors_jobs
Feature: Helpdesk - Monitors - Jobs

  #@MTA-802 @MTA-1045
  @mcp
  Scenario: To Do/Funding Requests Monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Funding Requests" monitor is selected from "To Do" section
     Then the "Funding Requests" monitor will display the jobs with resource status "Awaiting Funding Authorisation"
      And the "Funding Requests" monitor will not display the jobs with resource status "New Job Notification Sent"
      
  #@MTA-802 @MTA-1045
  @mcp @smoke
  Scenario: To Do/Removal Requests Monitor
    Given a "Helpdesk Operator" has logged in
      And there is a job with status "Removal Requested"
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Removal Requests" monitor is selected from "To Do" section
     Then the Removal Requests monitor will display the jobs with resource status "Removal Requested"
      And the "Removal Requests" monitor will not display the jobs with resource status "Removed"
      
  #@MTA-809 @MTA-1045
  @mcp
  Scenario: To Do/Uplifts Monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Uplift Requests" monitor is selected from "To Do" section
     Then the "Uplift Requests" monitor will display the jobs with initial funding request authorised
      And the "Uplift Requests" monitor will not display the jobs without initial funding request authorised
      
  #@MTA-812 @MTA-1045
  @mcp
  Scenario: To Do/Chase Monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Chase" monitor is selected from "To Do" section
     Then the Chase monitor will display the jobs with "active" chase event
      And the Chase monitor will display the jobs with "multiple active" chase event
      And the Chase monitor will not display the jobs with "resolved" chase event
      
  @mcp 
  Scenario: To Do/Awaiting Acceptance Monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Awaiting Acceptance" monitor is selected from "To Do" section
     Then the "Awaiting Acceptance" monitor will display the jobs with resource status "New Job Notification Sent, Call Required"
      And the "Awaiting Acceptance" monitor will display the jobs for which deferral time has been passed and notification period has been missed
      And the "Awaiting Acceptance" monitor will not display the jobs with resource status "ETA Provided"
      And the "Awaiting Acceptance" monitor will not display the P2 or P3 "Out Of Hours" jobs 
      
  @mcp 
  Scenario: For Info/Deferred jobs monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Deferred jobs" monitor is selected from "For Info" section
     Then the Deferred jobs monitor will display the jobs with active deferral event
      And the "Deferred jobs" monitor will display jobs with status "Job Advise Deferred"
      And the "Deferred jobs" monitor will display the P2 or P3 "Out Of Hours" jobs
      And the "Deferred jobs" monitor will not display the jobs with resource status "New Job Notification Sent"
      And the "Deferred jobs" monitor will not display the jobs with resource status "Call Required"
      And the "Deferred jobs" monitor will not display the P2 or P3 "In Hours" jobs
      
  @mcp
  Scenario: For Info/Notification Window monitor
    Given a user has "Quote" jobs with status "New Job Notification Sent"
      And a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Notification Window" monitor is selected from "For Info" section
     Then the "Notification Window" monitor will display the jobs with "New Job Notification Sent" resource status
      And the "Notification Window" monitor will display the jobs for which notification period "has not" been exceeded 
      And the "Notification Window" monitor will not display the jobs for which notification period "has" been exceeded 
      And the "Notification Window" monitor will not display the jobs with resource status "ETA Provided"
      And the "Notification Window" monitor will not display the jobs with resource status "Declined"
      
  @ukrb @uswm @usah
  Scenario: For Info/Parked Jobs monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Parked Jobs" monitor is selected from "For Info" section
     Then the Parked Jobs monitor will display the jobs with parked event and for which parked date and time is not expired
      And the Parked Jobs monitor will not display the jobs for which parked date and time has expired
      And the Parked Jobs monitor will not display the job which has reverted back to Logged status    
    
    @usad
    Scenario: For Info/Parked Jobs monitor
    Given a "Helpdesk Manager" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Parked Jobs" monitor is selected from "For Info" section
     Then the Parked Jobs monitor will display the jobs with parked event and for which parked date and time is not expired
      And the Parked Jobs monitor will not display the jobs for which parked date and time has expired
      And the Parked Jobs monitor will not display the job which has reverted back to Logged status    
      
      
  # RAINBOW: P1 and P2 unassigned jobs should show at all times (with some caveats like the job has been manually deferred), however, P3 and above should only show between 8am and 5pm BST
  @ukrb
  Scenario: To Do/Awaiting Assignment monitor
     Given a "Helpdesk Operator" has logged in
      And all job types exist for the "Awaiting Assignment" monitor
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Awaiting Assignment" monitor is selected from "To Do" section
     Then the Awaiting Assignment monitor will display the correct jobs
      And all jobs Awaiting Assignment will be displayed when the helpdesk is In Hours
      And only jobs Awaiting Assignment with "P1,P2,P0" priorities will be displayed when the helpdesk is Out Of Hours
      And the Awaiting Assignment monitor only displays jobs where primary or requested additional resource is unassigned      
   
  # WALMART: P1: Always show, P2: Show 7 days a week in hours (7am to 5pm EST), P7: Monday to Friday in hours (7am to 5pm EST)
  @notsignedoff @uswm
  Scenario: To Do/Awaiting Assignment monitor
     Given a "Helpdesk Operator" has logged in
      And all job types exist for the "Awaiting Assignment" monitor
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Awaiting Assignment" monitor is selected from "To Do" section
     Then the Awaiting Assignment monitor will display the correct jobs
      And "PE" jobs Awaiting Assignment will always be displayed
      And "P2" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Sunday
      And "P7" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Friday
      And the Awaiting Assignment monitor only displays jobs where primary or requested additional resource is unassigned   
      
  @notsignedoff @usad
   Scenario: To Do/Awaiting Assignment monitor
     Given a "Helpdesk Operator" has logged in
      And all job types exist for the "Awaiting Assignment" monitor
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Awaiting Assignment" monitor is selected from "To Do" section
     Then the Awaiting Assignment monitor will display the correct jobs
      And "P1" jobs Awaiting Assignment will always be displayed
      And "P2" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Sunday
      And "P3" jobs Awaiting Assignment will only be displayed when the helpdesk is In Hours Monday to Friday
      And the Awaiting Assignment monitor only displays jobs where primary or requested additional resource is unassigned   

  @mcp
  Scenario: For Info/Scheduled Callbacks monitor
    Given a "Helpdesk Operator" has logged in
      And the user has jobs with a scheduled callback event
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Scheduled Call-Backs" monitor is selected from "For Info" section
     Then the "Scheduled Call-Backs" monitor will display the jobs with scheduled callback event
      And the Scheduled Call-Backs monitor will not display the jobs for which scheduled call back date and time has passed
      And the "Scheduled Call-Backs" monitor will not display the jobs with resource status "ETA Provided"
      And the "Scheduled Call-Backs" monitor will not display the jobs with resource status "Declined"
      
  @uswm @ukrb @usah
  Scenario: For Info/Watched Jobs monitor
    Given a "Helpdesk Operator" has logged in
      And the "Jobs" tile is selected
      And All teams are selected from Settings
     When the "Watched Jobs" monitor is selected from "For Info" section
     Then the "Watched Jobs" monitor will display the watched jobs with watched icon
      And the "Watched Jobs" monitor will not display any job that is not being watched
      And the "Watched Jobs" monitor will not display any "Fixed" jobs