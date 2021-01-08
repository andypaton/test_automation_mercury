@deprecated @wip
@maintenance
Feature: add a new users to %testdb.TestAutomation_Users that can be used by the test automation scripts

############## NOTE: feature no longer required. Login using Active user from ApplicationUser with IT user profile

#note: users of type API_USER should have an IT resource profile and will only be user for making API calls

  @addUsers
  Scenario Outline: add user <username> details
    Given an "<type>" with username "<username>" and password "<password>" or encrypted password "<encryptedPassword>"
     When the user details are added to %testdb.TestAutomation_Users
     Then the user details exist in %testdb.TestAutomation_Users with an encrypted password
    Examples: 
      | username         | password  | encryptedPassword | type    | 
      | andrew.paton     | Pa55w0rd  |                   | Mercury | 
      | marcin.oleszczuk | Pa55w0rd  |                   | API     |
      | sonal.yede       | Pa55w0rd  |                   | Mercury |
      | parnika.kilaru   | Pa55w0rd  |                   | Mercury |
      | jenkins.user1    | Pa55w0rd  |                   | Mercury |
      | jenkins.user2    | Pa55w0rd  |                   | Mercury |
      | jenkins.user3    | Pa55w0rd  |                   | Mercury |
      | jenkins.user4    | Pa55w0rd  |                   | Mercury |
      | jenkins.user5    | Pa55w0rd  |                   | Mercury |
      | jenkins.user6    | Pa55w0rd  |                   | Mercury |
      | jenkins.user7    | Pa55w0rd  |                   | Mercury |
      | jenkins.user8    | Pa55w0rd  |                   | Mercury |
      | jenkins.user9    | Pa55w0rd  |                   | Mercury |
      | jenkins.user10   | Pa55w0rd  |                   | Mercury |

  @updatePassword
  Scenario Outline: update user <username> password 
    Given username "<username>" and password "<password>" or encrypted password "<encryptedPassword>"
     When the user details are updated on %testdb.TestAutomation_Users
     Then the user details exist in %testdb.TestAutomation_Users with an encrypted password
    Examples: 
      | username         | password  | encryptedPassword |
      | andrew.paton     | Pa55w0rd1 |                   |

  @updateUsers
  Scenario Outline: update user <username> to be <active>
    Given username "<username>" is updated to be "<active>"
     When the user details are updated on %testdb.TestAutomation_Users
     Then the user details exist in %testdb.TestAutomation_Users with an encrypted password
    Examples: 
      | username     | active     |
      | andrew.paton | not active |
      | andrew.paton | active     |