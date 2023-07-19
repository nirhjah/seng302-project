@ViewActivityStatistics
Feature: U35 – View activity statistics
  Scenario: AC1: Statistics of activity shown when viewing activity
    Given I am viewing my activities or team activities
    When I click on an activity
    Then I can see the details of that activity together with its statistics


  Scenario: AC2: Time player scored shown on line up on statistics page
    Given I am viewing an activity that is a game or friendly with lineups
    When there are statistics about scoring players
    Then I can see the time that player scored next to their icon on the line-up

  Scenario: AC3: Information about substitution of player shown on statistics page
    Given I am viewing an activity that is a game or friendly with lineups
    When there are statistics about substitute players
    Then I can see the icon, name and time of substitution of the player

  Scenario: AC4: For all statistics and facts with times, they are listed and sorted by their time in ascending order.
    Given I am viewing an activity
    When that activity has statistics and facts with times
    Then they are listed and sorted by their time in ascending order





