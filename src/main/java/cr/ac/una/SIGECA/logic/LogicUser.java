/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.data.UserData;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.service.UserService;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author crist
 */
@Service
public class LogicUser {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public LogicUser(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    public boolean saveUser(User user) {
        // Validaciones de formato
        if (!isValidEmail(user.getMail())) {
            System.err.println("Email inválido: " + user.getMail());
            return false;
        }
        if (!isValidPhone(user.getPhone())) {
            System.err.println("Teléfono inválido: " + user.getPhone());
            return false;
        }

        // Verifica que no exista un usuario con la misma cédula, nombre de usuario o correo
        boolean existsIdCard = userService.existsByIdCard(user.getIdCard());
        boolean existsNameUser = userService.getUserByNameUser(user.getNameUser()) != null;
        boolean existsMail = userService.existsByMail(user.getMail());

        if (!existsIdCard && !existsNameUser && !existsMail) {
            user.setPassword(hashPassword(user.getPassword()));
            userService.save(user);
            return true;
        }

        return false;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        // Validación para teléfonos de Costa Rica (8 dígitos numéricos, opcionalmente con prefijo +506)
        return phone != null && phone.matches("^(\\+506)?[24678]\\d{7}$");
    }

    public List<User> getUser() {
        List<User> list = userService.getAll();
        if (list.isEmpty()) {
            System.out.println("Lista vacía");
        }
        return list;
    }

    public boolean updateUser(User user) {
        User existingUser = userService.getUserByNameUser(user.getNameUser());

        if (existingUser != null && existingUser.getIdCard().equals(user.getIdCard())) {
            user.setId(existingUser.getId()); // Asegúrate de asignar el ID del existente
            user.setVerified(existingUser.isVerified());
            user.setVerificationCode(existingUser.getVerificationCode());
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(hashPassword(user.getPassword()));
            }
            userService.save(user); // save actúa como update si el ID está seteado
            System.out.println("Se actualizo" + user.toString());
            return true;
        }
        return false;
    }

    public void deleteUser(String id_user) {
        User user = userService.getUserByNameUser(id_user);
        User u = getUserById(id_user);
        if (user != null) {
            userService.delete(u.getId());
        } else {
            System.err.println("No se encontró");
        }
    }

    public String validateUser(String id_user, String password) {
        User user = userService.getUserByNameUser(id_user);
        if (user == null) {
            return "NO_USER";
        }

        if (!passwordMatches(user, password)) {
            return "WRONG_PASS";
        }

        if (!user.isVerified()) {
            return "NOT_VERIFIED";
        }

        // Usuario y contraseña correctos
        UserData.user = user;
        switch (user.getRole()) {
            case 'A':
                return "administrador";
            case 'S':
                return "supervisor";
            default:
                return "usuario";
        }
    }

    public String generateVerificationCode() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }

    public boolean verifyUser(String username, String code) {
        User user = userService.getUserByNameUser(username);
        if (user != null && code.equals(user.getVerificationCode())) {
            user.setVerified(true);
            user.setVerificationCode(null);
            userService.save(user);
            return true;
        }
        return false;
    }

    public boolean existUser(String cedula) {
        return userService.existsByIdCard(cedula);
    }

    public boolean existsByMail(String mail) {
        return userService.existsByMail(mail);
    }

    public LinkedList<User> filterUser(String role, String gender) {
        List<User> usersList = userService.getAll();
        LinkedList<User> userFilter = new LinkedList<>();

        for (User user : usersList) {
            boolean matchesRole = (role == null || role.isEmpty() || user.getRole() == role.charAt(0));
            boolean matchesGender = (gender == null || gender.isEmpty() || user.getGender() == gender.charAt(0));

            if (matchesRole && matchesGender) {
                userFilter.add(user);
            }
        }

        return userFilter;
    }

    public User getUserById(String id_user) {
        return userService.getUserByNameUser(id_user);
    }

    private String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return rawPassword;
        }
        if (isBcryptHash(rawPassword)) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }

    private boolean passwordMatches(User user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        boolean legacyMatch = storedPassword.equals(rawPassword);
        if (legacyMatch) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userService.save(user);
        }
        return legacyMatch;
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]\\$.{56}$");
    }
}
