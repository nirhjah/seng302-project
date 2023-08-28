package nz.ac.canterbury.seng302.tab.end2end;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;

import java.util.Objects;

/**
 * Implementation from Playwright Example Repository provided by teaching team
 *
 * Cucumber tests define the BeforeAll, BeforeEach, AfterEach and AfterAll steps in one file, which is reused for all
 * tests in the suite.
 *
 * The PlayWrightBrowser Class lets us reuse the setup and tier down phases, managing them in one place. This also
 * provides a single place to manage the browser context (page etc).
 *
 * The @CucumberContextConfiguration is required to annotate some class in the end2end package, so that the glue can
 * argument can find these files. This is because of the cucumber-spring dependency.
 */
@CucumberContextConfiguration
public class PlaywrightBrowser {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext browserContext;
    static Page page;
    static String baseUrl = "localhost:8080";


    @BeforeAll
    public static void openResources() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        // The current config runs the tests headlessly - this means that the browser is not displayed.
        // To run the browser headed (you can see the browser GUI as the tests run), switch to the following line:
        //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        baseUrl = Objects.equals(System.getenv("USER"), "gitlab-runner") ? "localhost:9500/test" : "localhost:8080";
    }

    @Before(order=1)        // Fix suggested by Matthew: Ensure this is ALWAYS run first
    public void openContext() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
        page.navigate(baseUrl);
    }

    @After
    public void closeContext() {
        browserContext.close();
    }

    @AfterAll
    public static void closeResources() {
        playwright.close();
    }

}
