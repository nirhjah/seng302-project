@U19_filter_teams_by_sport
# ! Note that AC3 is missing. This is because it's a front-end requirement, 
# ! so it's best to make it a manual test.
Feature: U19 - Filter teams by sport
  Scenario: AC1 - Given I am on the search teams page,
                  when I select a sport from a list of sports known by the system,
                  then only the teams with the selected sport are displayed.
    Given there is a sports team called "Apple" who plays the sport "Soccer"
    And there is a sports team called "Banana" who plays the sport "Hockey"
    And there is a sports team called "Coconut" who plays the sport "Soccer"
    When I select the sport "Soccer"
    Then only these teams are selected:
      |  Apple  |
      | Coconut |

  Scenario: AC2 - Given I am on the search teams page,
                  when I select more than one sport from a list of sports known by the system,
                  then all teams with any of the selected sports are displayed.
    Given there is a sports team called "Apple" who plays the sport "Soccer"
      And there is a sports team called "Banana" who plays the sport "Hockey"
      And there is a sports team called "Coconut" who plays the sport "Badminton"
      And there is a sports team called "Dates" who plays the sport "Soccer"
    When I select the sport "Soccer"
     And I select the sport "Hockey"
    Then only these teams are selected:
      | Apple  |
      | Banana |
      | Dates  |

# //   Scenario: AC3 - Given I am on the search teams page,
# //                   when I deselect one or more sports from a list of sports known by the system,
# //                   then the list of teams updates according to the selected sports.

  Scenario: AC4 - Given I am on the search teams page and all the filters are selected,
                  when no sports are selected,
                  then list of teams displays all teams in the database.
    Given there is a sports team called "Apple" who plays the sport "Soccer"
      And there is a sports team called "Banana" who plays the sport "Hockey"
      And there is a sports team called "Coconut" who plays the sport "Badminton"
      And there is a sports team called "Dates" who plays the sport "Soccer"
    When no teams are selected
    Then only these teams are selected:
      |  Apple  |
      |  Banana |
      | Coconut |
      |  Dates  |

