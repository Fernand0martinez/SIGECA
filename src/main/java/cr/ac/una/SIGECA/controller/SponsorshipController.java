/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.logic.LogicSponsorship;
import cr.ac.una.SIGECA.logic.LogicData;
import cr.ac.una.SIGECA.domain.Sponsorship;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author Usuario
 */
@RequestMapping("/sponsorships")
@Controller
public class SponsorshipController {

    private LogicData log;
    private LogicSponsorship logic;

    public SponsorshipController(LogicData log, LogicSponsorship logic) {
        this.log = log;
        this.logic = logic;

    }

    //metodo para retornar la vista principal
    @GetMapping("/admin/list")
    public String getStart(Model model, HttpServletRequest request) {

        List<Sponsorship> patrocinios = log.getAll();
        model.addAttribute("patrocinios", patrocinios);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sponsorship/list_sponsorship :: contenido";
        }
        return "sponsorship/list_sponsorship";
    }

    //metodo para retornar la vista de formulario
    @GetMapping("/admin/form")
    public String form(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sponsorship/form_sponsorship :: contenido";
        }
        return "redirect:/sponsorships/admin?cargarVista=patrocinio";
    }

    // Metodo para guardar en BD
    @PostMapping("/admin/save")
    public String registerSponsorship(
            @RequestParam(value = "companyCode", required = false) Integer companyCode,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "contractStart", required = false) String contractStart,
            @RequestParam(value = "contractEnd", required = false) String contractEnd,
            @RequestParam(value = "advertisingSpace", required = false) String advertisingSpace,
            @RequestParam(value = "sponsorshipType", required = false) String sponsorshipType,
            HttpServletRequest request,
            Model model) {

        String errors = logic.validateSponsorshipInputs(
                companyCode, company, phoneNumber, amount, contractStart,
                contractEnd, advertisingSpace, sponsorshipType);

        if (errors.length() > 0) {
            model.addAttribute("errores", errors.toString());
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "sponsorship/form_sponsorship :: contenido";
            }
            return "sponsorship/form_sponsorship";
        }

        // Validar fechas y calcular si el patrocinio está activo
        LocalDate start = LocalDate.parse(contractStart);
        LocalDate end = LocalDate.parse(contractEnd);
        boolean isActive = LogicSponsorship.isSponsorshipActive(start, end);

        Sponsorship sponsorship = new Sponsorship(companyCode, company, amount, start, end,
                advertisingSpace, isActive, sponsorshipType, phoneNumber);

        model.addAttribute("mensaje", "Registro exitoso!");
        model.addAttribute("tipo", "success");
        log.save(sponsorship);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            List<Sponsorship> patrocinios = log.getAll();
            model.addAttribute("patrocinios", patrocinios);
            return "sponsorship/list_sponsorship :: contenido";
        }

        return "redirect:/sponsorships/admin?cargarVista=patrocinio";

    }

    @GetMapping("/admin/filter")
    public String filterSponsor(
            @RequestParam(value = "sponsorshipType", required = false) String sponsorshipType,
            @RequestParam(value = "advertisingSpace", required = false) String advertisingSpace,
            HttpServletRequest request,
            Model model) {

        LinkedList<Sponsorship> filteredSponsorships = new LinkedList<>();
        StringBuilder message = new StringBuilder();

        log.filterSponsorship(filteredSponsorships, message, sponsorshipType, advertisingSpace);

        model.addAttribute("patrocinios", filteredSponsorships);
        model.addAttribute("message", message);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sponsorship/list_sponsorship :: contenido";
        }
        return "sponsorship/list_sponsorship";
    }

    //metodo de mapeo con javaScrip para editar
    @GetMapping("/admin/edit/{id}")
    public String editSponsorship(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
        Sponsorship sponsorship = log.getSponsorship(id);
        model.addAttribute("sponsorship", sponsorship);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sponsorship/update_sponsorship :: contenido";
        }
        return "sponsorship/update_sponsorship";
    }

    //metodo para guardar la edición
    @PostMapping("/admin/update")
    public String saveSponsorship(@RequestParam(value = "companyCode", required = false) Integer companyCode,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "contractStart", required = false) String contractStart,
            @RequestParam(value = "contractEnd", required = false) String contractEnd,
            @RequestParam(value = "advertisingSpace", required = false) String advertisingSpace,
            @RequestParam(value = "sponsorshipType", required = false) String sponsorshipType,
            HttpServletRequest request,
            Model model) {

        String errors = logic.validateUpdateInputs(
                companyCode, company, phoneNumber, amount, contractStart,
                contractEnd, advertisingSpace, sponsorshipType);

        if (errors.length() > 0) {

            Sponsorship sponsorship = new Sponsorship(companyCode, company, amount,
                    contractStart != null ? LocalDate.parse(contractStart) : null,
                    contractEnd != null ? LocalDate.parse(contractEnd) : null,
                    advertisingSpace, false, sponsorshipType, phoneNumber);

            model.addAttribute("errores", errors);
            model.addAttribute("sponsorship", sponsorship);

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "sponsorship/update_sponsorship :: contenido";
            }
            return "sponsorship/update_sponsorship";
        }

        // Validar fechas y calcular si el patrocinio está activo
        LocalDate start = LocalDate.parse(contractStart);
        LocalDate end = LocalDate.parse(contractEnd);

        boolean isActive = LogicSponsorship.isSponsorshipActive(start, end);

        Sponsorship sponsorship = new Sponsorship(companyCode, company, amount, start, end,
                advertisingSpace, isActive, sponsorshipType, phoneNumber);

        model.addAttribute("mensaje", "Actualizado exitoso!");
        model.addAttribute("tipo", "success");
        log.save(sponsorship);
        model.addAttribute("sponsorship", sponsorship);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            List<Sponsorship> patrocinios = log.getAll();
            model.addAttribute("patrocinios", patrocinios);
            return "sponsorship/list_sponsorship :: contenido";
        }
        return "sponsorship/update_sponsorship";

    }

    //metodo para eliminar por medio de ajax
    @PostMapping("/admin/delete")
    public String deleteSponsorship(@RequestParam("id") Integer id, HttpServletRequest request, Model model) {

        log.delete(id);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            List<Sponsorship> patrocinios = log.getAll();
            model.addAttribute("patrocinios", patrocinios);
            return "sponsorship/list_sponsorship :: contenido";
        }
        return "redirect:/sponsorships/admin/list";

    }

    //metodo para mostrar los detalles de un patrocinio por medio de ajax 
    @GetMapping("/admin/details/{codigo}")
    public String detalleParcial(@PathVariable("codigo") Integer codigo, Model model, HttpServletRequest request) {

        Sponsorship sponsorship = log.getSponsorship(codigo);

        model.addAttribute("patrocinio", sponsorship);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sponsorship/details :: contenido";
        }
        return "sponsorship/details";
    }

    @GetMapping("/admin")
    public String mostrarVistaAdministrador() {
        return "administrador";
    }

}
