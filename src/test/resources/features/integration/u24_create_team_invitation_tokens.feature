@create_team_invitation_tokens
Feature: U24 Create Team Invitation Token
  Scenario: AC1  When I am on the team profile page of a team I manage, then I can see a unique secret token for my team that is exactly 12 char long with a combination of letters and numbers, but no special characters.
    Given I manage a team
    When I am viewing the profile page of that team
    Then I can see a unique secret token for my team that is exactly 12 char long with a combination of letters and numbers, but no special characters


  Scenario: AC2 Given I am on the team profile page, when I generate a new secret token for my team, then a new token is generated, and this token is unique across the system, and that token is not a repeat of a previous token.
    Given I am on the team profile page
    When I generate a new secret token for my team
    Then a new token is generated, and this token is unique across the system and is not a repeat of the previous token