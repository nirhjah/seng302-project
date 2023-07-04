@filter_teams_by_sport
Feature: U19 - Filter teams by sport
  Scenario: AC1 - Given I am on the search teams page, when I select a sport from a list of sports known by the system, then only the teams with the selected sport are displayed.
    Given I am on the search teams page
    When I select a sport "Soccer"
    Then only teams with sports are selected:
      | Soccer |

  Scenario: AC2 - Given I am on the search teams page, when I select more than one sport from a lit of sports known by the system, then all teams with any of the selected sports are displayed.
    Given I am on the search teams page
    When I select a sport "Soccer"
    When I select a sport "Hockey"
    Then only teams with sports are selected:
      | Soccer |
      | Hockey |

  Scenario: AC3 - Given I am on the search teams page, when I deselect one or more sports from a lit of sports known by the system, then the list of teams updates according to the selected sports.
    Given I am on the search teams page
    When I select a sport "Soccer"
    When I select a sport "Hockey"
    When I deselect a sport "Soccer"
    Then only teams with sports are selected:
      | Hockey |

  Scenario: AC4 - Given I am on the search teams page and all the filters are selected, when no sports are selected, the list of teams displays all teams in the database.
    Given I am on the search teams page
    When no teams are selected
    Then all teams are shown

