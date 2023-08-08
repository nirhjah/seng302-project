@create_view_update_competition
 Feature: U39 – Create / view / update competition
Scenario: AC1: Federation administrators are a special type of users and are the only one allowed to create or update competitions
  Given I am a user of account type federation administrator
  When I attempt to access the create a competition page,
  Then I am brought to the create competition page.


  Scenario: AC1: Federation administrators are a special type of users and are the only one allowed to create or update competitions
    Given I am a normal user,
    When I attempt to access the create a competition page,
    Then I am instead shown an error message stating I don’t have valid permissions to access this page.

  Scenario: AC1: Federation administrators are a special type of users and are the only one allowed to create or update competitions
    Given I am a user of account type federation administrator
    When I attempt to access the update a competition page for a competition,
    Then I am brought to the update competition page for that competition.


  Scenario: AC1: Federation administrators are a special type of users and are the only one allowed to create or update competitions
    Given I am a normal user,
    When I attempt to access the update a competition page for a competition,
    Then I am instead shown an error message stating I don’t have valid permissions to access this page.

    Scenario: AC2: A competition has a name, is for a given sport, and is for a certain grade level, e.g., U10 (under 10-year-old), masters, MAG level 3. All are compulsory fields.
      Given I am a user of account type federation administrator
      And I am on the create or update competition page
      And I input valid information for name, sport and grade,
      When I click create competition,
      Then the competition is created
      And I am shown a ui element that display full details for the competition.

      Scenario: AC2: A competition has a name, is for a given sport, and is for a certain grade level, e.g., U10 (under 10-year-old), masters, MAG level 3. All are compulsory fields.
        Given I am a user of account type federation administrator
        And I am on the create or update competition page
        And I input invalid information for one of name, sport or grade,
        When I click create competition,
        Then the competition is not created
        And I am shown an error message stating that the field contains invalid information

  Scenario: AC2: A competition has a name, is for a given sport, and is for a certain grade level, e.g., U10 (under 10-year-old), masters, MAG level 3. All are compulsory fields.
    Given I am a user of account type federation administrator
    And I am on the create or update competition page
    And don’t input information for one of name, sport or grade,
    When I click create competition,
    Then the competition is not created
    And I am shown an error message stating that the field cannot be empty.

  Scenario: AC2: A competition has a name, is for a given sport, and is for a certain grade level, e.g., U10 (under 10-year-old), masters, MAG level 3. All are compulsory fields.
    Given I am a user of account type federation administrator
    And I am on the create or update competition page
    Then There are fields for name, sport and grade


    Scenario: AC4: A competition can include teams or individuals. For teams, a competition must include at least one team and all teams must be of the same grade level. For individuals, at least one person needs to be added to the competition.
      Given I am a user of account type federation administrator
      And I am on the create or update competition page
      And I have selected competition type as individual,
      When I select a user from a list of users in the system
      And I add that user to the competition and save my changes
      Then that user is added to the competition


  Scenario: AC4: A competition can include teams or individuals. For teams, a competition must include at least one team and all teams must be of the same grade level. For individuals, at least one person needs to be added to the competition.
    Given I am a user of account type federation administrator
    And I am on the create or update competition page
    And I have selected competition type as team,
    When I select a team from a list of teams in the system
    And I add that team to the competition and save my changes,
    Then that team is added to the competition.

  Scenario: AC4: A competition can include teams or individuals. For teams, a competition must include at least one team and all teams must be of the same grade level. For individuals, at least one person needs to be added to the competition.
    Given I am a user of account type federation administrator
    And I am on the create or update competition page
    And I have selected competition type as individual or team,
    When I attempt to save,
    Then my changes are not saved
    And I am shown an error message stating that the competition members must not be empty.

#    Scenario: AC5: If a team is currently enrolled into a competition, there is a UI element to view that competition. The UI element leads to the detail page of that competition where all teams taking part in the competition are listed.
#      Given I am viewing details about a team and that team is enrolled in a competition,
#      When I click a dedicated ui element to view that competition,
#      Then I am brought to a page with details about the competition, including name, grade level, sport and all teams involved in said competition.
#
#  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
#    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
#    When I apply a filter for that sport and select an option to display all competitions,
#    Then I am shown all competitions, past and current for the selected sport.
#
#  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
#    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
#    When I apply a filter for that sport and I select an option to display only current competitions,
#    Then I am shown only current competitions for the selected sport.
#
#
#  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
#    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
#    When I apply a filter for that sport and I select an option to display only past competitions,
#    Then I am shown only past competitions for the selected sport.
