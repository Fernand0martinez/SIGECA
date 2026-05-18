/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.logic.LogicReferee;
import org.springframework.ui.Model;
import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.service.RefereeService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/referees")
public class RefereeController {

    @Autowired
    private RefereeService refereeService;

    @GetMapping("/admin/list")
    public String listReferees(@RequestParam(value = "gen", required = false) String gender,
            @RequestParam(value = "available", required = false) String available,
            HttpServletRequest request,
            Model model) {

        LinkedList<Referee> referees = new LinkedList<>(refereeService.getAll());
        //Se valida la seleccion de los filtros con todos los arbitros
        if (LogicReferee.isValidFilterValue(gender)) {
            referees = LogicReferee.filterByGender(referees, gender);
        }

        if (LogicReferee.isValidFilterValue(available)) {
            referees = LogicReferee.filterByAvailability(referees, available);
        }

        model.addAttribute("referee", referees);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "referee/list_referee :: contenido";
        }
        return "referee/list_referee";
    }

    @GetMapping("/admin/form")
    public String formReferee(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "referee/form_referee :: contenido";
        }
        return "referee/form_referee";
    }

    @PostMapping("/admin/delete")
    public String deleteReferee(@RequestParam("idR") int id, HttpServletRequest request, Model model) {
        refereeService.delete(id);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return listReferees(null, null, request, model);
        }
        return "redirect:/referees/admin/list";
    }

    @PostMapping("/admin/save")
    public String saveReferee(
            @RequestParam("idR") String id,
            @RequestParam("nameR") String name,
            @RequestParam("lastnameR") String lastname,
            @RequestParam("mailR") String mail,
            @RequestParam("ageR") int age,
            @RequestParam("genderR") char gender,
            @RequestParam("experienceR") int experience,
            @RequestParam("availableR") boolean available,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (refereeService.existReferee(Integer.parseInt(id))) {
            redirectAttributes.addFlashAttribute("type", "Error");
            redirectAttributes.addFlashAttribute("message", "El árbitro ya se encuentra registrado");
            return "redirect:/referees/admin/list";
        }

        Referee ref = new Referee(id, name, lastname, mail, age, gender, experience, available);
        //Valida los datos internamente
        String rawErrors = LogicReferee.validateFields(id, name, lastname, mail, age, gender, experience);
        
        if(rawErrors.length() > 0) {
            redirectAttributes.addFlashAttribute("type", "Error");
            redirectAttributes.addFlashAttribute("message", rawErrors);
            return "redirect:/referees/admin/form";
        }
        refereeService.save(ref);
        redirectAttributes.addFlashAttribute("type", "Success");
        redirectAttributes.addFlashAttribute("message", "Árbitro registrado exitosamente");
        return "redirect:/referees/admin/list";

    }

    @GetMapping("/admin/edit")
    public String editReferee(@RequestParam("idR") int id, Model model, HttpServletRequest request) {
        Referee referee = refereeService.getById(id);
        if (referee == null) {
            model.addAttribute("error", "Árbitro no encontrado.");
            return "redirect:/referees/admin/list";
        }
        //Carga los datos del arbitro al form
        model.addAttribute("referee", referee);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "referee/form_referee_edit :: contenido";
        }
        return "referee/form_referee_edit";
    }

    @PostMapping("/admin/update")
    public String updateReferee(
            @RequestParam("idR") int id,
            @RequestParam("nameR") String name,
            @RequestParam("lastnameR") String lastname,
            @RequestParam("mailR") String mail,
            @RequestParam("ageR") int age,
            @RequestParam("genderR") char gender,
            @RequestParam("experienceR") int experience,
            @RequestParam("availableR") boolean available,
            HttpServletRequest request,
            Model model) {

        Referee referee = refereeService.getById(id);

        if (referee == null) {
            model.addAttribute("error", "Árbitro no encontrado.");
            return "redirect:/referees/admin/list";
        }

        referee.setName(name);
        referee.setLastName(lastname);
        referee.setMail(mail);
        referee.setAge(age);
        referee.setGender(gender);
        referee.setExperience(experience);
        referee.setAvailable(available);

        refereeService.save(referee);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return listReferees(null, null, request, model);
        }
        return "redirect:/referees/admin/list";
    }

    @GetMapping("/admin")
    public String mostrarVistaAdministrador() {
        return "administrador";
    }

}
