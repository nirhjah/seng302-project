package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.ui.Model;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

/**
 * This controller handles errors thrown by our program.
 * (for example, 404s)
 */
@ControllerAdvice
public class CustomErrorController extends ResponseEntityExceptionHandler {
    private void populateModel(Model model, String pageName, String errorMessage) {
        model.addAttribute("pageName", pageName);
        model.addAttribute("errorMessage", errorMessage);
    }

    @ExceptionHandler({ ResponseStatusException.class })
    public String handleAccessDeniedException(Model model, Exception ex, HttpServletResponse response) {
        response.setStatus(404);
        populateModel(model, "404 Page not found", "Sorry, the page you requested could not be found.");
        return "error";
    }

    @ExceptionHandler({Exception.class})
    public String handleGenericException(Model model, Exception ex, HttpServletResponse response) {
        if (ex instanceof ErrorResponse e) {
            response.setStatus(e.getStatusCode().value());
        } else {
            response.setStatus(500);
        }
        populateModel(model, "Error", "Uh oh, an unknown error has occured!");
        logger.error("Unknown error", ex);
        return "error";
    }
}

