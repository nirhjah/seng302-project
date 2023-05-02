package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * This controller handles errors thrown by our program.
 * (for example, 404s)
 */

/*

@Controller
public class CustomErrorController implements ErrorController {
    Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handle404Error() {
        logger.info("inside the handler.");
        return "error";
    }
}

 */


@ControllerAdvice
public class CustomErrorController extends ResponseEntityExceptionHandler {
    private void populateModel(Model model, String pageName, String errorMessage) {
        model.addAttribute("pageName", pageName);
        model.addAttribute("errorMessage", errorMessage);
    }

    @ExceptionHandler({ ResponseStatusException.class })
    public String handleAccessDeniedException(Model model, Exception ex) {
        populateModel(model, "404 Page not found", "Sorry, the page you requested could not be found.");
        return "customError";
    }

    @ExceptionHandler({Exception.class})
    public String handleGenericException(Model model, Exception ex) {
        populateModel(model, "Error", "Uh oh, an unknown error has occured!");
        return "customError";
    }
}
