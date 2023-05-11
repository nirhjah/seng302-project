package nz.ac.canterbury.seng302.tab.integration;


import io.cucumber.java.en.Given;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RegisterConfirmEmail {
    @Given("there is a valid registration link")
    public void hi() {}
}
