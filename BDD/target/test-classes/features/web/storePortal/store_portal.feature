#Remove @ukrb tag once MCP-17830 is resolved
@store_portal
@ukrb 
Feature: Store Portal - Chrome

  Scenario: Homepage is displayed as expected
    Given an active Store Portal user
     When the user has logged in
     Then the Homepage is displayed as expected
     
  Scenario: Calendar function works as expected
    Given an active Store Portal user
      And the user has logged in
     When the Calendar menu is selected
     Then the Calendar function works as expected
     
  Scenario: Days on the Calendar with no Jobs scheduled display message
     Given an active Store Portal user
      And the user has logged in
     When the Calendar menu is selected
      And a day with no scheduled jobs is selected
     Then the message "No engineer visits scheduled for today" is displayed on the timeline
     
  @notsignedoff
  Scenario: ETA Jobs are displayed correctly on the Calendar
    Given a Job with ETA for the current month
      And the user has logged in
     When the Calendar menu is selected
      And the date of the Job is selected
     Then the "ETA" Job details are displayed correctly
     
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
     Then the "In Progress / Returning" Job contains the message: "@Resource is @Status and has confirmed an ETA of @EtaDate between @EtaTime"
     
  Scenario: View a Job - Resource Awaiting Parts
    Given a Job is logged with Resource Awaiting Parts
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is viewed
     Then the "In Progress / Awaiting Parts Review" Job contains the message: "@Resource is @Status and has confirmed an ETA of @EtaDate between @EtaTime"
     
  Scenario: View a Job - Resource is Complete
    Given a Job is logged with Resource Complete
      And the user has logged in
     When "Closed" Tile is selected
      And the Job is viewed
     Then the "Fixed / Complete" Job contains the message: "Your job was fixed by @Resource on @JobCompletionDate at @JobCompletionTime"
     
  Scenario: Watch a Job
    Given an active Store Portal user
      And the user has logged in
     When "Random" Tile is selected
      And a non watched Job is viewed
      And the Job is selected to watch
      And "Watched" Tile is selected
     Then the Job "is" present in the Watched Job list
      And the Job "is" on the Watched Jobs monitor
      
  Scenario: Unwatch a Job
    Given a Watched Job
      And the user has logged in
     When "Watched" Tile is selected
      And the Job is viewed
      And the Job is unselected to watch
      And "Watched" Tile is selected
     Then the Job "is not" present in the Watched Job list
      And the Job "is not" on the Watched Jobs monitor
     
  Scenario: Watched Jobs are displayed correctly
    Given a Job has been logged
      And the user has logged in
     When "Reactive" Tile is selected
      And a non watched Job is viewed
      And the Job is selected to watch
      And "Watched" Tile is selected
     Then the Job details are displayed correctly for tile "Watched"
     
  Scenario: Onsite Jobs are displayed correctly
    Given a Job is logged with Resource On Site
      And the user has logged in
     When "Onsite" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Onsite"
     
  Scenario: Reactive Jobs are displayed correctly
    Given a Job has been logged
      And the user has logged in
     When "Reactive" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Reactive"
     
  Scenario: PPM Jobs are displayed correctly
    Given an active PPM Job
      And the user has logged in
     When "PPM" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "PPM"
    
  Scenario: Quote Jobs are displayed correctly
    Given a Quote Job has been logged
      And the user has logged in
     When "Quotes" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Quotes"
     
  Scenario: Closed Jobs are displayed correctly
    Given a Job is logged with Resource Complete
      And the user has logged in
     When "Closed" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Closed"
     
  Scenario: Responses are displayed correctly
    Given a Job with Feedback Response
      And the user has logged in
     When "Notifications" Tile is selected
      And "Responses" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Responses"
     
  Scenario: Pending Responses are displayed correctly
    Given a Fixed Job with no previous feedback
      And the user has logged in
      And negative feedback is given on a fixed job
     When "Notifications" Tile is selected
      And "Pending" Tile is selected
      And the Job is searched for
     Then the Job details are displayed correctly for tile "Pending"
     
  Scenario: Log a Job
    Given an active Store Portal user
      And the user has logged in
     When the Log a Job menu is selected
      And a Job is Logged
     Then the Job is present in the Reactive list
     
  Scenario: Log a Duplicate Job
    Given an active Store Portal user
      And the user has logged in
     When the Log a Job menu is selected
      And a Job is Logged
      And a Duplicate Job is created
     Then the first Job will be shown as a potential duplicate
