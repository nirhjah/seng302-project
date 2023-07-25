package nz.ac.canterbury.seng302.tab.integration;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.tab.controller.CreateClubController;
import nz.ac.canterbury.seng302.tab.controller.ProfileFormController;
import nz.ac.canterbury.seng302.tab.controller.ViewClubController;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.ClubRepository;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import nz.ac.canterbury.seng302.tab.service.ClubService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@ContextConfiguration(classes = IntegrationTestConfigurations.class)
public class CreateViewUpdateClubIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserService userService;

    @SpyBean
    private ClubService clubService;

    @Autowired
    private ApplicationContext applicationContext;


    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    private ClubRepository clubRepository;

    private User user;

    private Team team;

    private Team team2;

    private Team teamDifferentSport;

    private Team teamAlreadyInClub;

    private MvcResult result;

    private Club teamsClub;


    @Before("@create_club")
    public void setup() throws IOException {

        clubRepository = applicationContext.getBean(ClubRepository.class);
        teamRepository = applicationContext.getBean(TeamRepository.class);
        userRepository = applicationContext.getBean(UserRepository.class);

        TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class);
        EmailService emailService = applicationContext.getBean(EmailService.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        userService = Mockito.spy(new UserService(userRepository, taskScheduler, emailService, passwordEncoder));
        clubService = Mockito.spy(new ClubService(clubRepository));
        teamService = Mockito.spy(new TeamService(teamRepository));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new CreateClubController(clubService, userService, teamService), new ProfileFormController(userService,teamService), new ViewClubController(userService,teamService,clubService)).build();


        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        teamRepository.deleteAll();
        userRepository.deleteAll();
        Location testLocation = new Location(null, null, null, "CHCH", null, "NZ");
        user = new User("John", "Doe", new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime(), "johndoe@example.com", "Password123!", testLocation);
        team = new Team("test1", "Hockey", new Location(null, null, null, "chch", null, "nz"));
        team2 = new Team("test2", "Hockey", new Location("2 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        teamDifferentSport = new Team("test3", "Rugby", new Location("3 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));

        teamAlreadyInClub = new Team("TeamInAClubAlready", "Hockey", new Location("5 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand"));
        teamsClub = new Club("TeamsClub", new Location("4 test lane", null, null, "Christchurch", "8041", "NZ"), "Hockey",null);
        clubRepository.save(teamsClub);
        teamAlreadyInClub.setTeamClub(teamsClub);

        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(teamDifferentSport);
        teamRepository.save(teamAlreadyInClub);


        userRepository.save(user);

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(teamService.getTeamClubId(team)).thenReturn(teamsClub.getClubId());
        when(teamService.getTeam(Long.parseLong(team.getTeamId().toString()))).thenReturn(team);
        when(teamService.getTeam(Long.parseLong(team2.getTeamId().toString()))).thenReturn(team2);
        when(teamService.getTeam(Long.parseLong(teamDifferentSport.getTeamId().toString()))).thenReturn(teamDifferentSport);
        when(teamService.getTeam(Long.parseLong(teamAlreadyInClub.getTeamId().toString()))).thenReturn(teamAlreadyInClub);
    }

    @Given("I am anywhere on the system")
    @WithMockUser()
    public void i_am_anywhere_on_the_system() throws Exception {
        mockMvc.perform(get("/home"));
    }

    @When("I click on a UI element to create a club")
    @WithMockUser()
    public void i_click_on_a_UI_element_to_create_a_club() throws Exception {
        mockMvc.perform(get("/createClub"));
    }

    @Then("I will see a form to create a club")
    @WithMockUser()
    public void i_will_see_a_form_to_create_a_club() throws Exception {
        mockMvc.perform(get("/createClub")).andExpect(status().isOk());
    }

    @Given("I am on the create club page")
    public void i_am_on_the_create_club_page() throws Exception {
        mockMvc.perform(get("/createClub"));
    }

    @When("I enter valid values for the name, address line, postcode, city and country  and optionally a logo")
    @WithMockUser()
    public void i_enter_valid_values_for_name_address_line_postcode_city_and_country_and_optionally_a_logo() throws Exception {
       mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "new club")
                       .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111"))
                .andExpect(status().isFound());

    }

    @Then("The club is created into the system")
    public void the_club_is_created_into_the_system() {
        verify(clubService, times(1)).updateOrAddClub(any());

    }


    @When("I enter an empty club name or a name with invalid characters for a club \\(e.g. non-alphanumeric other than dots or curly brackets, name made of only acceptable non-alphanumeric),")
    public void i_enter_an_empty_club_name_or_a_name_with_invalid_characters_for_a_club_e_g_non_alphanumeric_other_than_dots_or_curly_brackets_name_made_of_only_acceptable_non_alphanumeric() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "-1")
                        .param("name", "!@#$%")
                        .param("sport", "Hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111"));

    }

    @Then("An error message tells me the name is invalid")
    public void an_error_message_tells_me_the_name_is_invalid() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "!@#$%")
                .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")).andExpect(status().isBadRequest());
        verify(clubService, times(0)).updateOrAddClub(any());

    }

    @When("I enter either an empty location that is not addressline2 and suburb, or location with invalid characters \\(i.e. any non-letters except spaces, apostrophes and dashes),")
    public void i_enter_either_an_empty_location_that_is_not_addressline2_and_suburb_or_location_with_invalid_characters_i_e_any_non_letters_except_spaces_apostrophes_and_dashes() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "!@#$%")
                .param("sport", "Hockey")
                .param("addressLine1", "")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "")
                .param("country", "")
                .param("postcode", ""));
    }

    @Then("An error message tells me the location is invalid")
    public void an_error_message_tells_me_the_location_is_invalid() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "!@#$%")
                .param("sport", "Hockey")
                .param("addressLine1", "")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "")
                .param("country", "")
                .param("postcode", "")).andExpect(status().isBadRequest());
        verify(clubService, times(0)).updateOrAddClub(any());

    }

    @Given("I am on the create or edit club page")
    public void i_am_on_the_create_or_edit_club_page() throws Exception {
        mockMvc.perform(get("/createClub"));
    }

    @When("I am the manager of at least one team")
    public void i_am_the_manager_of_at_least_one_team() {
        team.setRole(user, Role.MANAGER);
    }

    @Then("I can select as many teams as I want from the list of teams I manage to be added to that club")
    public void i_can_select_as_many_teams_as_i_want_from_the_list_of_teams_i_manage_to_be_added_to_that_club() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                        .param("clubId", "-1")
                        .param("name", "new club")
                        .param("sport", "Hockey")
                        .param("addressLine1", "addressline1")
                        .param("addressLine2", "addressline2")
                        .param("suburb", "Ilam")
                        .param("city", "Christchurch")
                        .param("country", "New Zealand")
                        .param("postcode", "1111")
                        .param("selectedTeams", team.getTeamId().toString())).andExpect(status().isFound());
        verify(clubService, times(1)).updateOrAddClub(any());

    }

    @When("I select a team that belongs to another club")
    public void i_select_a_team_that_belongs_to_another_club() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "new club")
                .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")
                .param("selectedTeams", teamAlreadyInClub.getTeamId().toString()));
    }

    @Then("An error message tells me that team already belongs to another club")
    public void an_error_message_tells_me_that_team_already_belongs_to_another_club() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "new club")
                .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")
                .param("selectedTeams", teamAlreadyInClub.getTeamId().toString())).andExpect(status().isBadRequest());
        verify(clubService, times(0)).updateOrAddClub(any());

    }

    @When("I select teams that contain different sports")
    public void i_select_teams_that_contain_different_sports() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "new club")
                .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")
                .param("selectedTeams", team.getTeamId().toString(), teamDifferentSport.getTeamId().toString()));
    }

    @Then("An error message tells me that teams must have the same sport")
    public void an_error_message_tells_me_that_teams_must_have_the_same_sport() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "new club")
                .param("sport", "Hockey")
                .param("addressLine1", "addressline1")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "Christchurch")
                .param("country", "New Zealand")
                .param("postcode", "1111")
                .param("selectedTeams", team.getTeamId().toString(), teamDifferentSport.getTeamId().toString())).andExpect(status().isBadRequest());
        verify(clubService, times(0)).updateOrAddClub(any());

    }

    @Given("I am on the team’s profile page and the team belongs to a club,")
    public void i_am_on_the_team_s_profile_page_and_the_team_belongs_to_a_club() throws Exception {
        System.out.println(team.getTeamId());
        mockMvc.perform(get("/profile")
                .param("teamID", team.getTeamId().toString()))
                .andExpect(status().isOk());
    }

    @When("I click on the link to their club,")
    public void i_click_on_the_link_to_their_club() throws Exception {
        result=mockMvc.perform(get("/view-club").param("clubID",String.valueOf(teamsClub.getClubId())))
                .andExpect(status().isOk())
                .andExpect(view().name("viewClub"))
                .andReturn();
    }

    @Then("I will see their club details \\(Not sure if this is what the link does)")
    public void i_will_see_their_club_details_not_sure_if_this_is_what_the_link_does() {
        Club actualClub = (Club) result.getModelAndView().getModel().get("club");
        Assertions.assertEquals(teamsClub.getName(), actualClub.getName());
        Assertions.assertEquals(teamsClub.getLocation().toString(), actualClub.getLocation().toString());
        Assertions.assertEquals(teamsClub.getSport(), actualClub.getSport());

    }



    @When("I enter an empty club sport or a sport with invalid characters \\(i.e. any non-letters except spaces, apostrophes and dashes)")
    public void i_enter_an_empty_club_sport_or_a_sport_with_invalid_characters_i_e_any_non_letters_except_spaces_apostrophes_and_dashes() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "Club")
                .param("sport", "!@#$")
                .param("addressLine1", "")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "")
                .param("country", "")
                .param("postcode", ""));
    }

    @Then("An error message tells me the sport is invalid")
    public void an_error_message_tells_me_the_sport_is_invalid() throws Exception {
        mockMvc.perform(post("/createClub", 42L)
                .param("clubId", "-1")
                .param("name", "Club")
                .param("sport", "!@#$")
                .param("addressLine1", "")
                .param("addressLine2", "addressline2")
                .param("suburb", "Ilam")
                .param("city", "")
                .param("country", "")
                .param("postcode", "")).andExpect(status().isBadRequest());
    }

}
