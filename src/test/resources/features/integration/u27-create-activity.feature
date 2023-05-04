Feature: U27 - Create Activity

  Scenario: AC1: Given I am anywhere on the system, when I click on a UI element to create an activity, then I see a form to create an activity.
    Given I'm on the home page
    And There is a user called "Test" "Account"
    When I click on create activity in the nav bar
    Then I'm taken to the create activity page

  Scenario Outline: AC2: Given I am on the create activity page and I enter valid values for the team it relates to (optional), the
  activity type, a short description and the activity start and end time, when I hit the create activity button,
  then an activity is created into the system and I see the details of the activity
    Given I'm on the create activity page
    And There is a user called "Test" "Account"
    And I have at least 1 team I coach or manage
    When I select <activityType> and <team>, and enter a valid description <description> and I select a valid <start> and <end> date time and press submit
    Then An activity is created
    Examples:
    | activityType | team | description | start | end |
    | "Game" | 1          | "Game with team" | "2023-06-08 01:30" | "2023-06-08 02:30" |
    | "Other" | -1          | "Meeting with team"  | "2023-07-08 05:30" | "2023-07-08 07:30" |

  Scenario: AC3: Given I am on the create activity page, when I am asked to select the team the activity relates to,
    then I can select any of the teams I am managing or coaching, or no team if the activity does not involve a team
      Given I'm on the create activity page
      And There is a user called "Test" "Account"
      And I have at least 1 team I coach or manage
      When I select a team for the activity
      Then I see all the teams I coach and manage and a no team option

  Scenario: AC4: Given I am on the create activity page, when I am asked to select the activity type, then I can
      select from: "game", "friendly", "training", "competition", "other".
        Given I'm on the create activity page
        And There is a user called "Test" "Account"
        When I select an activity type
        Then I see activity type options "game", "friendly", "training", "competition", "other"

  Scenario: AC5: Given I select "game" or "friendly" as the activity type, when I do not select a team and I hit the
  create activity button, then an error message tells me I must select a team for that activity type
    Given I'm on the create activity page
    And There is a user called "Test" "Account"
    And I have at least 1 team I coach or manage
    When I select "game" or "activity" and I don't select a team
    Then I see an error message on the page

