package nz.ac.canterbury.seng302.tab.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

    private void prefillModel(Model model) {
        model.addAttribute("validNameRegex", UserFormValidators.VALID_NAME_REGEX);
        model.addAttribute("validNameMessage", UserFormValidators.INVALID_NAME_MSG);
        model.addAttribute("postcodeRegex",UserFormValidators.VALID_POSTCODE_REGEX);
        model.addAttribute("postcodeRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("addressRegex",UserFormValidators.VALID_ADDRESS_REGEX);
        model.addAttribute("addressRegexMsg",UserFormValidators.INVALID_POSTCODE_MSG);
        model.addAttribute("countryCitySuburbNameRegex",UserFormValidators.VALID_COUNTRY_SUBURB_CITY_REGEX);
        model.addAttribute("countryCitySuburbNameRegexMsg",UserFormValidators.INVALID_COUNTRY_SUBURB_CITY_MSG);
        model.addAttribute("navTeams", teamService.getTeamList());
    }

    @GetMapping("/editUser")
    public String getEditUserForm(
            EditUserForm editUserForm,
            Model model,
            HttpServletRequest request) throws MalformedURLException {
        prefillModel(model);
        model.addAttribute("httpServletRequest",request);
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        User u = user.get();
        editUserForm.prepopulate(u);
        Set<String> sports = new HashSet<>(sportService.getAllSportNames());
        model.addAttribute("firstName", user.get().getFirstName());
        model.addAttribute("lastName", user.get().getLastName());
        model.addAttribute("displayPicture", user.get().getPictureString());
        model.addAttribute("knownSports", sportService.getAllSportNames());
        model.addAttribute("favouriteSports", u.getFavouriteSportNames());
        model.addAttribute("user", u);
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
        model.addAttribute("httpServletRequest",httpServletRequest);
        String invalidTags= "These are invalid sports: ";
        boolean first= true ,invalidSport=false;
        for (String tag : tags) {
            if (!tag.matches("^[\\p{L}\\s\\'\\-]+$")) {
                invalidSport=true;
                if (!first) {
                    invalidTags += ", ";
                } else {
                    first = false;
                }
                invalidTags += tag;
            }
            if (invalidSport) {
                redirectAttributes.addFlashAttribute("errorMessage", invalidTags);
                URL url = new URL(httpServletRequest.getRequestURL().toString());
                String path = (url.getPath() + "/..");
                model.addAttribute("path", path);
                return "redirect:/editUser";
            }
        }
            System.out.println(invalidTags);

        prefillModel(model);
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

        // Manual email uniqueness check
        if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
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
        //TODO set emailConfirmed to true

        userService.updateOrAddUser(user);

        if (shouldLogout) {
            httpServletRequest.logout();
            autoLogin.forceLogin(user.getEmail(), user.getAuthorities(), httpServletRequest);
        }

        return "redirect:user-info/self";
    }
}
