package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/connection")
public class TestConnectionController {

    /**
     * Dummy GET method to test server connection.
     * @return message
     */
    @GetMapping("/")
    public ResponseEntity<String> testConnection(){

        return ResponseEntity.ok("Successful");
    }
}
