Feature: U36 – View aggregated team statistics

  Scenario: AC1: When looking at a team, I can see how many games that team has played, won, lost, and drew overall (including friendlies).
    Given I am on the team profile page
    When I click on a UI element to view the aggregated team statistics
    Then I will see the team statistics


  Scenario: AC1: When looking at a team, I can see how many games that team has played, won, lost, and drew overall (including friendlies).
    Given I see the aggregated team statistics
    When There are 5 matches or more recorded for game and friendly
    Then I will see a section that displays the trend of the matches’ outcomes i.e win, lost, drew of the last 5 matches
    And I can visually distinguish friendlies from games in that trend


  Scenario: AC2: I can see a trend of the last 5 matches in terms of game and friendly won, lost and drew. Friendlies are visually distinguishable from games in that trend.
    Given I see the aggregated team statistics
    When there are less than 5 matches recorded for game and friendly
    Then I will see a section that displays the trend of the matches’ outcome i.e. win, lost, drew of the number of matches
    And I can visually distinguish friendlies from games in that trend


  Scenario: AC2: I can see a trend of the last 5 matches in terms of game and friendly won, lost and drew. Friendlies are visually distinguishable from games in that trend.
    Given I see the aggregated team statistics
    When there are  5 scorers or more
    Then I will see a section that lists the 5 top scorers names with how many goals they scored overall


  Scenario: AC3: I can see the names of the 5 top scorers with how many goals they scored overall.
    Given I see the aggregated team statistics,
    When there are less than 5 scorers
    Then I will see a section that lists the number of scorers names with how many goals they scored overall

  Scenario: AC3: I can see the names of the 5 top scorers with how many goals they scored overall.
    Given I see the aggregated team statistics,
    When there are 5 players or more
    Then I will see a section that lists the names of the 5 top players who played for the longest overall and their overall play time,
    And their average play time for a game will be displayed alongside their name.


  Scenario: AC4: I can see the names of the 5 top players who played for the longest overall with their overall play time, and their average play time for a game.
    Given I see the aggregated team statistics,
    When there are less than 5 players,
    Then I will see a section that lists the names of the number of top players who played for the longest overall and their overall play time,
    And their average play time for a game will be displayed alongside their name.


  Scenario: AC4: I can see the names of the 5 top players who played for the longest overall with their overall play time, and their average play time for a game.
    Given I see the aggregated team statistics
    When there are more than 5 activities
    Then I will see a section that displays the results of the last 5 activities with scores (i.e. friendlies and games)
    And their dates and scores will also be displayed alongside it

  Scenario: AC5: I can see the results of the 5 last activities with scores (I.e. friendlies and games), with their dates and scores.    Given I see the aggregated team statistics
    When there are less than 5 activities
    Then I will see a section that displays the results of the number of activities with scores(i.e. friendlies and games)
    And their dates and scores will also be displayed alongside it

  Scenario: AC6: I can see the results of the 5 last activities with scores (I.e. friendlies and games), with their dates and scores.
    Given I see the last activities section of the aggregated team statistics
    When I click on one of the last activities displayed
    Then I will see the details of that activity

  Scenario: AC7: I can access to the list of all activities this team has taken part in.
    Given I see the aggregated team statistics,
    When I click on the View team activities button
    Then I will see the list of activities this team has taken part in





