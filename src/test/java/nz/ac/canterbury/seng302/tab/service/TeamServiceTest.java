package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Location;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DataJpaTest
@Import(TeamService.class)
public class TeamServiceTest {

    Logger logger = LoggerFactory.getLogger(TeamServiceTest.class);

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    Location location = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
    Location location2 = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");
    Location location3 = new Location("1 Test Lane", "", "Ilam", "Christchurch", "8041", "New Zealand");

    @Test
    public void testGettingTeamList() throws IOException {
        List<Team> teamList = teamService.getTeamList();
        assertTrue(teamList.isEmpty());
        Team team = new Team("test", "Hockey", location);
        Team team2 = new Team("test2", "Netball", location2);
        Team team3 = new Team("test3", "Cricket", location3);
        List<Team> list = Arrays.asList(team, team2, team3);
        teamRepository.save(team);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamService.getTeamList();

        assertEquals(list.toString(), teamService.getTeamList().toString());
    }

    @Test
    public void testAddingTeam() throws IOException {
        Team team = new Team("test", "Hockey", location);
        teamService.addTeam(team);
        assertEquals(team.getName(), teamRepository.findById(team.getTeamId()).get().getName());
        assertEquals(team.getLocation().getAddressLine1(),
                teamRepository.findById(team.getTeamId()).get().getLocation().getAddressLine1());
        assertEquals(team.getSport(), teamRepository.findById(team.getTeamId()).get().getSport());
    }

    @Test
    public void testUpdatingPicture() throws IOException {
        Team team = new Team("test", "Hockey", location);
        teamRepository.save(team);

        Resource resource = new ClassPathResource("/static/image/default-profile.png");
        File file = resource.getFile();
        String pictureString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        try (FileInputStream input = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile("file",
                    file.getName(), "image/png", input.readAllBytes());
            teamService.updatePicture(multipartFile, team.getTeamId());
            assertEquals(pictureString, Base64.getEncoder().encodeToString(multipartFile.getBytes()));
        }
    }

    @Test
    public void givenAllFieldsValid_WhenTeamEditedOrCreated_ValidationReturnsTrue() throws IOException {

        // call the service validation
        String validSport = "Rugby";
        String validName = "AllBlacks";
        String validCountry = "New Zealand";
        String validCity = "Christchurch";
        String validPostcode = "";
        String validSuburb = "Papauni";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        logger.info("should be true: " + teamService.validateTeamRegistration(validSport, validName, validCountry,
                validCity, validPostcode, validSuburb, validAddressLine1, validAddressLine2));
        boolean isTestValid = teamService.validateTeamRegistration(validSport, validName, validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(true, isTestValid);
    }

    /**
     * Tests the TeamService Validation with an invalid sport
     * 
     **/
    @Test
    public void givenInvalidSportCharacter_WhenTeamEdited_ValidationReturnsFalse() throws IOException {

        // call the service validation
        String invalidSport = "%";
        String validName = "AllBlacks";
        String validCountry = "New Zealand";
        String validCity = "Christchurch";
        String validPostcode = "";
        String validSuburb = "Papauni";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        boolean isTestValid = teamService.validateTeamRegistration(invalidSport, validName, validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(false, isTestValid);
    }

    /**
     * Tests the TeamService Validation with an invalid team name
     * 
     **/
    @Test
    public void givenInvalidNameCharacter_WhenTeamEdited_ValidationReturnsFalse() throws IOException {

        // call the service validation
        String invalidSport = "Rugby";
        String validName = "@ll Blacks";
        String validCountry = "New Zealand";
        String validCity = "Christchurch";
        String validPostcode = "";
        String validSuburb = "Papauni";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        boolean isTestValid = teamService.validateTeamRegistration(invalidSport, validName, validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(false, isTestValid);
    }

    /**
     * Tests the TeamService Validation with an invalid country
     * 
     **/
    @Test
    public void givenInvalidCountryCharacter_WhenTeamEdited_ValidationReturnsFalse() throws IOException {

        // call the service validation
        String invalidSport = "Rugby";
        String validName = "All Blacks";
        String validCountry = "New|Zealand";
        String validCity = "Christchurch";
        String validPostcode = "";
        String validSuburb = "Papauni";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        boolean isTestValid = teamService.validateTeamRegistration(invalidSport, validName, validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(false, isTestValid);
    }

    /**
     * Tests the TeamService Validation with an invalid city
     * 
     **/
    @Test
    public void givenInvalidCityCharacter_WhenTeamEdited_ValidationReturnsFalse() throws IOException {

        // call the service validation
        String invalidSport = "Rugby";
        String validName = "All Blacks";
        String validCountry = "New Zealand";
        String validCity = "#";
        String validPostcode = "";
        String validSuburb = "Papauni";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        boolean isTestValid = teamService.validateTeamRegistration(invalidSport, validName, validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(false, isTestValid);
    }

    /**
     * Tests the TeamService Validation with an invalid suburb
     * 
     **/
    @Test
    public void givenInvalidSuburbCharacter_WhenTeamEdited_ValidationReturnsFalse() throws IOException {

        // call the service validation
        String invalidSport = "Rugby";
        String validName = "All Blacks";
        String validCountry = "New Zealand";
        String validCity = "Christchurch";
        String validPostcode = "";
        String invalidSuburb = "$";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        boolean isTestValid = teamService.validateTeamRegistration(invalidSport, validName, validCountry, validCity,
                validPostcode, invalidSuburb, validAddressLine1, validAddressLine2);
        assertEquals(false, isTestValid);
    }

    @Test
    public void givenSportWithTrailingWhitespace_WhenTeamSubmitted_TrailingWhitespaceRemovedAndValidationReturnsTrue()
            throws IOException {
        String validSportWithTrailingWhitespace = "Football   ";
        String validTeamName = "All Whites";
        String validCountry = "New Zealand";
        String validCity = "Auckland";
        String validPostcode = "";
        String validSuburb = "";
        String validAddressLine1 = "";
        String validAddressLine2 = "";

        String actualSportName = teamService.clipExtraWhitespace(validSportWithTrailingWhitespace);
        boolean isTestValid = teamService.validateTeamRegistration(validSportWithTrailingWhitespace, validTeamName,
                validCountry, validCity,
                validPostcode, validSuburb, validAddressLine1, validAddressLine2);
        assertEquals(true, isTestValid);
        assertEquals("Football", actualSportName);

    }

    @Test
    public void givenAllInputsValid_whenTrimmingWhitespace_noInputschanged() throws IOException {

        List<String> validInputs = new ArrayList<>();
        validInputs.add("Football");
        validInputs.add("Cams team");
        validInputs.add("");
        validInputs.add("Ice Hockey");
        validInputs.add("rugby");
        for (String item : validInputs) {
            item = teamService.clipExtraWhitespace(item);
        }
        List<String> expectedInputs = new ArrayList<>();
        expectedInputs.add("Football");
        expectedInputs.add("Cams team");
        expectedInputs.add("");
        expectedInputs.add("Ice Hockey");
        expectedInputs.add("rugby");
        assertEquals(expectedInputs, validInputs);

    }
}
