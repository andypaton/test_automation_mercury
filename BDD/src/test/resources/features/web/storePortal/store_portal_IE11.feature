#Tagging this feature as a wip as until MCP-17830 is resolved, we cannot use CSS to find elements and these scenarios will not run correctly in Jenkins
@store_portal
@internetexplorer @ukrb
@wip
Feature: Store Portal - Internet Explorer

  Scenario: Homepage is displayed as expected
    Given an active Store Portal user
     When the user has logged in
     Then the Homepage is displayed as expected
     
  Scenario: Calendar function works as expected
    Given an active Store Portal user
      And the user has logged in
     When the Calendar menu is selected
     Then the Calendar function works as expected
     
  @notsignedoff
  Scenario: Days on the Calendar with no Jobs scheduled display message
     Given an active Store Portal user
      And the user has logged in
     When the Calendar menu is selected
      And a day with no scheduled jobs is selected
     Then the message "No engineer visits scheduled for today" is displayed on the timeline
     
  @notsignedoff @bugRainbow
  Scenario: ETA Jobs are displayed correctly on the Calendar [bug: MCP-16696]
    Given a Job with ETA for the current month
      And the user has logged in
     When the Calendar menu is selected
      And the date of the Job is selected
     Then the "ETA" Job details are displayed correctly
     
  @notsignedoff
  Scenario: PPM Jobs are displayed correctly on the Calendar
    Given a PPM Job with a due date for the current month
      And the user has logged in
     When the Calendar menu is selected
      And the date of the Job is selected
     Then the "PPM" Job details are displayed correctly
     
  @notsignedoff
  Scenario: Contact Us displays the correct details [task: MCP-17830]
    Given an active Store Portal user
      And the user has logged in
     When the Contact Us menu is selected
     Then the Contact Us details are correct
     
  Scenario: Job details are displayed correctly
    Given an active Store Portal user
      And the user has logged in
     When "Random" Tile is selected
      And the Job is viewed
     Then the Job details are displayed correctly

  Scenario: View a Logged Job - Contact Us
    Given a Job has been logged
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the user can add "Contact Us" details and history is avaialable
     
  Scenario: View a Fixed Job - Feedback
    Given a Fixed Job with no previous feedback
      And the user has logged in
     When "Closed" Tile is selected
      And the Job is viewed
     Then the user can add "Feedback" details and history is avaialable
     
  Scenario: View a Job - No Resource assigned
    Given a Job has been logged
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "Logged / None Assigned" Job contains the message: "Your job has been logged and we will update you with an ETA as soon as possible"
     
  Scenario: View a Job - Resource assigned but not accepted or provided ETA
    Given a Job is logged with Resource assigned
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "Logged / New Job Notification Sent" Job contains the message: "Your job has been logged and we will update you with an ETA as soon as possible"
     
  Scenario: View a Job - Resource accepted and provided ETA
    Given a Job is logged with Resource accepted and provided ETA
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "Allocated / ETA Provided" Job contains the message: "Your job has been assigned to @Resource who has confirmed an ETA of @EtaDate between @EtaTime"
     
  Scenario: View a Job - Resource is On Site
    Given a Job is logged with Resource On Site
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "In Progress / On Site" Job contains the message: "@Resource is currently on site carrying out the work"
     
  Scenario: View a Job - Resource Returning
    Given a Job is logged with Resource Returning
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "In Progress / Returning" Job contains the message: "@Resource is returning and has confirmed an ETA of @EtaDate between @EtaTime"
     
  Scenario: View a Job - Resource Awaiting Parts
    Given a Job is logged with Resource Awaiting Parts
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "In Progress / Awaiting Parts Review" Job contains the message: "@Resource is returning and has confirmed an ETA of @EtaDate between @EtaTime"
     
  Scenario: View a Job - Resource is Complete
    Given a Job is logged with Resource Complete
      And the user has logged in
     When "Closed" Tile is selected
      And the Job is viewed
     Then the "Fixed / Complete" Job contains the message: "Your job was fixed by @Resource on @JobCompletionDate at @JobCompletionTime"
     
  @notsignedoff @bugRainbow
  Scenario: Watch a Job [bug: MCP-16791]
    Given an active Store Portal user
      And the user has logged in
     When "Random" Tile is selected
      And a non watched Job is viewed
      And the Job is selected to watch
      And "Watched" Tile is selected
     Then the Job "is" present in the Watched Job list
      And the Job "is" on the Watched Jobs monitor
      
  @notsignedoff @bugRainbow
  Scenario: Unwatch a Job [bug: MCP-16791]
    Given a Watched Job
      And the user has logged in
     When "Watched" Tile is selected
      And the Job is viewed
      And the Job is unselected to watch
      And "Watched" Tile is selected
     Then the Job "is not" present in the Watched Job list
      And the Job "is not" on the Watched Jobs monitor
     
  @notsignedoff
  Scenario: Watched Jobs are displayed correctly
    Given an active Store Portal user
      And the user has logged in
     When "Reactive" Tile is selected
      And a non watched Job is viewed
      And the Job is selected to watch
      And "Watched" Tile is selected
     Then the Job details are displayed correctly for tile "Watched"
     
  @notsignedoff
  Scenario: Onsite Jobs are displayed correctly
    Given a Job is logged with Resource On Site
      And the user has logged in
     When "Onsite" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Onsite"
     
  @notsignedoff
  Scenario: Reactive Jobs are displayed correctly
    Given a Job has been logged
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Reactive"
     
  @notsignedoff
  Scenario: PPM Jobs are displayed correctly
    Given an active PPM Job
      And the user has logged in
     When "PPM" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "PPM"
     
  @notsignedoff
  Scenario: Quote Jobs are displayed correctly
    Given a Quote Job has been logged
      And the user has logged in
     When "Quotes" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Quotes"
     
  @notsignedoff
  Scenario: Closed Jobs are displayed correctly
    Given a Job is logged with Resource Complete
      And the user has logged in
     When "Closed" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Closed"
     
  @notsignedoff
  Scenario: Responses are displayed correctly
    Given a Job is logged with Resource accepted and provided ETA
      And the user has logged in
     When "Notifications" Tile is selected
      And "Responses" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Responses"
     
  @notsignedoff
  Scenario: Pending Responses are displayed correctly
    Given a Fixed Job with no previous feedback
      And the user has logged in
      And negative feedback is given on a fixed job
     When "Notifications" Tile is selected
      And "Pending" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Pending"
     
  @notsignedoff
  Scenario: Log a Job
    Given an active Store Portal user
      And the user has logged in
     When the Log a Job menu is selected
      And a Job is Logged
     Then the Job is present in the Reactive list