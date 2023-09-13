Feature: U14 - Teams Location API

  Scenario: AC1: Can specify a full address on the edit team profile form
    Given I am on the edit team profile form,
    When I want to edit a team's location,
    Then I can specify a full address, i.e. up to two lines of free text (e.g. line 1: 114 Ilam Rd, line 2: Ilam), a suburb, a city, a postcode, and a country.

  Scenario: AC2: Must specify the city and country while editing location
    Given I edit the location (a.k.a. address) of a team,
    When I edit the address,
    Then I must specify the city and country

  Scenario: AC3: Error message is displayed if city and country is not specified
    Given I edit the location (a.k.a. address) of a team,
    When I edit the address and I don't specify both the city and country,
    Then an error message tells me these two fields are mandatory


  Scenario: AC4: May specify address line 1 and 2, suburb and postcode while editing location
    Given I edit the location (a.k.a. address) of a team,
    When I edit the address,
    Then I may specify the address lines 1 and 2, the suburb, and the postcode, but these fields are not mandatory

  Scenario: AC5: Location suggestions is displayed when I start typing a location
    Given I am on the edit team profile form,
    When I start typing for a location,
    Then I receive suggestions,
    Then the matching fields from the suggestion I selected are populated into their corresponding fields in the form
    # Enter steps here