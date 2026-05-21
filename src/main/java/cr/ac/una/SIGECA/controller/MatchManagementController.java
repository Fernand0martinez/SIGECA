package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.domain.MatchEvent;
import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.service.MatchManagementService;
import cr.ac.una.SIGECA.service.MatchService;
import cr.ac.una.SIGECA.service.RefereeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/match/admin")
public class MatchManagementController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchManagementService matchManagementService;

    @Autowired
    private RefereeService refereeService;

    @GetMapping("/list")
    public String listMatches(Model model) {
        model.addAttribute("matches", matchService.getAll());
        return "match/list_matches";
    }

    @GetMapping("/edit")
    public String editMatch(@RequestParam("id") int id, Model model) {
        Match match = matchService.getById(id);
        List<Referee> referees = refereeService.getAll();
        model.addAttribute("match", match);
        model.addAttribute("referees", referees);
        return "match/edit_match";
    }

    @PostMapping("/update")
    public String updateMatch(
            @RequestParam("id") int id,
            @RequestParam("homeGoals") int homeGoals,
            @RequestParam("awayGoals") int awayGoals,
            @RequestParam("refereeId") int refereeId,
            @ModelAttribute("events") List<MatchEvent> events,
            RedirectAttributes redirectAttributes) {
        
        try {
            Referee referee = refereeService.getById(refereeId);
            matchManagementService.assignReferee(id, referee);
            matchManagementService.registerMatchResult(id, homeGoals, awayGoals, events);
            
            redirectAttributes.addFlashAttribute("success", "Partido actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/match/admin/list";
    }
}
