@create_club

Feature: U38 – Create / view / update a club

  Scenario: AC1: There is a UI element that allows me to create a club.
    Given I am anywhere on the system
    When I click on a UI element to create a club
    Then I will see a form to create a club

  Scenario: AC2: When creating a club, I must specify a name and a location, i.e. address line 1, address line 2, suburb, postcode, city, and country, with address line 1, postcode, city and country being compulsory. I can add an optional logo. If none given, a generic club image is used.
    Given  I am on the create club page
    When I enter valid values for the name, address line, postcode, city and country  and optionally a logo
    Then The club is created into the system

  Scenario: AC2: When creating a club, I must specify a name and a location, i.e. address line 1, address line 2, suburb, postcode, city, and country, with address line 1, postcode, city and country being compulsory. I can add an optional logo. If none given, a generic club image is used.
    Given I am on the create club page
    When I enter an empty club name or a name with invalid characters for a club (e.g. non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),
    Then An error message tells me the name is invalid

  Scenario: AC2: When creating a club, I must specify a name and a location, i.e. address line 1, address line 2, suburb, postcode, city, and country, with address line 1, postcode, city and country being compulsory. I can add an optional logo. If none given, a generic club image is used.
    Given I am on the create club page
    When I enter either an empty location that is not addressline2 and suburb, or location with invalid characters (i.e. any non-letters except spaces, apostrophes and dashes),
    Then An error message tells me the location is invalid

  Scenario: AC3: When creating or editing a club, I can select as many teams as I want from the teams I manage to be added into that club.
    Given I am on the create or edit club page
    When I am the manager of at least one team
    Then I can select as many teams as I want from the list of teams I manage to be added to that club

  Scenario: AC4: A team cannot belong to more than one club.
    Given I am on the create or edit club page
    When I select a team that belongs to another club
    Then An error message tells me that team already belongs to another club

  Scenario: AC5 A club cannot contain teams of different sports.
    Given I am on the create or edit club page
    When I select teams that contain different sports
    Then An error message tells me that teams must have the same sport

  Scenario: AC6: Teams belonging to a club have a link to their clubs in their team’s profile page.
    Given I am on the team’s profile page and the team belongs to a club,
    When I click on the link to their club,
    Then I will see their club details (Not sure if this is what the link does)

    Scenario: AC9: When creating a club, I must specify a name, sport (PO APPROVED) and a location, i.e. address line 1, address line 2, suburb, postcode, city, and country, with address line 1, postcode, city and country being compulsory. I can add an optional logo. If none given, a generic club image is used.
      Given I am on the create club page
      When I enter an empty club sport or a sport with invalid characters (i.e. any non-letters except spaces, apostrophes and dashes)
      Then An error message tells me the sport is invalid
