@helpdesk
Feature: Helpdesk - Smoke Test - Warm up
 
  @warmup @mcp
  Scenario: open url and wait before starting a smoke test suite
    Given an application url has been opened
      And system waits for "1" minute
      And login page is displayed correctly
     When notifications url has been opened
      And system waits for "1" minute
     Then notification page is displayed correctly