package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicUser;
import cr.ac.una.SIGECA.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LogicUser logUser;

    @Autowired
    private EmailService emailService;

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
        }

        model.addAttribute("error", "Código incorrecto. Inténtalo de nuevo.");
        model.addAttribute("username", username);
        return "user/verify";
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
            return prepareUserForm(model, user, "editar", null);
        }

        return prepareUserForm(model, new User(), "registrar", null);
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
            @RequestParam char gender,
            Model model) {

        User formUser = new User(nameUser, password, role, phone, idCard, name, lastName, mail, age, gender);

        if (userId != null && !userId.isEmpty()) {
            User existingUser = logUser.getUserById(userId);
            if (existingUser == null) {
                return prepareUserForm(model, formUser, "editar", "UsuarioNoEncontrado");
            }

            User newUser = new User(nameUser, password, role, phone, userId, name, lastName, mail, age, gender);
            String editValidationError = validateUserInput(newUser, true);
            if (editValidationError != null) {
                return prepareUserForm(model, newUser, "editar", editValidationError);
            }

            boolean success = logUser.updateUser(newUser);
            return success
                    ? "redirect:/login/supervisor?cargarVista=usuarios"
                    : prepareUserForm(model, newUser, "editar", "ErrorAlActualizar");
        }

        if (password == null || password.isBlank()) {
            return prepareUserForm(model, formUser, "registrar", "PasswordRequerido");
        }

        String registerValidationError = validateUserInput(formUser, false);
        if (registerValidationError != null) {
            return prepareUserForm(model, formUser, "registrar", registerValidationError);
        }

        String code = logUser.generateVerificationCode();
        formUser.setVerificationCode(code);
        formUser.setVerified(false);

        boolean success = logUser.saveUser(formUser);
        if (!success) {
            return prepareUserForm(model, formUser, "registrar", "ErrorAlGuardar");
        }

        try {
            emailService.sendVerificationEmail(formUser.getMail(), formUser.getName(), code);
            return "redirect:/login/verify?username=" + formUser.getNameUser();
        } catch (MessagingException | IllegalStateException e) {
            e.printStackTrace();
            return prepareUserForm(model, formUser, "registrar", "ErrorAlEnviarCorreo");
        }
    }

    private String validateUserInput(User user, boolean editing) {
        if (!editing && logUser.existUser(user.getIdCard())) {
            return "CedulaRepetida";
        }
        if (!editing && logUser.getUserById(user.getNameUser()) != null) {
            return "UsuarioRepetido";
        }
        if (!editing && logUser.existsByMail(user.getMail())) {
            return "CorreoRepetido";
        }
        if (user.getMail() == null || !user.getMail().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            return "EmailInvalido";
        }
        if (user.getPhone() == null || !user.getPhone().matches("^(\\+506)?[24678]\\d{7}$")) {
            return "TelefonoInvalido";
        }
        return null;
    }

    private String prepareUserForm(Model model, User user, String mode, String errorCode) {
        model.addAttribute("usuario", user);
        model.addAttribute("modo", mode);
        if (errorCode != null && !errorCode.isBlank()) {
            model.addAttribute("errorCode", errorCode);
        }
        return "user/form_user";
    }
}
