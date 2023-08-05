@Add_activity_statistics
Feature: U34 – Add activity statistics
#  Scenario: AC1: Score added to the team is in same format for both teams
#    Given I have created a activity type that is a game or friendly,
#    When I add a score to each team,
#    Then it must be in the same format, i.e. if team A receives a score “3”, then team B receives a score “1”, or if team A receives a score “141-1” then team B receives a score “138-8”
#
#
#  Scenario: AC1: User cannot add scores of different formats
#    Given I have created a activity type that is a game or friendly
#    And I have given Team A a score value of in the format “4”
#    When I give Team B’s format in the score “123-4”
#    Then an error appears telling me that both teams require their scores in the same format
#
#
#  Scenario: AC1: User needs to specify person who scored and time of score in statistics
#    Given there is an activity type of game or friendly
#    And I am adding statistics for it.
#    When I am adding a score,
#    Then I must specify the person who scored
#    And the time it occurred.
#
#
#  Scenario: AC2: Person who scored must be a member of the team when adding a score
#    Given I am adding a score,
#    When I select the person who scored,
#    Then they must be a member of the team, who is not a coach
#
#  Scenario: AC2: The time of score must be within timing of the game when adding a score
#    Given I am adding a score,
#    When I specify a time that it occurred
#    Then it must be within the timing of the game. (eg. 40 minutes or 1h 20m)
#
#  Scenario: AC2: The time is in the correct format when adding a score
#    Given I am adding a score
#    When I specify a time,
#    Then it is given in the format of the hours and minutes into a game
#
  Scenario: AC3: Player and time must be specified when adding substitution
    Given there is an activity type of game or friendly
    When I am adding a substitution,
    Then I must specify the player who was taken off, the one who was put on and the time that this occured.


#  Scenario: AC3: Person who scored must be in team  when adding substitution
#    Given I am adding a substitution,
#    When I select the person who scored
#    Then  they must be a member of the team
#
#  Scenario: AC3: Time of substitution should be within timing of game when adding substitution
#    Given I am adding a substitution,
#    When I specify a time for the substitution
#    Then it must be within the timing of the game.
#
#
#  Scenario: AC3: The time is in the correct format when adding a substitution
#    Given I am adding a substitution,
#    When I specify a time for the substitution
#    Then it is the amount of hours, minutes, seconds into the activity
#
#  Scenario: AC4: A description should be specified when adding a fact
#    Given There is an activity type that is not training
#    When I am adding a fact
#    Then I must specify a description and optionally a time at when the fact took place
#
#  Scenario: AC4: Time of fact should be within timing of game when adding fact
#    Given I am adding a fact
#    When I specify a time that it occurred
#    Then it must be within the timing of the game
#
#  Scenario: AC4: The time is in the correct format when adding a fact
#    Given I am adding a fact
#    When if I specify a time
#    Then is it the amount of hours, minutes, seconds into the activity





