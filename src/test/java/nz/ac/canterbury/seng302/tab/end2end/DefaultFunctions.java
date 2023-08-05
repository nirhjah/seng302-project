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

    public static void pwCreateActivity(){
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("[name='activityType']").selectOption("Game");
        PlaywrightBrowser.page.locator("[name='team']").selectOption("1");
        PlaywrightBrowser.page.locator("textarea#description").type("This is the description");
        PlaywrightBrowser.page.locator("input#startDateTime").fill("2024-08-03T14:30");
        PlaywrightBrowser.page.locator("input#endDateTime").fill("2024-09-03T14:30");
        PlaywrightBrowser.page.locator("input#address-line-1").type("20 Ilam Road");
        PlaywrightBrowser.page.locator("input#postcode").type("8042");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();
    }
}
