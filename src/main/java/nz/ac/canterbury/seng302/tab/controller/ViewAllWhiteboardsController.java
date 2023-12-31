package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.tab.entity.WhiteBoardRecording;
import nz.ac.canterbury.seng302.tab.service.video.WhiteboardRecordingService;
@Controller
public class ViewAllWhiteboardsController {
    private static final int MAX_PAGE_SIZE = 10;
    
    public static final Sort SORT_BY_NAME = Sort.by(
            Sort.Order.asc("name").ignoreCase()
    );

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WhiteboardRecordingService whiteboardRecordingService;

    public ViewAllWhiteboardsController(WhiteboardRecordingService whiteboardRecordingService) {
        this.whiteboardRecordingService = whiteboardRecordingService;
    }

    private Pageable getPageable(int page) {
        return PageRequest.of(page, MAX_PAGE_SIZE, SORT_BY_NAME);
    }

    private Page<WhiteBoardRecording> getWhiteboardPage(int page, String currentSearch, List<String> sports) {
        var pageable = getPageable(page);
        return whiteboardRecordingService.findPublicPaginatedWhiteboardsBySports(pageable, currentSearch, sports);
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

        int internalPageNo = Math.max(0, pageNo - 1);
       
        Page<WhiteBoardRecording> wbs = getWhiteboardPage(internalPageNo, currentSearch, sports);
        
        // Values for pagination
        model.addAttribute("totalPages", wbs.getTotalPages());
        model.addAttribute("page", internalPageNo + 1);
        // Values for searching
        model.addAttribute("currentSearch", currentSearch);
        model.addAttribute("listOfWhiteboards", wbs.toList());
        List<String> allSports = whiteboardRecordingService.getAllPublicWhiteboardSports();
        model.addAttribute("listOfSports", allSports);
        return "viewAllWhiteboards";
    }

}
