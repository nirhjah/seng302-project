package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;

public class DefaultFunctions {

    public static void pwLogin() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.locator("input#username").type("admin@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type("1");
        PlaywrightBrowser.page.locator("#loginButton").click();
    }

    public static void pwCreateTeam() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createTeam");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

        PlaywrightBrowser.page.locator("input#name").type("team");
        PlaywrightBrowser.page.locator("input#sport").type("football");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();
    }

    public static void pwCreateFormation() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/profile?teamID=1");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#formations-tab").click();
        PlaywrightBrowser.page.locator("li#create-formation-li").click();
        PlaywrightBrowser.page.locator("button#create-formation-button").click();
    }

    public static void pwCreateNewTeamWithFormationAndActivity() {


        for (int i = 0; i < 10; i++) {

            String teamName = String.valueOf(i);
            //create team
            PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createTeam");
            PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

            PlaywrightBrowser.page.locator("input#name").type(teamName);
            PlaywrightBrowser.page.locator("input#sport").type("football");
            PlaywrightBrowser.page.locator("input#city").type("Christchurch");
            PlaywrightBrowser.page.locator("input#country").type("New Zealand");
            PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

            //create formation
            PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
            PlaywrightBrowser.page.locator("div.tab#formations-tab").click();
            PlaywrightBrowser.page.locator("li#create-formation-li").click();
            PlaywrightBrowser.page.locator("button#create-formation-button").click();

            //create activity
            PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
            PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);

            PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
            PlaywrightBrowser.page.locator("#team").selectOption(teamName);
            PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
            PlaywrightBrowser.page.fill("#description", "desc");
            PlaywrightBrowser.page.fill("#startDateTime", "2025-04-02T05:15");
            PlaywrightBrowser.page.fill("#endDateTime", "2026-04-02T05:15");
            PlaywrightBrowser.page.locator("input#address-line-1").type("1");
            PlaywrightBrowser.page.locator("input#postcode").type("8042");
            PlaywrightBrowser.page.locator("input#city").type("Christchurch");
            PlaywrightBrowser.page.locator("input#country").type("New Zealand");
            PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

        }



    }

}

