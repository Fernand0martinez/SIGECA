/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.logic.LogicUser;
import cr.ac.una.SIGECA.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author crist
 */
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private LogicUser logUser;

    @GetMapping("/list")
    public String listUser(HttpSession session, Model model, HttpServletRequest request) {
        User usuario = (User) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            // Si es AJAX, devuelve 401
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "fragments/error :: sesion-expirada"; // crea este fragmento
            }
            return "redirect:/login/iniciar";
        }

        List<User> users = logUser.getUser();
        model.addAttribute("users", users);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "user/list_user :: contenido";
        }

        return "user/list_user";
    }

    @GetMapping("/filter-fragment")
    public String filtrarFragmento(@RequestParam(required = false) String roleFilter,
            @RequestParam(required = false) String genderFilter,
            Model model) {
        List<User> users = logUser.filterUser(roleFilter, genderFilter);
        model.addAttribute("users", users);
        return "user/fragments/lista_usuario :: tbodyUsuarios";
    }

    @GetMapping("/edit")
    public String editarUserForm(@RequestParam String nameUser, Model model) {
        return "redirect:/login/form?userId=" + nameUser;
    }

    @PostMapping("/delete")
    public String eliminarUser(@RequestParam String id_user, Model model) {
        logUser.deleteUser(id_user);

        List<User> users = logUser.getUser(); // recargar lista de usuarios

        model.addAttribute("users", users);
        model.addAttribute("mensaje", "Usuario eliminado correctamente");
        model.addAttribute("tipo", "success");

        return "user/fragments/lista_usuario :: tbodyUsuarios";
    }

    @GetMapping("/details/{id_user}")
    public String showDetails(@PathVariable String id_user, Model model, HttpServletRequest request) {        // Se obtiene el usuario por su ID    
        User user = logUser.getUserById(id_user);          // Se obtiene el usuario por su ID
        model.addAttribute("user", user);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "user/detalles_user :: contenido";
        }
        return "user/detalles_user";
    }
}
