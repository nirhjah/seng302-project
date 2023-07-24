# The associated feature file is FilterTeamsBySportOrCityFeature.java
# ! Note that AC3 is missing. This is because it's a front-end requirement, 
# ! so it's best to make it a manual test.
@view_teams_page_filtering
Feature: U20 - Filter Teams By City
  Scenario: AC1 - Given I am on the search teams page showing me results for a search query,
            when I select a city from the list of cities collected from the teams shown in the list,
            then only the teams with the selected city are displayed
    Given there are no other teams
    Given there is a sports team called "Anemone" located in "Austin"
      And there is a sports team called "Baboon" located in "Berlin"
      And there is a sports team called "Cats" located in "Copenhagen"
      And there is a sports team called "Humans" located in "Berlin"
    When I select the city "Berlin"
    Then only these teams are selected:
      | Baboon |
      | Humans |

  Scenario: AC2 - Given I am on the search team page,
            when I select more than one city from the list of cities collected from the teams shown in the results,
            then all teams with any of the selected cities are displayed
    Given there are no other teams
    Given there is a sports team called "Anemone" located in "Austin"
      And there is a sports team called "Baboon" located in "Berlin"
      And there is a sports team called "Cats" located in "Copenhagen"
      And there is a sports team called "Dolphin" located in "Delphi"
      And there is a sports team called "Humans" located in "Copenhagen"
    When I select the city "Austin"
     And I select the city "Copenhagen"
    Then only these teams are selected:
      | Anemone |
      |  Cats   |
      | Humans  |

#   Scenario: AC3 - Given I am on the search teams page,
#             when I deselect one or more cities from the list of cities collected from the teams shown in the results,
#             then the list of teams updates according to the selected cities
