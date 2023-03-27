package nz.ac.canterbury.seng302.tab.controller;

import java.util.*;

import nz.ac.canterbury.seng302.tab.entity.Sport;
import nz.ac.canterbury.seng302.tab.service.SportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    UserService userService;

    @Autowired
    SportService sportService;

    private void prefillModel(Model model) {
        model.addAttribute("validNameRegex", UserFormValidators.VALID_NAME_REGEX);
        model.addAttribute("validNameMessage", UserFormValidators.INVALID_NAME_MSG);
    }

    @GetMapping("/editUser")
    public String getEditUserForm(
            EditUserForm editUserForm,
            Model model) {
        prefillModel(model);
        Optional<User> user = userService.getCurrentUser();
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        User u = user.get();
        editUserForm.prepopulate(u);
        Set<String> sports = new HashSet<>(sportService.getAllSportNames());
        model.addAttribute("knownSports", sportService.getAllSportNames());
        model.addAttribute("favouriteSports", u.getFavouriteSportNames());
        model.addAttribute("user", u);
        return "editUserForm";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(
            @Valid EditUserForm editUserForm,
            BindingResult bindingResult,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestParam("tags") List<String> tags,
            Model model, RedirectAttributes redirectAttributes) throws ServletException {

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
        }
        if (invalidSport) {
            redirectAttributes.addFlashAttribute("errorMessage", invalidTags);
            return "redirect:/editUser";
        }

        System.out.println(invalidTags);
        prefillModel(model);
        Optional<User> optUser = userService.getCurrentUser();
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();

        // Manual email uniqueness check
        if (userService.emailIsUsedByAnother(user, editUserForm.getEmail())) {
            bindingResult.addError(new FieldError("editUserForm", "email", "Email is already in use."));
        }

        if (bindingResult.hasErrors()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "editUserForm";
        }

        // Log-out if the user changes their email
        boolean shouldLogout = !user.getEmail().equals(editUserForm.getEmail());

        user.setFirstName(editUserForm.getFirstName());
        user.setLastName(editUserForm.getLastName());
        user.setEmail(editUserForm.getEmail());
        user.setDateOfBirth(editUserForm.getDateOfBirth());

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
        userService.updateOrAddUser(user);

        if (shouldLogout) {
            httpServletRequest.logout();
            return "redirect:login";
        }

        return "redirect:user-info/self";
    }
}
