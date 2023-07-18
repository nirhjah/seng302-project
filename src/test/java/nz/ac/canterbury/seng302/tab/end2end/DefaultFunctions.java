package nz.ac.canterbury.seng302.tab.end2end;

public class DefaultFunctions {

    public static void pwLogin() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/login");
        PlaywrightBrowser.page.locator("input#username").type("admin@gmail.com");
        PlaywrightBrowser.page.locator("input#password").type("1");
        PlaywrightBrowser.page.locator("#loginButton").click();
    }

    public static void pwCreateTeam() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createTeam");
        PlaywrightBrowser.page.locator("input#name").type("team");
        PlaywrightBrowser.page.locator("input#sport").type("football");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();
    }

    public static void pwCreateFormation() {
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/profile?teamID=1");
        PlaywrightBrowser.page.locator("div.tab#formations-tab").click();
        PlaywrightBrowser.page.locator("li#create-formation-li").click();
        PlaywrightBrowser.page.locator("button#create-formation-button").click();
    }
}
