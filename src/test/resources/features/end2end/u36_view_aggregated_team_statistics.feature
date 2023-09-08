#@view_aggregated_team_statistics
#Feature: U36 – View aggregated team statistics
#
#  Scenario: AC1 When I am looking at a team, I can see the teams statistics
#    Given I am on the team profile page
#    When I click on a UI element to view the aggregated team statistics
#    Then I will see the team statistics
#
#
#  Scenario: AC1: When looking at a team, I can see how many games that team has played, won, lost, and drew overall (including friendlies).
#    Given I see the aggregated team statistics
#    And there are activities (game or friendly)  that have won, lost or drew overall,
#    Then I can see a total of how many games or friendlies the team has played, won, lost and drew overall
#
#
#  Scenario: AC2: I can see a trend of the last 5 matches in terms of game and friendly won, lost and drew. Friendlies are visually distinguishable from games in that trend.
#    Given I have at least 5 activities
#    And they are of either type Game or Friendly
#    When I am viewing team aggregated statistics
#    Then I can see a trend of the last 5 activities in terms of win, loss and draw
#    And I can see the date and score of the activity
#    And I can click on each activity to be taken to that activitys page
#
#
##  Left this out of e2e testing because it requires /populate_database to be run to add players to a team, but /populate_database only adds players to the first team available which causes issues for the tests. I have manually tested this AC instead
##  Scenario: AC3: I can see the names of the 5 top scorers with how many goals they scored overall.
##    Given There are at least 5 or more members of the team
##    And they all have scored in the activity
##    When I am viewing team aggregated statistics
##    Then I can see the top 5 scorers with how many goals they scored
##
##
##  Scenario: AC4: I can see the names of the 5 top players who played for the longest overall with their overall play time, and their average play time for a game.
##    Given There are at least 5 or more members of the team
##    When I am viewing the team aggregated statistics
##    Then I can see the top 5 players sorted by overall play time
##    And I can see their average play time too
#
#
#
#  Scenario: AC7: I can access to the list of all activities this team has taken part in.
#    Given I see the aggregated team statistics
#    When I click on the View team activities button
#    Then I will see the list of activities this team has taken part in
#
#
#
#
#
