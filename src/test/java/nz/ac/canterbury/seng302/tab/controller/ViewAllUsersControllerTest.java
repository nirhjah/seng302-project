package nz.ac.canterbury.seng302.tab.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
@WebMvcTest(ViewAllUsersController.class)
class ViewAllUsersControllerTest {

    @MockBean
    private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;


}