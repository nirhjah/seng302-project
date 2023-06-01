@filter_teams_by_sport
Feature: U19 - Filter teams by sport
  Scenario: AC1 - Given I am on the search teams page, when I select a sport from a list of sports known by the system, then only the teams with the selected sport are displayed.
    Given I select a sport "soccer"
    Then only teams with sports are selected:
      | soccer |

  Scenario: AC2 - Given I am on the search teams page, when I select more than one sport from a lit of sports known by the system, then all teams with any of the selected sports are displayed.
    Given I select a sport "soccer"
    Given I select a sport "hockey"
    Then only teams with sports are selected:
      | soccer |
      | hockey |

  Scenario: AC3 - Given I am on the search teams page, when I deselect one or more sports from a lit of sports known by the system, then the list of teams updates according to the selected sports.
    Given I select a sport "soccer"
    Given I select a sport "hockey"
    Given I deselect a sport "soccer"
    Then only teams with sports are selected:
      | hockey |

  Scenario: AC4 - Given I am on the search teams page and all the filters are selected, when no sports are selected, the list of teams displays all teams in the database.
    Given no teams are selected
    Then all teams are shown

