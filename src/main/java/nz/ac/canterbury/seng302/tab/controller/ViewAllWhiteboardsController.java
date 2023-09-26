package nz.ac.canterbury.seng302.tab.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.*;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
public class ViewAllWhiteboardsController {
    private static final int MAX_PAGE_SIZE = 10;
    
    public static final Sort SORT_BY_TEAM_NAME = Sort.by(
            Sort.Order.asc("name").ignoreCase()
    );

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WhiteboardRecordingService whiteboardRecordingService;

    public ViewAllWhiteboardsController(WhiteboardRecordingService whiteboardRecordingService) {
        this.whiteboardRecordingService = whiteboardRecordingService;
    }

    private Pageable getPageable(int page) {
        return PageRequest.of(page, MAX_PAGE_SIZE, SORT_BY_TEAM_NAME);
    }

    private Page<WhiteBoardRecording> getWhiteboardPage(int page, String currentSearch, List<String> sports) {
        var pageable = getPageable(page);
        return whiteboardRecordingService.findPaginatedWhiteboardsBySports(pageable, currentSearch, sports);
    }



    /**
     * Controller for seeing all whiteboards
     * @param page current page
     * @param currentSearch current search term
     * @param sports list of sports for filtering
     * @param model passes data to html
     * @param request httprequest
     * @return page of all viewable whiteboards.
     */
    @GetMapping("/view-whiteboards")
    public String allWhiteboards(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "currentSearch", required = false) String currentSearch,
            @RequestParam(name = "sports", required=false) List<String> sports,
            Model model, HttpServletRequest request) {
        logger.info("GET /view-whiteboards");
        model.addAttribute("httpServletRequest", request);

        int internalPageNo = pageNo - 1;
        // Dummy data
        Team t900 = new Team("Team 900", "Programming", new Location(null, null,
                null, "Chc", null, "nz"));
        List<WhiteBoardRecording> wbs = List.of(new WhiteBoardRecording("Test", t900), new WhiteBoardRecording("Hello", t900));

        // Replace withvalues from service + pagination
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("page", pageNo);
        model.addAttribute("listOfWhiteboards", wbs);
        model.addAttribute("listOfSports", List.of("hockey"));
        model.addAttribute("totalPages", 1);
        return "viewAllWhiteboards";
    }
}
