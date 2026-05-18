package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.data.UserData;
import cr.ac.una.SIGECA.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AdministradorController {

    @GetMapping({"/admin/profile", "/users/profile", "/supervisors/profile"})
    public String showDetails(Model model, HttpServletRequest request) {
        User user = UserData.getUser();
        model.addAttribute("user", user);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "perfil :: contenido";
        }
        return "perfil";
    }

}
