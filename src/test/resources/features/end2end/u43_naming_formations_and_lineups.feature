@naming_formations_and_lineups

Feature: U43 Naming Formations and Lineups
  Scenario: I can name lineups
    Given I name a lineup that I am creating
    When the lineup is saved
    Then the lineup displays with its name anywhere the lineup is displayed

    Scenario: I can name lineups
      Given I am creating a lineup
      When I give a lineup no name and create the formation
      Then the lineup has a default name based on the activity date and formation