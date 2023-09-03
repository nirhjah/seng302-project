package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.options.LoadState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/team-info?teamID=1");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("div.tab#formations-tab").click();
        PlaywrightBrowser.page.locator("li#create-formation-li").click();
        PlaywrightBrowser.page.locator("button#create-formation-button").click();
    }

    public static void pwCreateNewTeamWithFormationAndActivity() {



        String teamName = String.valueOf(0);
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

    public static void pwCreateNewTeamWithFormationAnd5Activity() {

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/populate_database");

        String teamName = String.valueOf(0);
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

        // Get the current datetime
        LocalDateTime currentDateTime = LocalDateTime.now().plusMinutes(1);

        // Format the current datetime as "yyyy-MM-dd'T'HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String currentDateTimeString = currentDateTime.format(formatter);
        LocalDateTime endDateTime = currentDateTime.plusMinutes(1);
        String endDateTimeString = endDateTime.format(formatter);


        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
           PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
            PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
            PlaywrightBrowser.page.locator("#team").selectOption(teamName);
//            PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
            PlaywrightBrowser.page.fill("#description", "desc");
            PlaywrightBrowser.page.fill("#startDateTime", currentDateTimeString);
            PlaywrightBrowser.page.fill("#endDateTime", endDateTimeString);
            PlaywrightBrowser.page.locator("input#address-line-1").type("1");
            PlaywrightBrowser.page.locator("input#postcode").type("8042");
            PlaywrightBrowser.page.locator("input#city").type("Christchurch");
            PlaywrightBrowser.page.locator("input#country").type("New Zealand");
            PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
        PlaywrightBrowser.page.locator("#team").selectOption(teamName);
//        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
        PlaywrightBrowser.page.fill("#description", "desc");
        PlaywrightBrowser.page.fill("#startDateTime", currentDateTimeString);
        PlaywrightBrowser.page.fill("#endDateTime", endDateTimeString);
        PlaywrightBrowser.page.locator("input#address-line-1").type("1");
        PlaywrightBrowser.page.locator("input#postcode").type("8042");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
        PlaywrightBrowser.page.locator("#team").selectOption(teamName);
//        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
        PlaywrightBrowser.page.fill("#description", "desc");
        PlaywrightBrowser.page.fill("#startDateTime", currentDateTimeString);
        PlaywrightBrowser.page.fill("#endDateTime", endDateTimeString);
        PlaywrightBrowser.page.locator("input#address-line-1").type("1");
        PlaywrightBrowser.page.locator("input#postcode").type("8042");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
        PlaywrightBrowser.page.locator("#team").selectOption(teamName);
//        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
        PlaywrightBrowser.page.fill("#description", "desc");
        PlaywrightBrowser.page.fill("#startDateTime", currentDateTimeString);
        PlaywrightBrowser.page.fill("#endDateTime", endDateTimeString);
        PlaywrightBrowser.page.locator("input#address-line-1").type("1");
        PlaywrightBrowser.page.locator("input#postcode").type("8042");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();

        PlaywrightBrowser.page.navigate(PlaywrightBrowser.baseUrl + "/createActivity");
        PlaywrightBrowser.page.waitForLoadState(LoadState.NETWORKIDLE);
        PlaywrightBrowser.page.locator("#activityType").selectOption("Game");
        PlaywrightBrowser.page.locator("#team").selectOption(teamName);
//        PlaywrightBrowser.page.locator("#formation-dropdown").selectOption("1-4-4-2");
        PlaywrightBrowser.page.fill("#description", "desc");
        PlaywrightBrowser.page.fill("#startDateTime", currentDateTimeString);
        PlaywrightBrowser.page.fill("#endDateTime", endDateTimeString);
        PlaywrightBrowser.page.locator("input#address-line-1").type("1");
        PlaywrightBrowser.page.locator("input#postcode").type("8042");
        PlaywrightBrowser.page.locator("input#city").type("Christchurch");
        PlaywrightBrowser.page.locator("input#country").type("New Zealand");
        PlaywrightBrowser.page.locator("div.submit-button button[type='submit']").click();


    }


    }

