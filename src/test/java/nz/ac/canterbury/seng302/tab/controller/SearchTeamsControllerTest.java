package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

class SearchTeamsControllerTest {


    @WithMockUser
    @Test
    public void searchTeams_testSearch_withOneCity() {

    }

    @WithMockUser
    @Test
    public void searchTeams_testSearch_withMultipleCities() {

    }

    @WithMockUser
    @Test
    public void searchTeams_testSearch_withNoCities() {
        // should return all teams

    }


    @WithMockUser
    @Test
    public void searchTeams_testSearch_withAllCities() {
        // should also return all teams

    }


    @WithMockUser
    @Test
    public void searchTeams_testDeselect_deselectOneCity() {

    }


    @WithMockUser
    @Test
    public void searchTeams_testDeselect_deselectAllCities() {
        // should remove all filters and return all cities

    }

}