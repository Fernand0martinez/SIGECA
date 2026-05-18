/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.logic.LogicUser;
import cr.ac.una.SIGECA.domain.User;
import jakarta.mail.MessagingException;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author crist
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LogicUser logUser;
    
    @Autowired
    private cr.ac.una.SIGECA.service.EmailService emailService;

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login/home";
    }

    @PostMapping("/authenticate")
    public String loginUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            Model model,
            HttpSession session) {

        if (username == null || password == null) {
            model.addAttribute("error", true);
            model.addAttribute("mensaje", "Debe ingresar usuario y contraseña");
            return "index";
        }

        String ruta = logUser.validateUser(username, password);

        switch (ruta) {
            case "NO_USER":
                model.addAttribute("error", true);
                model.addAttribute("mensaje", "Usuario no registrado");
                return "index";
            case "WRONG_PASS":
                model.addAttribute("error", true);
                model.addAttribute("mensaje", "Contraseña incorrecta");
                return "index";
            case "NOT_VERIFIED":
                return "redirect:/login/verify?username=" + username;
            default:
                User usuario = logUser.getUserById(username);
                session.setAttribute("usuarioLogueado", usuario);
                return ruta;
        }
    }

    @GetMapping("/verify")
    public String showVerifyPage(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "user/verify";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String username, @RequestParam String code, Model model) {
        if (logUser.verifyUser(username, code)) {
            model.addAttribute("mensaje", "Cuenta activada con éxito. Ya puedes iniciar sesión.");
            return "index";
        } else {
            model.addAttribute("error", "Código incorrecto. Inténtalo de nuevo.");
            model.addAttribute("username", username);
            return "user/verify";
        }
    }

    @GetMapping("/supervisor")
    public String cargarSupervisor() {
        return "supervisor";
    }

    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) String userId, Model model) {
        if (userId != null && !userId.isEmpty()) {
            User user = logUser.getUserById(userId);
            if (user == null) {
                return "redirect:/users/list?error=UsuarioNoEncontrado";
            }
            model.addAttribute("usuario", user);
            model.addAttribute("modo", "editar");
        } else {
            User user = new User();
            model.addAttribute("usuario", user );
            model.addAttribute("modo", "registrar");
        }
        return "user/form_user";
    }

    @PostMapping({"/register", "/registrar"})
    public String registerOrUpdateUser(
            @RequestParam(required = false) String userId,
            @RequestParam String nameUser,
            @RequestParam(required = false) String password,
            @RequestParam char role,
            @RequestParam String phone,
            @RequestParam String idCard,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String mail,
            @RequestParam int age,
            @RequestParam char gender) {

        if (userId != null && !userId.isEmpty()) {
            // Para editar
            User existingUser = logUser.getUserById(nameUser);
            if (existingUser == null) {
                return "redirect:/users/list?error=UsuarioNoEncontrado";
            } else {
                User newUser = new User(nameUser, password, role, phone, userId, name, lastName, mail, age, gender);
                boolean success = logUser.updateUser(newUser);
                return success
                        ? "redirect:/login/supervisor?cargarVista=usuarios"
                        : "redirect:/users/list?error=ErrorAlActualizar";
            }
        } else {
            // Para registrar
            if (password == null || password.isBlank()) {
                return "redirect:/login/form?error=PasswordRequerido";
            }
            User newUser = new User(nameUser, password, role, phone, idCard, name, lastName, mail, age, gender);

            // Validaciones personalizadas con redirección al formulario correcto
            if (logUser.existUser(idCard)) {
                return "redirect:/login/form?error=CedulaRepetida";
            }
            if (logUser.getUserById(nameUser) != null) {
                return "redirect:/login/form?error=UsuarioRepetido";
            }
            if (logUser.existsByMail(mail)) {
                return "redirect:/login/form?error=CorreoRepetido";
            }
            if (!newUser.getMail().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return "redirect:/login/form?error=EmailInvalido";
            }
            if (!newUser.getPhone().matches("^(\\+506)?[24678]\\d{7}$")) {
                return "redirect:/login/form?error=TelefonoInvalido";
            }

            // Generar código de verificación
            String code = logUser.generateVerificationCode();
            newUser.setVerificationCode(code);
            newUser.setVerified(false);

            boolean success = logUser.saveUser(newUser);
            if (success) {
                try {
                    emailService.sendVerificationEmail(newUser.getMail(), newUser.getName(), code);
                    return "redirect:/login/verify?username=" + newUser.getNameUser();
                } catch (MessagingException | IllegalStateException e) {
                    e.printStackTrace();
                    return "redirect:/login/form?error=ErrorAlEnviarCorreo";
                }
            } else {
                return "redirect:/login/form?error=ErrorAlGuardar";
            }
        }
    }

}
