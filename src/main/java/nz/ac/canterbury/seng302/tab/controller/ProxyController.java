package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

/**
 * This class acts as a proxy for the OpenRouteService Geocoding API. It sends HTTP GET requests to the API's
 * "geocode/autocomplete" endpoint, passing along query parameters for text, API key, and language.
 */
@Controller
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String opsApiKey = System.getenv().get("OPS_API_KEY");

    /**
     * Sends an HTTP GET request to the OpenRouteService Geocoding API's "geocode/autocomplete" endpoint, passing
     * along the given input text, API key, and language. Returns a ResponseEntity<String> object containing the
     * response body as a string.
     *
     * @param text the input text to search for matching addresses or places
     * @param language the language code to use for the response
     * @return a ResponseEntity<String> object containing the API's response body as a string
     */
    @GetMapping("/geocode/autocomplete")
    public ResponseEntity<String> autocomplete(@RequestParam("text") String text,
                                               @RequestParam("language") String language) {

        String endpoint = "https://api.openrouteservice.org/geocode/autocomplete" +
                "?text=" + text +
                "&api_key=" + opsApiKey +
                "&language=" + language;

        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        return response;
    }
}


