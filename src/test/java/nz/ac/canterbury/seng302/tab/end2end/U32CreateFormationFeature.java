package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.ElementHandle;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;


public class U32CreateFormationFeature {

    private static boolean isSetupExecuted = false;

    @Before("@create_formation")
    public void setup() {
        DefaultFunctions.pwLogin();
        if (!isSetupExecuted) {
            DefaultFunctions.pwCreateTeam();
            DefaultFunctions.pwCreateFormation();
            isSetupExecuted = true;
        }
    }

    @Given("I am on my team's profile")
    public void iAmOnMyTeamSProfile() throws Exception {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=1");
    }

    @When("I click on a UI element to see all the team's formations")
    public void iClickOnAUIElementToSeeAllTheTeamSFormations() {
        PlaywrightBrowser.page.locator("div.tab#formations-tab").click();
    }

    @Then("I see a list of all formations for that team")
    public void iSeeAListOfAllFormationsForThatTeam() throws Exception {
        PlaywrightBrowser.page.locator(".formation-li");
    }

    @Given("I am on my team's formation page")
    public void iAmOnMyTeamSFormationPage() throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
    }

    @When("I click on a UI element to create a new line-up")
    public void iClickOnAUIElementToCreateANewLineUp() {
        PlaywrightBrowser.page.locator("li#create-formation-li").click();
    }

    @Then("I see a graphical representation of a sport pitch corresponding to the sport of that team")
    public void iSeeAGraphicalRepresentationOfASportPitchCorrespondingToTheSportOfThatTeam() {
        PlaywrightBrowser.page.querySelector("#pitch:visible");
    }

    @Given("I set up an invalid number of players per sector with {string}")
    public void iSetUpAnInvalidNumberOfPlayersPerSectorWithInput(String input) throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
        iClickOnAUIElementToCreateANewLineUp();
        PlaywrightBrowser.page.locator("input#formation-string").type(input);
    }

    @When("I generate that formation")
    public void iGenerateThatFormation() {
        PlaywrightBrowser.page.locator("button#generate-formation-button").click();
    }

    @Then("an error message tells me that the formation is invalid.")
    public void anErrorMessageTellsMeThatTheFormationIsInvalid() {
        PlaywrightBrowser.page.querySelector(".error-message:visible");
    }

    @Given("I am on the formation creation page")
    public void iAmOnTheFormationCreationPage() throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
        iClickOnAUIElementToCreateANewLineUp();
    }

    @When("I specify a number of players per sector on the pitch in the form of dash separated numbers starting from the back line up to the front line on the pitch")
    public void iSpecifyANumberOfPlayersPerSectorOnThePitchInTheFormOfDashSeparatedNumbersStartingFromTheBackLineUpToTheFrontLineOnThePitch() {
        PlaywrightBrowser.page.locator("input#formation-string").fill("1-4-4-2");
    }

    @Then("a formation is generated that matches the formation string")
    public void aFormationIsGeneratedThatMatchesTheFormationString() {
        List<Integer> formationList = Arrays.asList(1,4,4,2);
        int currentPlayerIndex = 0;
        for (int i = 0; i < formationList.size(); i++) {
            int max = currentPlayerIndex + formationList.get(i);
            for (; currentPlayerIndex < max; currentPlayerIndex++) {
                int playersAbove = PlaywrightBrowser.page.querySelectorAll(".player:above(.player#player" + (currentPlayerIndex+1) + ")").size();
                int expectedPlayersAbove = formationList.subList(i + 1, formationList.size()).stream().mapToInt(Integer::intValue).sum();
                Assertions.assertEquals(expectedPlayersAbove, playersAbove);
            }
        }
    }

    @Given("I have correctly set up a formation with the number of players per sector")
    public void iHaveCorrectlySetUpAFormationWithTheNumberOfPlayersPerSector() throws Exception {
        iAmOnMyTeamSProfile();
        iClickOnAUIElementToSeeAllTheTeamSFormations();
        iClickOnAUIElementToCreateANewLineUp();
        PlaywrightBrowser.page.locator("input#formation-string").fill("1-4-3-3");
    }

    @When("I click on the create formation button")
    public void iClickOnTheCreateFormationButton() {
        PlaywrightBrowser.page.locator("button#create-formation-button").click();
    }

    @Then("the formation is persisted in the system")
    public void theFormationIsPersistedInTheSystem() {
        List<ElementHandle> formationListElements = PlaywrightBrowser.page.querySelectorAll(".formation-li");
        Assertions.assertEquals("1-4-3-3", formationListElements.get(formationListElements.size()-1).textContent().trim());
    }

}
