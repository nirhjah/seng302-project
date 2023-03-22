package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class ProxyController {
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/geocode/autocomplete")
    public ResponseEntity<String> autocomplete(@RequestParam("text") String text,
                                               @RequestParam("language") String language) {

        String apiKey = "5b3ce3597851110001cf624872c21df9f0f8419799b5718ed8769650";

        String endpoint = "https://api.openrouteservice.org/geocode/autocomplete" +
                "?text=" + text +
                "&api_key=" + apiKey +
                "&language=" + language;
        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);

        return response;
    }
}

