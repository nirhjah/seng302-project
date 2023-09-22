@view_all_clubs

Feature: U44 - View All Clubs

  Scenario: AC1: when viewing all clubs, I can select a sport from a list of sports to filter the clubs
    Given I am on the view all clubs page
    When I select a sport from a list of sports known by the system
    Then only the clubs with the selected sport are displayed

  Scenario: AC2: I can filter by location
    Given I am on the view all clubs page and all the filters are selected
    When no sports are selected,
    Then the list of clubs displays all clubs in the database

  Scenario: AC3: I 
    Given I am on the  view all clubs page
    When I deselect one or more sports from a list of sports known by the system
    Then the list of clubs updates according to the selected sports

  Scenario: AC4
    Given I am on the view all clubs page
    When I enter a search term
    Then all clubs matching that search term (including partial matches) display
