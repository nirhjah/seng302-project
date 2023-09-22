@view_all_clubs

Feature: U44 - View All Clubs

#  Scenario: AC1: when viewing all clubs, I can select a sport from a list of sports to filter the clubs
#    Given I am on the view all clubs page
#    When I select a sport from a list of sports known by the system
#    Then only the clubs with the selected sport are displayed
#
#  Scenario: AC2: I can filter by location
#    Given I am on the view all clubs page and all the filters are selected
#    When no sports are selected,
#    Then the list of clubs displays all clubs in the database
#
#  Scenario: AC3: I
#    Given I am on the  view all clubs page
#    When I deselect one or more sports from a list of sports known by the system
#    Then the list of clubs updates according to the selected sports
#
#  Scenario: AC4
#    Given I am on the view all clubs page
#    When I enter a search term
#    Then all clubs matching that search term (including partial matches) display

  Scenario: AC1 - Given I am on the view all clubs page showing me results for a search query,
  when I select a city from the list of cities collected from the clubs shown in the list,
  then only the clubs with the selected city are displayed
    Given there are no other clubs
    Given there is a club called "Anemone" located in "Austin"
    And there is a club called "Baboon" located in "Berlin"
    And there is a club called "Cats" located in "Copenhagen"
    And there is a club called "Humans" located in "Berlin"
    When I select the city "Berlin"
    Then only these teams are selected:
      | Baboon |
      | Humans |

  Scenario: AC2 - Given I am on the view all clubs page,
  when I select more than one city from the list of cities collected from the clubs shown in the results,
  then all clubs with any of the selected cities are displayed
    Given there are no other clubs
    Given there is a club called "Anemone" located in "Austin"
    And there is a club called "Baboon" located in "Berlin"
    And there is a club called "Cats" located in "Copenhagen"
    And there is a club called "Dolphin" located in "Delphi"
    And there is a club called "Humans" located in "Copenhagen"
    When I select the city "Austin"
    And I select the city "Copenhagen"
    Then only these teams are selected:
      | Anemone |
      |  Cats   |
      | Humans  |

