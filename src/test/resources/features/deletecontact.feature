@delete-contact
Feature: REST - Delete Contact Feature
  Background:
    Given the base uri is "https://thinking-tester-contact-list.herokuapp.com"
    And a new user is created
    And "2" contacts are created

  Scenario Outline: Validate that a user may delete contacts
    Given the user has the list of contacts
    When the user deletes the second contact
    Then the contacts list size is "<list size>"
    And the contact must not be in the list of contacts

    Examples:
      | list size |
      | 1         |
