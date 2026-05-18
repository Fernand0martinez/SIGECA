package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.service.PlayerService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class PlayerControllerTest {

    private MockMvc mockMvc;
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        playerService = Mockito.mock(PlayerService.class);
        PlayerController.players = List.of(new Player());
        PlayerController controller = new PlayerController(playerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void adminListReturnsFragmentForAjaxRequests() throws Exception {
        mockMvc.perform(get("/players/admin/list").header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("player/list_players :: contenido"));
    }

    @Test
    void adminCreateReturnsFragmentForAjaxRequests() throws Exception {
        mockMvc.perform(get("/players/admin/create").header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("player/form_jugador :: contenido"));
    }
}
