package nz.ac.canterbury.seng302.tab.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import nz.ac.canterbury.seng302.tab.authentication.AutoLogin;
import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.service.SportService;
import nz.ac.canterbury.seng302.tab.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.tab.entity.User;
import nz.ac.canterbury.seng302.tab.form.EditUserForm;
import nz.ac.canterbury.seng302.tab.service.UserService;
import nz.ac.canterbury.seng302.tab.validator.UserFormValidators;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EditUserFormController {

    private final Logger logger = LoggerFactory.getLogger(EditUserFormController.class);

    @Autowired
    TeamService teamService;
    @Autowired
    UserService userService;

    @Autowired
    SportService sportService;

    @Autowired
    private AutoLogin autoLogin;

    private void prefillModel(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute("validNameRegex", UserFormValidators.VALID_NAME_REGEX);
        model.addAttribute("validNameMessage", UserFormValidators.INVALID_NAME_MSG);
        model.addAttribute("postcodeRegex",UserFormValidators.VALID_POSTCODE_REGEX);
        model.addAttribute("postcodeRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("addressRegex",UserFormValidators.VALID_ADDRESS_REGEX);
        model.addAttribute("addressRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("countryCitySuburbNameRegex",UserFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg",UserFormValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);
        model.addAttribute("navTeams", teamService.getTeamList());
        model.addAttribute("httpServletRequest",httpServletRequest);
    }

    @GetMapping("/editUser")
    public String getEditUserForm(
            EditUserForm editUserForm,
            Model model,
            HttpServletRequest request) throws MalformedURLException {
        prefillModel(model, request);

        Optional<User> optionalUser = userService.getCurrentUser();
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optionalUser.get();
        editUserForm.prepopulate(user);
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("displayPicture", user.getPictureString());
        model.addAttribute("knownSports", sportService.getAllSportNames());
        model.addAttribute("favouriteSports", user.getFavouriteSportNames());
        model.addAttribute("user", user);
        URL url = new URL(request.getRequestURL().toString());
        String path = (url.getPath() + "/..");
        model.addAttribute("path", path);
        return "editUserForm";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(
            @Validated EditUserForm editUserForm,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestParam("tags") List<String> tags,
            Model model,
            RedirectAttributes redirectAttributes) throws ServletException, MalformedURLException {
        prefillModel(model, httpServletRequest);

        // Check that all the sports have valid names
        String invalidSports = tags.stream()
                .filter(tag -> !tag.matches("^[\\p{L}\\s\\'\\-]+$"))
                .collect(Collectors.joining(", "));
        if (!invalidSports.isEmpty()) {
            logger.info("Invalid sports: {}", invalidSports);
            String errorMessage = "These are invalid sports: " + invalidSports;
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            URL url = new URL(httpServletRequest.getRequestURL().toString());
            String path = (url.getPath() + "/..");
            model.addAttribute("path", path);
            return "redirect:/editUser";
        }

        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();

        List<Sport> newFavSports = new ArrayList<>();
        List<String> knownSportNames = sportService.getAllSportNames();
        List<Sport> knownSports = sportService.getAllSports();

        for (String tag : tags) {
            if (knownSportNames.contains(tag)){
                int index = knownSportNames.indexOf(tag);
                newFavSports.add(knownSports.get(index));
            } else {
                newFavSports.add(new Sport(tag));
            }
        }

        user.setFavoriteSports(newFavSports);

        if (!editUserForm.getEmail().matches(UserFormValidators.VALID_EMAIL_REGEX)) {
            // bindingResult.addError(new FieldError("editUserForm", "email", UserFormValidators.WELL_FORMED_EMAIL));
        } else if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
            bindingResult.addError(new FieldError("editUserForm", "email", "Email is already in use."));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            model.addAttribute("favouriteSports", user.getFavouriteSportNames());
            URL url = new URL(httpServletRequest.getRequestURL().toString());
            String path = (url.getPath() + "/..");
            model.addAttribute("path", path);
            return "editUserForm";
        }

        // Log-out if the user changes their email
        boolean shouldLogout = !user.getEmail().equals(editUserForm.getEmail());

        user.setFirstName(editUserForm.getFirstName());
        user.setLastName(editUserForm.getLastName());
        user.setEmail(editUserForm.getEmail());
        user.setDateOfBirth(editUserForm.getDateOfBirth());
        user.getLocation().setAddressLine1(editUserForm.getAddressLine1());
        user.getLocation().setAddressLine2(editUserForm.getAddressLine2());
        user.getLocation().setCity(editUserForm.getCity());
        user.getLocation().setCountry(editUserForm.getCountry());
        user.getLocation().setSuburb(editUserForm.getSuburb());
        user.getLocation().setPostcode(editUserForm.getPostcode());

        userService.updateOrAddUser(user);

        if (shouldLogout) {
            httpServletRequest.logout();
            autoLogin.forceLogin(user.getEmail(), user.getAuthorities(), httpServletRequest);
        }

        return "redirect:user-info/self";
    }
}
