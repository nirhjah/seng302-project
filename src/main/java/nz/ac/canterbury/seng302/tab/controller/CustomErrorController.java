package nz.ac.canterbury.seng302.tab.controller;

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

    @ExceptionHandler({ ResponseStatusException.class })
    public String handleAccessDeniedException(
            Exception ex) {
        return "customError";
    }
}

