package nz.ac.canterbury.seng302.tab.service;

import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.TeamRole;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.mail.EmailService;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot Service class for Team Service
 */
@Service
public class TeamService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Countries and cities can have letters from all alphabets, with hyphens,
     * apostrophes and
     * spaces. Must start with an alphabetical character
     */
    public final String countryCitySuburbNameRegex = "^\\p{L}+[\\- '\\p{L}]*$";

    /**
     * Addresses can have letters, numbers, spaces, commas, periods, hyphens,
     * forward slashes, apostrophes and pound signs. Must include
     * at least one alphanumeric character
     **/
    public final String addressRegex = "^(?=.*[\\p{L}\\p{N}])(?:[\\- ,./#'\\p{L}\\p{N}])*$";

    /**
     * Allow letters, numbers, forward slashes and hyphens. Must start with an
     * alphanumeric character.
     */
    public final String postcodeRegex = "^[\\p{L}\\p{N}]+[\\-/\\p{L}\\p{N}]*$";

    /**
     * A team name can be alphanumeric, dots and curly braces. Must start with on
     * alphabetical character
     **/
    public final String teamNameUnicodeRegex = "^[\\p{L}\\p{N}\\s]+[}{.\\p{L}\\p{N}\\s]+$";

    /**
     * A sport can be letters, space, apostrophes or hyphens. Must start with on
     * alphabetical character
     **/
    public final String sportUnicodeRegex = "^\\p{L}+[\\- '\\p{L}]*$";

    public List<Team> getTeamList() {
        return teamRepository.findAll();
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public Team getTeam(long teamID) {
        return teamRepository.findById(teamID).orElse(null);
    }

    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    public Page<Team> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return teamRepository.findAll(pageable);
    }

    /**
     * Method which updates the picture by taking the MultipartFile type and
     * updating the picture
     * stored in the team with id primary key.
     *
     * @param file MultipartFile file upload
     * @param id   Team's unique id
     */
    public void updatePicture(MultipartFile file, long id) {
        Team team = teamRepository.findById(id).get();

        // Gets the original file name as a string for validation
        String pictureString = StringUtils.cleanPath(file.getOriginalFilename());
        if (pictureString.contains("..")) {
            System.out.println("not a valid file");
        }
        try {
            // Encodes the file to a byte array and then convert it to string, then set it
            // as the pictureString variable.
            team.setPictureString(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Saved the updated picture string in the database.
        teamRepository.save(team);
    }

    /**
     * Method that finds paginated teams by city <strong>AND</strong> sports, using
     * a list of both to filter by selected by the user
     *
     * @param pageable          page object
     * @param searchedLocations list of locations to filter by selected by the user
     * @param searchedSports    list of sports to filter by selected by the user
     * @param name              the team name query inputted by the use
     * @return Page(s) of teams filtered by city/cities and sport/sports
     */
    public Page<Team> findPaginatedTeamsByCityAndSports(Pageable pageable, List<String> searchedLocations, List<String> searchedSports, String name) {

        if (searchedLocations == null) {
            searchedLocations = List.of();
        } else {
            searchedLocations = searchedLocations.stream().map(String::toLowerCase).toList();
        }

        if (searchedSports == null) {
            searchedSports = List.of();
        } else {
            searchedSports = searchedSports.stream().map(String::toLowerCase).toList();
        }
        return teamRepository.findTeamByFilteredLocationsAndSports(pageable, searchedLocations, searchedSports, name);
    }

    public Optional<Team> findByToken(String token) {
        return teamRepository.findByToken(token);
    }


    /**
     * gets a page of all teams the given user is a member of
     * @param user          user to filter teams by
     * @return              all teams the user is apart of
     */
    public Page<Team> findTeamsByUser(int pageNo, int pageSize, User user) {

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        return teamRepository.findTeamsWithUser(user, pageable);
    }

    /**
     * @param sport the sport that the user will input in the create teams/edit
     *              teams page
     * @return true if the sport is valid and matches the regex
     */
    public boolean isValidSport(String sport) {
        return (sport.matches(sportUnicodeRegex));
    }

    /**
     * @param name the team name that the user inputs in the create/edit teams page
     * @return True if the team name matches the valid regex
     */
    public boolean isValidTeamName(String name) {

        return (name.matches(teamNameUnicodeRegex));
    }

    /**
     * @param location this could either be the city or country that the user
     *                 inputs for the create/edit teams
     * @return true if the string matches the regex
     */
    public boolean isValidCountryCityName(String location) {

        return (location.matches(countryCitySuburbNameRegex));
    }

    /**
     * @param suburb the suburb that the user
     *               inputs for the create/edit teams
     * @return true if the string matches the regex
     */
    public boolean isValidSuburb(String suburb) {

        return (suburb.matches(countryCitySuburbNameRegex) || suburb.isEmpty()); // suburbs can be empty
    }

    /**
     * @param postcode the postcode for the team location
     * @return true if the postcode has only letters numbers and slashes and starts
     * with an alphanumeric
     */
    public boolean isValidPostcode(String postcode) {

        return (postcode.matches(postcodeRegex) || postcode.isEmpty());
    }

    /**
     * @param addressline either address line 1 or address line 2 from the
     *                    create/edit teams page
     * @return true if the address line matches the regex
     */
    public boolean isValidAddressLine(String addressline) {

        return (addressline.matches(addressRegex) || addressline.isEmpty());
    }

    /**
     * @param country
     * @param city
     * @param postcode
     * @param suburb
     * @param addressline1
     * @param addressline2
     * @return true if all the params match their respective regex's
     */
    public boolean isValidLocation(String country, String city, String postcode, String suburb, String addressline1,
                                   String addressline2) {
        boolean isvalid = isValidCountryCityName(country) && isValidCountryCityName(city)
                && isValidSuburb(suburb) && isValidPostcode(postcode) && isValidAddressLine(addressline1)
                && isValidAddressLine(addressline2);
        return isvalid;
    }

    /**
     * Validates registering a team
     */
    public boolean validateTeamRegistration(String sport, String name, String country, String city,
                                            String postcode,
                                            String suburb, String addressline1, String addressline2) {

        return isValidSport(sport) && isValidTeamName(name)
                && isValidLocation(country, city, postcode, suburb, addressline1, addressline2);
    }

    /**
     * clips extra whitespace off the end of the string and removes double ups of
     * whitespace
     *
     * @param string
     * @return string that is stripped from double up whitespace and whitespace at
     * the end and start of the string
     */
    public String clipExtraWhitespace(String string) {

        // checks if there is a double whitespace inside the string
        String filtered = string.trim().replaceAll("\\s+", " ");
        return filtered;

    }

    public boolean userRolesAreValid(List<String> userRoles) {
        int numOfManagers = Collections.frequency(userRoles, Role.MANAGER.toString());
        return ((numOfManagers > 0) && (numOfManagers <=3));
    }

    public List<Team> findTeamsWithUser(User user) {return teamRepository.findTeamsWithUser_List(user);}
}
