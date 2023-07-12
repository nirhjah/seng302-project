@create_formation
Feature: U32 – Create formation
  Scenario: AC1 - All formations
    Given I am on my team’s profile
    When I click on a UI element to see all the team’s formations
    Then I see a list of all formations for that team

  Scenario: AC2 - Graphical display for formation
    Given I am on my team’s formation page
    When I click on a UI element to create a new line-up
    Then I see a graphical representation of a sport pitch corresponding to the sport of that team

  Scenario: AC4 - Error message on invalid formation
    Given I set up the number of players per sector
    When the number of players per sector is invalid (I.e. does not respect the pattern of a number followed by a dash except for the last number), or is empty,
    Then an error message tells me that the formation is invalid.

  Scenario: AC5 - Formation
    Given I am on the formation creation page
    When I specify a number of players per sector on the pitch in the form of dash separated numbers starting from the back line up to the front line on the pitch
    Then a formation is generated that matches the formation string

  Scenario: AC6 - Perist Formation
    Given I have correctly set up a formation with the number of players per sector
    When I click on the create formation button
    Then the formation is persisted in the system

