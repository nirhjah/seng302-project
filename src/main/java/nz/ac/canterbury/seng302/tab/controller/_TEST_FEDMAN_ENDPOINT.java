package nz.ac.canterbury.seng302.tab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class _TEST_FEDMAN_ENDPOINT {
    
    @GetMapping("/fedman")
    public ResponseEntity<String> canFederationManagersSeeThis() {
        return ResponseEntity.ok("Wow look at that, you CAN access this endpoint!");
    }
}
