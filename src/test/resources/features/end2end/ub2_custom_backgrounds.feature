@custom_background
Feature: UB2 - Custom Backgrounds

  Scenario Outline: AC1: Users should be able to select a named pitch type from multiple
  different pitch types e.g. soccer, basketball, field hockey etc.
    Given I am creating a whiteboard
    When I select the field type
    Then I can choose from a list of field types
    And the selected pitch "<pitch>" becomes the background of the whiteboard

    Examples:
      | pitch             |
      | American Football |
      | Basketball        |
      | Hockey            |
      | Netball           |
      | Rugby             |
      | Soccer            |
      | Tennis            |
      | Volleyball        |