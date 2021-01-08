@adhoc
Feature: Compare monitor counts between deployments

  @captureBaselineMonitorCounts
  Scenario: capture baseline counts
    Given a Helpdesk user has logged in
     When all monitor counts are captured
     Then a file is created with monitor counts 
     
     
  @compareMonitorCounts
  Scenario: compare monitor counts against baseline counts
#    Given system property "baselineMonitorCountFile" is "/tmp/6.2.0.7729_20200422T1030.txt"
    Given a system property for "baselineMonitorCountFile"
      And a Helpdesk user has logged in
     When all monitor counts are captured
     Then the monitor counts are compared against the baseline
