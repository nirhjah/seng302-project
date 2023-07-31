//package nz.ac.canterbury.seng302.tab.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import nz.ac.canterbury.seng302.tab.entity.Club;
//import nz.ac.canterbury.seng302.tab.entity.Location;
//import nz.ac.canterbury.seng302.tab.entity.Team;
//import nz.ac.canterbury.seng302.tab.entity.User;
//import nz.ac.canterbury.seng302.tab.form.CreateAndEditClubForm;
//import nz.ac.canterbury.seng302.tab.form.CreateAndEditCompetitionForm;
//import nz.ac.canterbury.seng302.tab.helper.exceptions.UnmatchedSportException;
//import nz.ac.canterbury.seng302.tab.service.ClubService;
//import nz.ac.canterbury.seng302.tab.service.TeamService;
//import nz.ac.canterbury.seng302.tab.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Spring Boot Controller class for the Create Competition Form
// */
//public class CreateCompetitionController {
//
//    Logger logger = LoggerFactory.getLogger(CreateCompetitionController.class);
//
//    private CompetitionService competitionService;
//
//    private final UserService userService;
//
//    private final TeamService teamService;
//
//    @Autowired
//    public CreateCompetitionController(CompetitionService competitionService,UserService userService, TeamService teamService) {
//        this.competitionService = competitionService;
//        this.userService = userService;
//        this.teamService = teamService;
//    }
//
//    /**
//     * Gets club form
//     * @param clubId  if editing, get club through clubId
//     * @param model   model
//     * @param request http request
//     * @param createAndUpdateCompetitionForm form data
//     * @return create club form
//     */
//    @GetMapping("/createCompetition")
//    public String clubForm(@RequestParam(name = "edit", required = false) Long clubId,
//                           Model model,
//                           HttpServletRequest request, CreateAndEditCompetitionForm createAndUpdateCompetitionForm) {
//
//        logger.info("GET /createCompetition");
//        prefillModel(model, request);
//
//        Competition competition;
//
//        if (competition != null) {
//            if ((competition = competitionService.findCompetitionById(competitionId).get()) != null) {
//                prefillModelWithClub(model, competition);
//            }
//        }
//        return "createCompetitionForm";
//    }
//
//
//    @PostMapping("/create")
//    public String createCompetition(
//            @RequestParam(name = "competitionId", defaultValue = "-1") long competitionId,
//            @RequestParam(name="name") String name,
//            @RequestParam(name = "sport") String sport,
//            @RequestParam(name = "gradeLevel") String gradeLevel gradeLevel,
//            @Validated CreateAndEditCompetitionForm createAndEditCompetitionForm,
//            BindingResult bindingResult,
//            HttpServletResponse httpServletResponse,
//            Model model,
//            HttpServletRequest httpServletRequest) throws IOException {
//        prefillModel(model, httpServletRequest);
//
//        long newCompetitionId = competitionId;
//
//        if (competitionId !=-1) {
//            Competition editCompetition = competitionService.findCompetitionById(competitionId).get();
//
//            if (bindingResult.hasErrors()) {
//                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                prefillModelWithClub(model, editClub);
//                return "createCompetitionForm";
//            }
//
//            editCompetition.setName(name);
//            editCompetition.setSport(sport);
//            editCompetition.setGradeLevel(gradeLevel);
//
//            competitionService.updateOrAddCompetition(editCompetition);
//
//        } else {
//
//            Competition competition = new Competition(name, sport, gradeLevel);
//
//            setTeamsClub(selectedTeams, club, bindingResult);
//
//            if (bindingResult.hasErrors()) {
//                System.out.println(bindingResult.getAllErrors());
//                httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                return "createCompetitionForm";
//            }
//
//            competitionService.updateOrAddCompetition(competition);
//
//            newCompetitionId = competition.getCompetitionId;
//
//        }
//
//        return "redirect:/viewCompetition?competitionId=" + newCompetitionId;
//    }
//
//    /**
//     * Prefills model with competition fields
//     * @param model model to be filled
//     * @param competition  club to get info from
//     */
//    public void prefillModelWithClub(Model model, Competition competition) {
//        model.addAttribute("competitionId", competition.getCompetitionId());
//        model.addAttribute("name", competition.getName());
//        model.addAttribute("sport", club.getSport());
//        model.addAttribute("gradeLevel", club.getGradeLevel());
//    }
//
//    /**
//     * Prefill model with info required from navbar, as well as the list of teams a user is manager of
//     * @param model model to be filled
//     */
//    public void prefillModel(Model model, HttpServletRequest httpServletRequest) {
//        Optional<User> user = userService.getCurrentUser();
//        List<Team> teamsUserManagerOf = new ArrayList<>();
//        for (Team team : teamService.getTeamList()) {
//            if (team.isManager(user.get())) {
//                teamsUserManagerOf.add(team);
//            }
//        }
//        model.addAttribute("httpServletRequest", httpServletRequest);
//        model.addAttribute("listOfTeams", teamsUserManagerOf);
//    }
//
//}
//}
