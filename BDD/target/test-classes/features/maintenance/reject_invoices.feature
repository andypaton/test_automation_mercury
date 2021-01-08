@maintenance @rejectInvoices
Feature: Reject Invoices

  Scenario Outline: Reject "<state>" invoices
    Given all invoices at state "<state>"
     When all invoices except the latest "3" for each supplier are updated to rejected 
    Examples:
      | state               |
      | Awaiting Approval   |
