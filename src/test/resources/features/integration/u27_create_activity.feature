@create_activity
Feature Activity Creation:
  Scenario Page Exists for Creating Activity:
    Given I am anywhere on the system,
    When I click on a UI element to create an activity,
    Then I see a form to create an activity.

  Scenario: Creating a valid activity
    Given I am on the create activity page
    When I enter valid values for the team it relates to, the activity type, a short description, and the activity start and end time and location and create the activity
    Then an activity is created into the system
    And I see the details of the activity

  Scenario: Selecting the team for the activity
    Given I am on the create activity page
    When I am asked to select the team the activity relates to
    Then I can select any of the teams I am managing or coaching
    And I can select no team if the activity does not involve a team

  Scenario: Selecting the activity type
    Given I am on the create activity page
    When I am asked to select the activity type
    Then I can select from: "game", "friendly", "training", "competition", "other"

  Scenario: Creating a "game" or "friendly" activity without selecting a team
    Given I am on the create activity page
    And I select "game" or "friendly" as the activity type
    And I do not select a team
    When I hit the create activity button
    Then an error message tells me I must select a team for that activity type

  Scenario: Creating an activity without selecting an activity type
    Given I am on the create activity page
    When I do not select the activity type
    When I hit the create activity button
    Then an error message tells me I must select an activity type

  Scenario: Invalid activity description
    Given I am on the create activity page
    And I enter an empty description
    When I hit the create activity button
    Then an error message tells me the description is invalid

    Given I am on the create activity page
    And I enter a description made of numbers or non-alphabetical characters only
    When I hit the create activity button
    Then an error message tells me the description is invalid

    Given I am on the create activity page
    And I enter a description longer than 150 characters
    When I hit the create activity button
    Then an error message tells me the description is invalid

  Scenario: Missing start and/or end time
    Given I am on the create activity page
    And I do not provide both a start and an end time
    When I hit the create activity button
    Then an error message tells me the start and end time are compulsory

  Scenario: End time before start time
    Given I am on the create activity page
    And I enter the activity start and end time
    And I enter an end time before the start time
    When I hit the create activity button
    Then an error message tells me the end date is before the start date

  Scenario: Activity dates prior to team creation date
    Given I am on the create activity page
    And I enter the activity start and end time
    And I enter either a start or an end time before the team creation date
    When I hit the create activity button
    Then an error message tells me the dates are prior to the team’s creation date
    And the team’s creation date is shown