package nz.ac.canterbury.seng302.tab.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.tab.entity.Club;
import nz.ac.canterbury.seng302.tab.entity.Team;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.enums.Role;
import nz.ac.canterbury.seng302.tab.repository.TeamRepository;
import nz.ac.canterbury.seng302.tab.validator.TeamFormValidators;

/**
 * Spring Boot Service class for Team Service
 */
@Service
public class TeamService {

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
    public final String countryCitySuburbNameRegex = TeamFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX;

    /**
     * Addresses can have letters, numbers, spaces, commas, periods, hyphens,
     * forward slashes, apostrophes and pound signs. Must include
     * at least one alphanumeric character
     **/
    public final String addressRegex = TeamFormValidators.VALID_ADDRESS_REGEX;

    /**
     * Allow letters, numbers, forward slashes and hyphens. Must start with an
     * alphanumeric character.
     */
    public final String postcodeRegex = TeamFormValidators.VALID_POSTCODE_REGEX;

    /**
     * A team name can be alphanumeric, dots and curly braces. Must start with on
     * alphabetical character
     **/
    public final String teamNameUnicodeRegex = TeamFormValidators.VALID_TEAM_NAME_REGEX;

    /**
     * A sport can be letters, space, apostrophes or hyphens. Must start with on
     * alphabetical character
     **/
    public final String sportUnicodeRegex = TeamFormValidators.VALID_TEAM_SPORT_REGEX;

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
     * Method that finds paginated teams by city <strong>AND</strong> sports, using
     * a list of both to filter by selected by the user
     *
     * @param pageable          page object
     * @param searchedCities list of locations to filter by selected by the user
     * @param searchedSports    list of sports to filter by selected by the user
     * @param name              the team name or club name query inputted by the user
     * @return Page(s) of teams filtered by city/cities and sport/sports
     */
    public Page<Team> findPaginatedTeamsByCityAndSports(Pageable pageable, List<String> searchedCities, List<String> searchedSports, String name) {
        if (searchedCities == null) {
            searchedCities = List.of();
        } else {
            searchedCities = searchedCities.stream().map(String::toLowerCase).toList();
        }

        if (searchedSports == null) {
            searchedSports = List.of();
        } else {
            searchedSports = searchedSports.stream().map(String::toLowerCase).toList();
        }
        return teamRepository.findTeamByFilteredLocationsAndSports(pageable, searchedCities, searchedSports, name);
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

    public List<Team> findTeamsByClub(Club club) {
        long id = club.getClubId();
        return teamRepository.findTeamsByTeamClubClubId(id);
    }


    /**
     * Checks if a team has a club and return the club id if it does.
     * @param team the team which the method checks if it contains club
     * @return the club id if the team has a club
     */
    public Long getTeamClubId(Team team) {
        if (team.getTeamClub() == null) {
            return null;
        }
        return team.getTeamClub().getClubId();
    }

    /**
     * Gets a unique list of all the sports teams are in, alphabetically ordered.
     */
    public List<String> getAllTeamSports() {
        return teamRepository.getAllDistinctSports();
    }

    /**
     * Gets a unique list of all the cities teams are in, alphabetcally
     */
    public List<String> getAllTeamCities() {
        return teamRepository.getAllDistinctCities();
    }

    public Optional<Team> findTeamById(long id) {
        return teamRepository.findById(id);
    }


    /**
     * Gets list of teams that have matching sport, and match the club, or are not in a club at all
     * @param sport sport to match
     * @param club club to match
     * @return list of teams
     */
    public List<Team> findTeamsBySportAndClubOrNotInClub(String sport, Club club) { return teamRepository.findTeamsBySportAndClubOrNotInClub(sport, club); }

    /**
     * Gets list of teams that have matching sport and are not in a club at all
     * @param sport sport to match
     * @return list of teams
     */
    public List<Team> findTeamsBySportAndNotInClub(String sport) { return teamRepository.findTeamsBySportAndNotInClub(sport); }
}
