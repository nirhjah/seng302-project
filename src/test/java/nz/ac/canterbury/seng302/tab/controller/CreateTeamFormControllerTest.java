package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class CreateTeamFormControllerTest {

    @Autowired
    private MockMvc mockMvc;

     /**
     * Miminimum amount of fields filled with valid input for post request
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenAFieldsValid_return302() throws Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "{test.team 1}").param("sport", "hockey-team a'b")
     .param("city", "Christchurch").param("country", "New Zealand")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./profile?teamID=1"));
     }

     /**
     * The Team Name is Invalid according to the regex, as it contains invalid
     char: ^
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenTeamNameFieldIsInvalid_return302() throws Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test^team").param("sport", "hockey")
     .param("location", "christchurch")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

     /**
     * The Sport name is Invalid as it containings invalid char: #
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenSportFieldIsInvalid_return302() throws Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test").param("sport", "###")
     .param("location", "christchurch")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

     /**
     * The City name is invalid as it contains invalid char: $
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenCityIsInvalid_return302() throws Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test").param("sport", "hockey")
     .param("city", "$sumner$").param("country", "New Zealand")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

     /**
     * The Team name is invalid as it contains invalid chars (which are valid for
     sport and location): - '
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenTeamNameFieldIsInvalidWithCharsValidForSport_return302() throws
     Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test-team's").param("sport", "hockey")
     .param("city", "Christchurch").param("New Zealand")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

     /**
     * The sport name is invalid as it contains invalid chars (which are valid for
     team): . { } and numbers
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenSportFieldIsInvalidWithCharsValidForTeam_return302() throws
     Exception {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test").param("sport", "123.123{123}")
     .param("location", "christchurch")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

     /**
     * The city name is invalid as it contains invalid chars (which are valid
     for team): . { } and numbers
     * @throws Exception thrown if Mocking fails
     */
     @Test
     void whenCityIsInvalidWithCharsValidForTeam__return302() throws Exception
     {
     mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
     .param("name", "test").param("sport", "hockey")
     .param("location", "abc123'{}.a")).andExpect(status().isFound())
     .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
     }

    /**
     * The country name is invalid as it contains invalid chars (which are valid
     for team): . { } and numbers
     * @throws Exception thrown if Mocking fails
     */
    @Test
    void whenCountryIsInvalidWithCharsValidForTeam__return302() throws Exception
    {
        mockMvc.perform(post("/createTeam", 42L).param("teamID", "1")
                        .param("name", "test").param("sport", "hockey")
                        .param("location", "abc123'{}.a")).andExpect(status().isFound())
                .andExpect(view().name("redirect:./createTeam?invalid_input=1&edit=1"));
    }
}
