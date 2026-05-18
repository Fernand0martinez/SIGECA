/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.domain.Sponsorship;
import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.logic.LogicReferee;
import cr.ac.una.SIGECA.service.RefereeService;
import cr.ac.una.SIGECA.service.SponsorshipService;
import cr.ac.una.SIGECA.service.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author kenda
 */
@Controller
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private SponsorshipService sponsorshipService; // Necesario para manejar los patrocinios

    @Autowired
    private RefereeService refereeService; // Nuevo servicio para árbitros

    @GetMapping("/admin/list")
    public String listTournaments(Model model, HttpServletRequest request) {
        List<Tournament> tournaments = tournamentService.getAll();
        model.addAttribute("tournaments", tournaments);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "tournament/list_tournament :: contenido";
        }
        return "tournament/list_tournament"; 
    }
    
    @GetMapping("/user/registration")
    public String inscriptionTournament(Model model, HttpServletRequest request) {
        List<Tournament> tournaments = tournamentService.getAll();
        model.addAttribute("tournaments", tournaments);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "tournament/registration_team :: contenido";
        }
        return "tournament/registration_team"; 
    }
    
    

    @GetMapping("/admin/form")
    public String formTournament(Model model, HttpServletRequest request) {
        
        List<Sponsorship> sponsorships = sponsorshipService.getAll();
        List<Referee> referees = refereeService.getAll();
        model.addAttribute("sponsorships", sponsorships);
        model.addAttribute("refeeres", referees);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "tournament/form_tournament :: contenido";
        }
        return "tournament/form_tournament";
    }

    @PostMapping("/admin/save")
    public String saveTournament(
            @RequestParam("name") String name,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "sponsorId", required = false) Integer sponsorId,
            @RequestParam(value = "refereeIds", required = false) List<Integer> refereeIds,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación básica de fechas
        if (endDate.isBefore(startDate)) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                model.addAttribute("error", "La fecha de cierre no puede ser anterior a la fecha de inicio");
                model.addAttribute("sponsorships", sponsorshipService.getAll());
                model.addAttribute("refeeres", refereeService.getAll());
                return "tournament/form_tournament :: contenido";
            }
            redirectAttributes.addFlashAttribute("error", "La fecha de cierre no puede ser anterior a la fecha de inicio");
            return "redirect:/tournaments/admin/form";
        }

        
        boolean status = LocalDate.now().isBefore(endDate) && LocalDate.now().isBefore(startDate);

        Tournament tournament = new Tournament();
        tournament.setName(name);
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);
        tournament.setStatus(status);

        if (sponsorId != null) {
            Sponsorship sponsor = sponsorshipService.getById(sponsorId);
            tournament.setSponsor(sponsor);
        }

        if (refereeIds != null && !refereeIds.isEmpty()) {
            List<Referee> selectedReferees = LogicReferee.getRefereesByIds(refereeService.getAll(), refereeIds);
            tournament.setReferees(selectedReferees);
        }

        tournamentService.save(tournament);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            model.addAttribute("tournaments", tournamentService.getAll());
            model.addAttribute("success", "Torneo creado exitosamente");
            return "tournament/list_tournament :: contenido";
        }
        redirectAttributes.addFlashAttribute("success", "Torneo creado exitosamente");
        return "redirect:/tournaments/admin/list";
    }

    @PostMapping("/admin/delete")
    public String deleteTournament(@RequestParam("id") int id, HttpServletRequest request, Model model) {
        tournamentService.delete(id);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            model.addAttribute("tournaments", tournamentService.getAll());
            return "tournament/list_tournament :: contenido";
        }
        return "redirect:/tournaments/admin/list";
    }

    @GetMapping("/admin/edit")
    public String editTournament(@RequestParam("id") int id, Model model) {
        Tournament tournament = tournamentService.getById(id);
        if (tournament == null) {
            return "redirect:/tournaments/admin/list";
        }

        List<Sponsorship> sponsorships = sponsorshipService.getAll();
        model.addAttribute("sponsorships", sponsorships);
        model.addAttribute("tournament", tournament);
        return "tournament/form_tournament_edit";
    }

    @PostMapping("/admin/update")
    public String updateTournament(
            @RequestParam("id") int id,
            @RequestParam("name") String name,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "sponsorId", required = false) Integer sponsorId,
            RedirectAttributes redirectAttributes) {

        Tournament tournament = tournamentService.getById(id);
        if (tournament == null) {
            return "redirect:/tournaments/admin/list";
        }

        // Validar fechas
        if (endDate.isBefore(startDate)) {
            redirectAttributes.addFlashAttribute("error", "La fecha de cierre no puede ser anterior a la fecha de inicio");
            return "redirect:/tournaments/admin/edit?id=" + id;
        }

        // Actualizar estado basado en fechas
        boolean status = LocalDate.now().isBefore(endDate) && LocalDate.now().isAfter(startDate);

        tournament.setName(name);
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);
        tournament.setStatus(status);

        // Actualizar patrocinio
        if (sponsorId != null) {
            Sponsorship sponsor = sponsorshipService.getById(sponsorId);
            tournament.setSponsor(sponsor);
        } else {
            tournament.setSponsor(null);
        }

        tournamentService.save(tournament);

        redirectAttributes.addFlashAttribute("success", "Torneo actualizado exitosamente");
        return "administrador";
    }

    @GetMapping("/admin/details")
    public String tournamentDetails(@RequestParam("id") int id, Model model) {
        Tournament tournament = tournamentService.getById(id);
        if (tournament == null) {
            return "redirect:/tournaments/admin/list";
        }
        model.addAttribute("tournament", tournament);
        return "tournament/details_tournament";
    }

    @GetMapping("/admin")
    public String mostrarVistaAdministrador() {
        return "administrador";
    }
    
    
}
