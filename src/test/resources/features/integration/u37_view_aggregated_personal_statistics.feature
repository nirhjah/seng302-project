#Feature: U37 – View aggregated personal statistics
#
#  Scenario: When viewing my profile, I can also see aggregated statistics for all sports I do or teams I belong to.
#    Given I am on my profile page
#    When I click on a UI element to view my aggregated personal statistics
#    Then I will see the statistics of all the teams I belong to and of sports I do
#
#
#  Scenario: For each individual sport, I can see the list of activities recorded with their statistics as visible in the view activity statistics.
#    Given I am viewing my aggregated personal statistics for all sports I do
#    When I click on a UI element for a single sport
#    Then I am shown a list of activities for that sport with their statistics
#
#
#  Scenario:  For each team I belong to, I can see my personal aggregated statistics, i.e. how many goals I scored, how much time I played overall, how much time in average per match, how many matches compared to all matches that team played.
#    Given I belong to a team
#    When I view the team I belong to
#    Then I can see my aggregated personal statistics of that team including how many goals I scored, my overall playing time, my time in average per match and how many matches I played compared to the matches the team played.
#
#  Scenario: For each team I belong to, I can see my personal statistics for the activities of that team, including how much time I played, how many goals I scored (with the time) and whether I substituted or was substituted (with the time).
#    Given I am viewing a team I belong to
#    When I click on a UI element
#    Then I can see my personal statistics for the activities of that team, including my overall play time, how many goals I scored (with the time) and whether I was substituted (with the time).
#
#  Scenario: For each activity, I can access to the full details of that activity.
#    Given I am viewing my aggregated personal statistics with all activities
#    When I click a UI element on an activity
#  Then I am shown the view activity page
#
#
#
#
