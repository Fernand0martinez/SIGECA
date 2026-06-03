package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.service.PlayerService;
import cr.ac.una.SIGECA.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @GetMapping("/user/list")
    public String listTeamsUser(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            HttpSession session) {

        User loggedUser = getLoggedUser(session);
        model.addAttribute("title", "Mi Equipo");
        model.addAttribute("teams", teamService.listTeamsByOwner(loggedUser));

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "team/list_teams_user :: main";
        }

        return "team/list_teams_user";
    }

    @GetMapping("/user/form")
    public String showTeamFormUser(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            @RequestParam(value = "idTeam", required = false) Integer idTeam,
            HttpSession session) {

        User loggedUser = getLoggedUser(session);
        Team team = (idTeam != null && idTeam > 0)
                ? teamService.findById(idTeam)
                : new Team();

        if (team != null && team.getId() != null && !isOwner(team, loggedUser)) {
            model.addAttribute("error", "No puedes editar un equipo de otro usuario.");
            model.addAttribute("teams", teamService.listTeamsByOwner(loggedUser));
            return "team/team_table_user";
        }

        model.addAttribute("team", team);
        model.addAttribute("players", playerService.getAll());
        model.addAttribute("memberDorsals", team.getMembers().stream()
                .collect(Collectors.toMap(member -> member.getPlayer().getId(), member -> member.getDorsal())));
        model.addAttribute("selectedPlayerIds", team.getPlayers().stream().map(Player::getId).toList());
        model.addAttribute("selectedCaptainId", team.getCaptain() != null ? team.getCaptain().getId() : null);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "team/team_form_user :: contenido";
        }
        return "team/team_form_user";
    }

    @PostMapping("/user/save")
    public String saveTeamUser(
            @ModelAttribute Team team,
            @RequestParam(value = "playerIds", required = false) List<Integer> playerIds,
            @RequestParam(value = "captainId", required = false) Integer captainId,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        User loggedUser = getLoggedUser(session);
        Map<Integer, Integer> dorsalByPlayer = new HashMap<>();
        try {
            if (loggedUser == null) {
                throw new IllegalArgumentException("Debes iniciar sesion para crear un equipo.");
            }
            if (team.getId() != null) {
                Team existingTeam = teamService.findById(team.getId());
                if (!isOwner(existingTeam, loggedUser)) {
                    throw new IllegalArgumentException("No puedes modificar un equipo de otro usuario.");
                }
            }
            if (playerIds != null) {
                for (Integer playerId : playerIds) {
                    String dorsalValue = request.getParameter("dorsal_" + playerId);
                    if (dorsalValue != null && !dorsalValue.isBlank()) {
                        dorsalByPlayer.put(playerId, Integer.valueOf(dorsalValue));
                    }
                }
            }
            teamService.saveTeamWithMembers(team, playerIds, captainId, dorsalByPlayer, loggedUser);
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

        model.addAttribute("teams", teamService.listTeamsByOwner(loggedUser));
        return "team/team_table_user";
    }

    @PostMapping("/user/delete")
    public String deleteTeamUser(@RequestParam("id") int idTeam, Model model, HttpSession session) {
        User loggedUser = getLoggedUser(session);
        Team team = teamService.findById(idTeam);
        if (team != null && isOwner(team, loggedUser)) {
            teamService.deleteTeam(idTeam);
            model.addAttribute("success", "Team deleted");
        } else {
            model.addAttribute("error", "No puedes eliminar un equipo de otro usuario.");
        }
        model.addAttribute("teams", teamService.listTeamsByOwner(loggedUser));
        return "team/team_table_user";
    }

    @GetMapping("/user/filter")
    public String filterTeamsUser(@RequestParam(value = "query", required = false) String query, Model model, HttpSession session) {
        User loggedUser = getLoggedUser(session);
        List<Team> teams = teamService.filterTeamsByOwner(query, loggedUser);
        model.addAttribute("teams", teams);
        model.addAttribute("title", (query != null && !query.isBlank()) ? "Results for: " + query : "My Teams");
        model.addAttribute("noResults", teams.isEmpty());
        model.addAttribute("noResult", "No teams found");
        return "team/team_table_user";
    }

    private User getLoggedUser(HttpSession session) {
        Object user = session.getAttribute("usuarioLogueado");
        return user instanceof User loggedUser ? loggedUser : null;
    }

    private boolean isOwner(Team team, User user) {
        return team != null
                && team.getOwner() != null
                && user != null
                && Objects.equals(team.getOwner().getId(), user.getId());
    }
}
