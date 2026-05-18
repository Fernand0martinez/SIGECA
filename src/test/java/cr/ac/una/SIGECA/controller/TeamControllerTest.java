package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.service.PlayerService;
import cr.ac.una.SIGECA.service.TeamService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class TeamControllerTest {

    private MockMvc mockMvc;
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = Mockito.mock(TeamService.class);
        Mockito.when(teamService.listAllTeams()).thenReturn(List.of());

        TeamController controller = new TeamController();
        ReflectionTestUtils.setField(controller, "teamService", teamService);
        ReflectionTestUtils.setField(controller, "playerService", Mockito.mock(PlayerService.class));
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listTeamsReturnsFragmentForAjaxRequests() throws Exception {
        mockMvc.perform(get("/teams/user/list").header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isOk())
                .andExpect(view().name("team/list_teams_user :: main"));
    }

    @Test
    void listTeamsReturnsFullViewForRegularRequests() throws Exception {
        mockMvc.perform(get("/teams/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("team/list_teams_user"));
    }
}
