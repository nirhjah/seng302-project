package nz.ac.canterbury.seng302.tab.end2end;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class U9ViewTeamProfile {
    private static boolean isSetupExecuted = false;

    @Before("@view_team")
    public void create_team_init() {
        DefaultFunctions.pwLogin();
        if (!isSetupExecuted) {
            DefaultFunctions.pwCreateTeam();
            isSetupExecuted = true;
        }
    }

    @Given("I am anywhere on the system,")
    public void i_am_anywhere_on_the_system() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/home");
    }

    @When("I click on one of my team's name,")
    public void i_click_on_one_of_my_team_s_name() {
        PlaywrightBrowser.page.selectOption("#dropDownList", "1");
    }

    @Then("I see the team's details \\(a.k.a. profile), i.e. it's name, location and sport")
    public void i_see_the_team_s_details_a_k_a_profile_i_e_it_s_name_location_and_sport() {
        String nameValue = String.valueOf(PlaywrightBrowser.page.evaluate("document.querySelector('h3[data-cy=\"name\"]').textContent"));

        String sportValue = String.valueOf(PlaywrightBrowser.page.evaluate("document.querySelector('h3[data-cy=\"sport\"]').textContent"));
        String locationValue = String.valueOf(PlaywrightBrowser.page.evaluate("document.querySelector('h3[data-cy=\"location\"]').textContent"));
        Assertions.assertNotNull(nameValue);
        Assertions.assertNotNull(sportValue);
        Assertions.assertNotNull(locationValue);

    }

    @Given("I am on my team details page,")
    public void i_am_on_my_team_details_page() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=1");
    }

    @Then("I cannot edit any of the details that are shown to me.")
    public void i_cannot_edit_any_of_the_details_that_are_shown_to_me() {
        boolean allH3NotContentEditable = (boolean) PlaywrightBrowser.page.evaluate("() => { " +
                "const h3Elements = Array.from(document.querySelectorAll('h3')); " +
                "return h3Elements.every(element => element.getAttribute('contentEditable') !== 'true'); " +
                "}");

        Assertions.assertTrue(allH3NotContentEditable);

    }
}
