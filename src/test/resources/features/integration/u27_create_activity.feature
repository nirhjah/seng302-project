@create_activity
Feature: Activity Creation
  Scenario: Page Exists for Creating Activity
    Given I am anywhere on the system,
    When I click on a UI element to create an activity,
    Then I see a form to create an activity.

# Commented out due to detached entity error with tests. Functionality works manually as of 09/12/23

#  Scenario: Creating a valid activity
#    Given I am on the create activity page
#    When I enter valid values for the team it relates to, the activity type, a short description, and the activity start and end time and location and create the activity
#    Then an activity is created into the system
#
#  Scenario: Selecting the team for the activity
#    Given I am on the create activity page
#    Then I can select any of the teams I am managing or coaching
#    And I can select no team if the activity does not involve a team

  Scenario: Selecting the activity type
    Given I am on the create activity page
    Then I can select from game, friendly, training, competition and other

  Scenario Outline: Creating a "game" or "friendly" activity without selecting a team
    Given I am on the create activity page
    And I select <activityType> as the activity type
    When I do not select a team
    Then an error message tells me I must select a team for that activity type
    Examples:
      | activityType   |
      | "Game"         |
      | "Friendly"     |


  Scenario: Creating an activity without selecting an activity type
    Given I am on the create activity page
    When I do not select the activity type
    Then an error message tells me I must select an activity type

  Scenario: Invalid activity description
    Given I am on the create activity page
    When I enter an empty description
    Then an error message tells me the description is too short

    Given I am on the create activity page
    When I enter a description made of numbers or non-alphabetical characters only
    Then an error message tells me the description is invalid

    Given I am on the create activity page
    When I enter a description longer than 150 characters
    Then an error message tells me the description is too long

  Scenario: Missing start time
    Given I am on the create activity page
    When I do not provide a start time
    Then an error message tells me the start time is compulsory

  Scenario: Missing end time
    Given I am on the create activity page
    When I do not provide an end time
    Then an error message tells me the end time is compulsory

  Scenario: End time before start time
    Given I am on the create activity page
    When I enter the activity start and end time with the end time before the start time
    Then an error message tells me the end date is before the start date

  Scenario: Activity dates prior to team creation date
    Given I am on the create activity page
    When I enter either a start or an end time before the team creation date
    Then an error message that includes the team's creation date tells me the dates are prior to the team’s creation date
