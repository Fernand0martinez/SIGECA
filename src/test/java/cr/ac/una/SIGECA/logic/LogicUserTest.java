package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogicUserTest {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private LogicUser logicUser;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        logicUser = new LogicUser(userService, passwordEncoder);
    }

    @Test
    void testSaveUser_DuplicateEmail() {
        User user = new User();
        user.setMail("test@example.com");
        user.setIdCard("12345");
        user.setNameUser("testuser");
        user.setPhone("22222222");

        when(userService.existsByMail("test@example.com")).thenReturn(true);

        boolean result = logicUser.saveUser(user);

        assertFalse(result, "Should return false when email already exists");
        verify(userService, never()).save(any(User.class));
    }

    @Test
    void testSaveUser_UniqueEmail() {
        User user = new User();
        user.setMail("new@example.com");
        user.setIdCard("12345");
        user.setNameUser("newuser");
        user.setPhone("22222222");

        when(userService.existsByMail("new@example.com")).thenReturn(false);
        when(userService.existsByIdCard("12345")).thenReturn(false);
        when(userService.getUserByNameUser("newuser")).thenReturn(null);

        boolean result = logicUser.saveUser(user);

        assertTrue(result, "Should return true when email is unique");
        verify(userService, times(1)).save(user);
    }
}
