Feature: U25 - Join a Team

  Scenario: AC1 Given I am anywhere on the system, when I click on a dedicated UI element to see the teams I am a member of, then I can choose to join a new team.
    Given I am on the home page
    When I click the my teams button
    Then I see the my teams page


  Scenario: AC2 Given I am being showed an input to join a new team, when I input an invitation token that is associated to a team in the system, then I am added as a member to this team.
    Given I am on the my teams page
    When I input a valid team invitation token
    Then I am added as a member to that team


  Scenario: AC3 Given I am being showed an input to join a new team, when I input an invitation token that is not associated to a team in the system, then an error message tells me the token is invalid.
    Given I am on the my teams page
    When I input an invalid team invitation token
    Then An error message tells me the token is invalid


  Scenario: AC4 Given I have joined a new team, when I click on a dedicated UI element to see the teams I am a member of, then I see the new team I just joined.
    Given I have joined a new team
    When I click the my teams button
    Then I see the team I just joined