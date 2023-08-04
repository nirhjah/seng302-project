package nz.ac.canterbury.seng302.tab.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * TODO: This is just a test controller
 */
@Controller
public class TESTDropdown {
    
    /**
     * TODO: This is just a test method.
     */
    public List<String> getAllSportsFromJSON() {

        return List.of(    "5K run",
            "Abseiling",
            "Acrobatic gymnastics",
            "Aerobatics",
            "Aerobic gymnastics",
            "Aid climbing",
            "Aiki-jūjutsu",
            "Aikido",
            "Air hockey",
            "Air racing",
            "Air sports",
            "Airsoft",
            "All-terrain vehicle competition",
            "Alpine skiing",
            "Amateur wrestling",
            "American football",
            "Angling",
            "Apnoea finswimming",
            "Archery",
            "Arm wrestling",
            "Artistic billiards",
            "Artistic cycling",
            "Artistic gymnastics",
            "Association football"
        );
    }

    @GetMapping("TEST-dropdown")
    public String testDropdown(Model model, HttpServletRequest request) {
        model.addAttribute("httpServletRequest", request);
        model.addAttribute("items", getAllSportsFromJSON());
        model.addAttribute("secondItems", List.of("One", "Two", "Three", "Four", "Five!"));
        return "testFilterDropdownFragment";
    }
}
