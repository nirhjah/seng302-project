#@create_activity
#  Scenario: AC1:
#    Given I am anywhere on the system,
#    When I click on a UI element to create an activity,
#    Then I see a form to create an activity.
#
#
#  Scenario: AC2:
#    Given I am on the create activity page
#    And I enter valid values for the team it relates to (optional), the activity type, a short description and the activity start and end time,
#    When I hit the create activity button, then an activity is created into the system
#    And I see the details of the activity.
#
#  Scenario: AC3:
#    Given I am on the create activity page,
#    When I am asked to select the team the activity relates to,
#    Then I can select any of the teams I am managing or coaching, or no team if the activity does not involve a team.
#
#  Scenario: AC4:
#    Given I am on the create activity page,
#    When I am asked to select the activity type,
#    Then I can select from: “game”, “friendly”, “training”, “competition”, “other”.
#
#  Scenario: AC5:
#    Given I select “game” or “friendly” as the activity type,
#    When I do not select a team and I hit the create activity button,
#    Then an error message tells me I must select a team for that activity type.
#
#  Scenario: AC6:
#    Given I do not select the activity type,
#    When I hit the create activity button,
#    Then an error message tells me I must select an activity type.
#
#  Scenario: AC7:
#    Given I enter an empty description, a description made of numbers or non-alphabetical characters only, or a description longer than 150 characters,
#    When I hit the create activity button,
#    Then an error message tells me the description is invalid.
#
#
#  Scenario: AC8:
#    Given I do not provide both a start and an end time,
#    When I hit the create activity button,
#    Then an error message tells me the start and end time are compulsory.
#
#  Scenario: AC9:
#    Given I enter the activity start and end time,
#    And I enter an end time before the start time,
#    When I hit the create activity button,
#    Then an error message tells me the end date is before the start date.
#
#  Scenario: AC10:
#    Given I enter the activity start and end time,
#    And I enter either a start or an end time before the team creation date,
#    When I hit the save button, when I hit the create activity button,
#    Then an error message tells me the dates are prior the team’s creation date and the team’s creation date is shown.
#
#
#
#
