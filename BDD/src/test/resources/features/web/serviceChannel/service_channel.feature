@serviceChannel
Feature: Service Channel

  @serviceChannelJobs_OutboundWorkOrder
  Scenario: Create Work Orders for Mercury jobs
    Given all test city database tables have been purged
      And a list of jobs without a Work Order on Service Channel
     When the job is sent to the job logged event injection API
     Then the database queue has been updated correctly

  # this scenario runs from Jenkins and is built with parameters
  @serviceChannelJobs_OutboundReactive
  Scenario: Reactive jobs created in Mercury are shown correctly in Service Channel
     Given a system property for "NumberOfJobs"
      When "Reactive" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Reactive" jobs are displayed correctly on Service Channel
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  @serviceChannelJobs_OutboundRegression
  Scenario: Reactive jobs created in Mercury are shown correctly in Service Channel
     Given system property "NumberOfJobs" is "1"
      When "Reactive" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Reactive" jobs are displayed correctly on Service Channel
     
  # this scenario runs from Jenkins and is built with parameters
  @serviceChannelJobs_OutboundQuote
  Scenario: Quote jobs created in Mercury are shown correctly in Service Channel
     Given a system property for "NumberOfJobs"
      When "Quote" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Quote" jobs are displayed correctly on Service Channel
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  @serviceChannelJobs_OutboundRegression
  Scenario: Quote jobs created in Mercury are shown correctly in Service Channel
     Given system property "NumberOfJobs" is "1"
      When "Quote" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Quote" jobs are displayed correctly on Service Channel
      
  # this scenario runs from Jenkins and is built with parameters
  @serviceChannelJobs_OutboundMultiQuote
  Scenario: Multi-Quote jobs created in Mercury are shown correctly in Service Channel
     Given a system property for "NumberOfJobs"
      When "Multi-Quote" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Multi-Quote" jobs are displayed correctly on Service Channel
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  @serviceChannelJobs_OutboundRegression
  Scenario: Multi-Quote jobs created in Mercury are shown correctly in Service Channel
     Given system property "NumberOfJobs" is "1"
      When "Multi-Quote" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "Multi-Quote" jobs are displayed correctly on Service Channel
      
  # this scenario runs from Jenkins and is built with parameters
  @serviceChannelJobs_OutboundPPM
  Scenario: PPM jobs created in Mercury are shown correctly in Service Channel
     Given a system property for "NumberOfJobs"
      When "PPM" jobs are created in Mercury
       And the job is sent to the job logged event injection API
      Then the "PPM" jobs are displayed correctly on Service Channel
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  @serviceChannelJobs_OutboundRegression
  Scenario: PPM jobs created in Mercury are shown correctly in Service Channel
     Given system property "NumberOfJobs" is "1"
      When "PPM" jobs are created in Mercury
       And the job is sent to the ppm logged event injection API
      Then the "PPM" jobs are displayed correctly on Service Channel
      
  # this scenario runs from Jenkins and is built with parameters
  @serviceChannelJobs_CombinationMapping
  Scenario: Combination mapping jobs are displayed correctly in Service Channel mapping table
     Given a system property for "Environment"
       And an IT user has logged in
      When jobs are created using the combination mapping spreadsheet
      Then the mapping is displayed correctly for each job
     
  # same as above - but with properties fed in from given steps instead of from jenkins job
  # to run this scenario locally you will need to map jenkins folder to your Y drive
  Scenario: Combination mapping jobs are displayed correctly in Service Channel mapping table
     Given system property "Environment" is "dev_uswm"
       And an IT user has logged in
      When jobs are created using the combination mapping spreadsheet
      Then the mapping is displayed correctly for each job