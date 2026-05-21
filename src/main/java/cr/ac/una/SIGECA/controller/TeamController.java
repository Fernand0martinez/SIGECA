package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.service.PlayerService;
import cr.ac.una.SIGECA.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    // LISTADO PARA USUARIO
    @GetMapping("/user/list")
    public String listTeamsUser(Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith
    ) {
        model.addAttribute("title", "Mi Equipo");
        model.addAttribute("teams", teamService.listAllTeams());

        if ("XMLHttpRequest".equals(requestedWith)) {
            // solo el <main th:fragment="main">…
            return "team/list_teams_user :: main";
        }
       
        return "team/list_teams_user";
    }

    // FORMULARIO 
    @GetMapping("/user/form")
    public String showTeamFormUser(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            @RequestParam(value = "idTeam", required = false) Integer idTeam) {

        Team team = (idTeam != null && idTeam > 0)
                ? teamService.findById(idTeam)
                : new Team();
        model.addAttribute("team", team);

        List<Player> players = playerService.getAll();
        model.addAttribute("players", players);
        model.addAttribute("memberDorsals", team.getMembers().stream()
                .collect(Collectors.toMap(member -> member.getPlayer().getId(), member -> member.getDorsal())));
        model.addAttribute("selectedPlayerIds", team.getPlayers().stream().map(Player::getId).toList());
        model.addAttribute("selectedCaptainId", team.getCaptain() != null ? team.getCaptain().getId() : null);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "team/team_form_user :: contenido";
        }
        return "team/team_form_user";
    }

    // SAVE, DELETE y FILTER devuelven solo la tabla
    @PostMapping("/user/save")
    public String saveTeamUser(
            @ModelAttribute Team team,
            @RequestParam(value = "playerIds", required = false) List<Integer> playerIds,
            @RequestParam(value = "captainId", required = false) Integer captainId,
            HttpServletRequest request,
            Model model) {
        Map<Integer, Integer> dorsalByPlayer = new HashMap<>();
        try {
            if (playerIds != null) {
                for (Integer playerId : playerIds) {
                    String dorsalValue = request.getParameter("dorsal_" + playerId);
                    if (dorsalValue != null && !dorsalValue.isBlank()) {
                        dorsalByPlayer.put(playerId, Integer.valueOf(dorsalValue));
                    }
                }
            }
            teamService.saveTeamWithMembers(team, playerIds, captainId, dorsalByPlayer);
            model.addAttribute("success", "Team saved successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("team", team);
            model.addAttribute("players", playerService.getAll());
            model.addAttribute("memberDorsals", dorsalByPlayer);
            model.addAttribute("selectedPlayerIds", playerIds != null ? playerIds : List.of());
            model.addAttribute("selectedCaptainId", captainId);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "team/team_form_user :: contenido";
            }
            return "team/team_form_user";
        }
        model.addAttribute("teams", teamService.listAllTeams());        
        return "team/team_table_user";
    }

    @PostMapping("/user/delete")
    public String deleteTeamUser(@RequestParam("id") int idTeam, Model model) {
        teamService.deleteTeam(idTeam);
        model.addAttribute("success", "Team deleted");
        model.addAttribute("teams", teamService.listAllTeams());
        return "team/team_table_user";
    }

    @GetMapping("/user/filter")
    public String filterTeamsUser(@RequestParam(value = "query", required = false) String q, Model model) {
        List<Team> teams = teamService.filterTeams(q);
        model.addAttribute("teams", teams);
        model.addAttribute("title",
                (q != null && !q.isBlank()) ? "Results for: " + q : "My Teams");
        model.addAttribute("noResults", teams.isEmpty());
        model.addAttribute("noResult", "No teams found");
        return "team/team_table_user";
    }
    
}
