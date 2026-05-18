package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.service.PlayerService;
import cr.ac.una.SIGECA.service.TeamService;
import java.util.List;
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
            @RequestParam(value = "idTeam", required = false) Integer idTeam) {

        Team team = (idTeam != null && idTeam > 0)
                ? teamService.findById(idTeam)
                : new Team();
        model.addAttribute("team", team);
        
        model.addAttribute("players", playerService.getAll());

        return "team/team_form_user";    
    }

    // SAVE, DELETE y FILTER devuelven solo la tabla
    @PostMapping("/user/save")
    public String saveTeamUser(@ModelAttribute Team team, Model model) {
        try {
            teamService.saveTeam(team);
            model.addAttribute("success", "Team saved successfully");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
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
