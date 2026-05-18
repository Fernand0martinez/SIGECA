package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Court;
import cr.ac.una.SIGECA.service.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/courts")
public class CourtController {

    @Autowired
    private CourtService courtService;

    // Listar canchas para admin
    @GetMapping("/admin/list")
    public String listCourtsAdmin(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        List<Court> courts = courtService.listCourts();
        model.addAttribute("titulo", "Lista de Canchas");
        model.addAttribute("courts", courts);       
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "court/list_courts_admin :: main";           
        }
        return "court/list_courts_admin";
    }

    // Listar canchas para cliente
    @GetMapping("/client/list")
    public String listCourtsClient(
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith
    ) {
        List<Court> courts = courtService.listAvailableCourts();
        model.addAttribute("titulo", "Canchas Disponibles");
        model.addAttribute("courts", courts);

        if ("XMLHttpRequest".equals(requestedWith)) {
            // devolvemos solo el fragmento <main th:fragment="main">
            return "court/list_courts_client :: main";
        }        
        return "court/list_courts_client";
    }

    // Mostrar formulario para agregar/editar
    @GetMapping("/admin/form")
    public String showFormCourt(
            Model model,
            @RequestParam(value = "idCourt", required = false) Integer idCourt,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        Court court;
        if (idCourt != null && idCourt > 0) {
            court = courtService.findById(idCourt);
        } else {
            court = new Court();
            court.setIdCourt(null);
        }
        model.addAttribute("court", court);
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "court/court_form :: formulario";
        }
        return "court/court_form";
    }

    // Guardar cancha
    @PostMapping("/admin/save")
    public String saveCourts(@ModelAttribute Court court, Model model) {
        try {
            courtService.saveCourt(court);
            model.addAttribute("success", court.getIdCourt() == 0 ? "Cancha creada exitosamente" : "Cancha actualizada exitosamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("courts", courtService.listCourts());
        return "court/court_table_admin";
    }

    // Filtrar canchas
    @GetMapping("/admin/filter")
    public String filterAdminCourts(
            @RequestParam(value = "query", required = false) String query,
            Model model
    ) {
        List<Court> courts = courtService.filterCourtsByQuery(query);
        model.addAttribute("courts", courts);
        model.addAttribute("titulo", query != null ? "Resultados para: " + query : "Todas las Canchas");
        model.addAttribute("noResults", courts.isEmpty());
        model.addAttribute("noResult", "No se encontraron resultados para la búsqueda");
        return "court/court_table_admin";
    }

    // Filtrar canchas 
    @GetMapping("/client/filter")
    public String filterClientCourts(
            @RequestParam(value = "query", required = false) String query,
            Model model
    ) {
        List<Court> courts = courtService.filterCourtsByQuery(query);
        courts = courts.stream()
                .filter(c -> "Disponible".equalsIgnoreCase(c.getStatusCourt()))
                .toList();
        model.addAttribute("courts", courts);
        model.addAttribute("titulo", query != null ? "Resultados para: " + query : "Canchas Disponibles");
        model.addAttribute("noResults", courts.isEmpty());
        model.addAttribute("noResult", "No se encontraron resultados para la búsqueda");
        return "court/court_table_client";
    }

    // Eliminar cancha
    @PostMapping("/admin/delete")
    public String deleteCourt(
            @RequestParam("id_court") int idCourt,
            Model model
    ) {
        courtService.deleteCourt(idCourt);
        model.addAttribute("success", "Cancha eliminada exitosamente");
        model.addAttribute("courts", courtService.listCourts());
        return "court/court_table_admin";
    }
    
}
