package cr.ac.una.SIGECA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller that maps the root URL ("/") to the Thymeleaf template
 * located at src/main/resources/templates/index.html.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Returns the logical view name "index" (Thymeleaf will resolve to index.html)
        return "index";
    }
}
