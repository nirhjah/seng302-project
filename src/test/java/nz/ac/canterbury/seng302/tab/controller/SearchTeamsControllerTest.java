package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;

class SearchTeamsControllerTest {

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        for (int i=1; i<1000; i++) {
        }
    }

    @WithMockUser
    @Test
    public void searchTeams_testSearch_withOneCity() {
        mvc.perform(post("/searchTeams?citys=[]")
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