@view_all_clubs

Feature: U44 - View All Clubs

  Scenario: AC1 - Given I am on the view all clubs page showing me results for a search query,
  when I select a city from the list of cities collected from the clubs shown in the list,
  then only the clubs with the selected city are displayed
    Given there are no other clubs
    Given there is a club called "Anemone" located in "Austin"
    And there is a club called "Baboon" located in "Berlin"
    And there is a club called "Cats" located in "Copenhagen"
    And there is a club called "Humans" located in "Berlin"
    When I filter by the city "berlin"
    Then only these clubs are selected:
      | Baboon |
      | Humans |

  Scenario: AC2 - Given I am on the view all clubs page,
  when I select more than one city from the list of sports collected from the clubs shown in the results,
  then all clubs with any of the selected sports are displayed
    Given there are no other clubs
    Given there is a club called "Anemone" with the sport "football"
    And there is a club called "Baboon" with the sport "hockey"
    And there is a club called "Cats" with the sport "rugby"
    And there is a club called "Dolphin" with the sport "handball"
    And there is a club called "Humans" with the sport "football"
    When I filter by the sport "football"
    And I filter by the sport "rugby"
    Then only these clubs are selected:
      | Anemone |
      |  Cats   |
      | Humans  |

