@create_view_update_competition_e2e
Feature: U39 – Create / view / update competition E2E

Scenario: AC5: If a team is currently enrolled into a competition, there is a UI element to view that competition. The UI element leads to the detail page of that competition where all teams taking part in the competition are listed.
    Given I am viewing details about a team and that team is enrolled in a competition,
    When I click a dedicated ui element to view that competition,
    Then I am brought to a page with details about the competition, including name, grade level, sport and all teams involved in said competition.

  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
    When I apply a filter for that sport and select an option to display all competitions,
    Then I am shown all competitions, past and current for the selected sport.

#  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
#    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
#    When I apply a filter for that sport and I select an option to display only current competitions,
#    Then I am shown only current competitions for the selected sport.


  Scenario: AC6: There is a convenient way to browse for all current and past competitions per sport.
    Given I am on a page dedicated to displaying competitions and there exists past and current competitions for a sport,
    When I apply a filter for that sport and I select an option to display only past competitions,
    Then I am shown only past competitions for the selected sport.
