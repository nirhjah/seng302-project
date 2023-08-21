#@add_activity_stats
#Feature: U34 – Add activity statistics
#
#  Scenario: AC1
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When the activity has begun
#    Then I am able to add an overall score, and record any substitutions or goals through a dedicated UI element for each.
#
#  Scenario: AC2
#    Given I am a manager or coach,
#    And I am viewing an activity of any type
#    When the activity has begun
#    Then I am able to record facts through a dedicated UI element.
#
#  Scenario: AC3
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When the activity has ended,
#    Then I am able to add an outcome for the overall activity through a dedicated UI element
#
#  Scenario: AC4
#    Given I am a manager or coach,
#    And I am viewing an activity of the type “Game” or “Friendly”
#    When the activity has begun
#    And I am adding a goal, substitution or fact
#    And I specify a time
#    Then the time must fall within the bounds of the activity (ie cannot be before the beginning or after the end)
#
#  Scenario: AC5
#    Given I am a manager or coach
#    And I am viewing an activity of the type “Game” or “Friendly”
#    When the activity has begun
#    And I am adding a goal, substitution or fact
#    And I am specifying a team member to be associated with the statistics
#    Then the I can only specify players (not managers or coaches)
#
#  Scenario: AC6
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When the activity has begun
#    And there is a current overall score
#    Then I am able to update the overall score again
#
#  Scenario: AC7
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When I am adding the overall score
#    And I enter the score in the format “5” for Team A and “7” for Team B, or “5-6” for Team A and “7-6” for Team B
#    Then the application accepts the scores as the format matches
#
#
#  Scenario: AC8
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When I am adding the overall score
#    And I enter the score in the format “5” for Team A and “7-6” for Team B
#    Then the application doesn’t accept the scores as the format doesn’t match and an error message displays telling the user that “Error: The score formats do not match”
#
#  Scenario: AC9
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    And the activity has begun
#    When I click the UI element to add a goal
#    Then there is a form with the required field of scorer and time it occurred and the optional fields description and value.
#
#  Scenario: AC10
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    And the activity has begun
#    And I am adding a score
#    When I don’t enter a value into the goal value field (and rest of form is right)
#    Then the system accepts and uses a default value of 1
#
#  Scenario: AC11
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    And the activity has begun
#    And I am adding a score
#    When I enter a positive integer (given rest of form is valid)
#    Then the system accepts
#
#  Scenario: AC12
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    And the activity has begun
#    And I am adding a score
#    When I enter a value that is not a positive integer
#    Then the system doesn’t accept and an error message displays telling the user to enter a positive integer.
#
#  Scenario: AC13
#    Given I am a manager or coach,
#    And I am viewing an activity of the type ‘Game’ or ‘Friendly’
#    When the activity has begun
#    And I am adding a substitution
#    Then I must fill out the required fields of time, player on and player off, and optionally fill out the description field
#
#  Scenario: AC14
#    Given I am a manager or coach,
#    And I am viewing an activity of any type
#    When the activity has begun
#    And I am adding a fact about the activity
#    Then I must fill out the required field of description and optionally the time it occurred.
#
