package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.data.UserData;
import cr.ac.una.SIGECA.domain.Court;
import cr.ac.una.SIGECA.domain.Reservation;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicData_Reservation;
import cr.ac.una.SIGECA.repository.CourtRepository;
import cr.ac.una.SIGECA.service.CourtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/reservations")
@Controller
public class ReservationController {

    @Autowired
    private CourtService courtService;
    @Autowired
    private LogicData_Reservation log;

    private CourtRepository courtRepo;

    @Autowired
    public ReservationController(CourtRepository courtRepo) {
        this.courtRepo = courtRepo;
    }

    public ReservationController(LogicData_Reservation log) {
        this.log = log;
    }

    private User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        return user != null ? user : UserData.getUser();
    }

    private boolean belongsToUser(Reservation reservation, User user) {
        return reservation != null
                && user != null
                && reservation.getUser() != null
                && reservation.getUser().getId() == user.getId();
    }

    @GetMapping("/user/list")
    public String getListReservation(Model model, HttpServletRequest request, HttpSession session) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login/home";
        }

        List<Reservation> reservations = log.getAllByUser(user);
        model.addAttribute("reservacion", reservations);
        model.addAttribute("usuario", user);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "reservation/list_reservation :: contenido";
        }
        return "reservation/list_reservation";
    }

    @GetMapping("/user")
    public String mostrarVistaUsuario() {
        return "usuario";
    }

    @GetMapping("/admin")
    public String mostrarVistaAdministrador() {
        return "administrador";
    }

    @GetMapping("/user/form")
    public String getForm(Model model, HttpServletRequest request, HttpSession session) {
        User user = getCurrentUser(session);

        if (user == null) {
            return "redirect:/login/home";
        }

        List<Court> courts = courtService.listCourts();
        model.addAttribute("titulo", "Lista de Canchas");
        model.addAttribute("courts", courts);
        model.addAttribute("usuario", user);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "reservation/form_reservation :: contenido";
        }
        return "reservation/form_reservation";
    }

    @PostMapping("/user/save")
    public String registerReservation(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startHour") Integer startHour,
            @RequestParam("endHour") Integer endHour,
            @RequestParam("courtId") Integer idCourt,
            HttpServletRequest request,
            Model model,
            HttpSession session) {

        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login/home";
        }

        if (startHour >= endHour) {
            model.addAttribute("mensaje", "La hora de fin debe ser mayor a la de inicio");
            model.addAttribute("tipo", "error");
            model.addAttribute("usuario", user);
            model.addAttribute("courts", courtService.listCourts());
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "reservation/form_reservation :: contenido";
            }
            return "reservation/form_reservation";
        }

        Court court = courtRepo.findById(idCourt).orElse(null);
        if (court == null) {
            return "redirect:/reservations/user/form";
        }

        if (!log.isCourtAvailable(court, date, startHour, endHour)) {
            model.addAttribute("mensaje", "La cancha ya está reservada en esa fecha y hora.");
            model.addAttribute("tipo", "error");
            model.addAttribute("courts", courtService.listCourts());
            model.addAttribute("usuario", user);

            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "reservation/form_reservation :: contenido";
            }
            return "reservation/form_reservation";
        }

        double totalPrice = log.calculateTotalPrice(court, startHour, endHour);
        Reservation reservation = new Reservation(user, court, date, startHour, endHour, totalPrice);
        log.save(reservation);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            model.addAttribute("reservacion", log.getAllByUser(user));
            model.addAttribute("usuario", user);
            return "reservation/list_reservation :: contenido";
        }
        return "redirect:/reservations/user/list";
    }

    @GetMapping("/user/details/{codigo}")
    public String detalleParcial(@PathVariable("codigo") Integer codigo, Model model, HttpServletRequest request, HttpSession session) {
        Reservation reservation = log.getReservation(codigo);
        User user = getCurrentUser(session);

        if (!belongsToUser(reservation, user)) {
            return "redirect:/login/home";
        }

        model.addAttribute("reservacion", reservation);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "reservation/details :: contenido";
        }
        return "reservation/details";
    }

    @PostMapping("/user/delete")
    public String deleteReservation(@RequestParam("id") Integer id, HttpServletRequest request, Model model, HttpSession session) {
        User user = getCurrentUser(session);
        Reservation reservation = log.getReservation(id);

        if (!belongsToUser(reservation, user)) {
            return "redirect:/login/home";
        }

        log.delete(id);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            model.addAttribute("reservacion", log.getAllByUser(user));
            model.addAttribute("usuario", user);
            return "reservation/list_reservation :: contenido";
        }
        return "redirect:/reservations/user/list";
    }

    @GetMapping("/admin/list")
    public String getReservationAdmn(Model model) {
        List<Reservation> reservation = log.getAll();
        model.addAttribute("reservacion", reservation);
        return "reservation/reservation_adm";
    }

    @GetMapping("/user/edit/{id}")
    public String editReservation(@PathVariable("id") Integer id, Model model, HttpServletRequest request, HttpSession session) {
        Reservation reservation = log.getReservation(id);
        User user = getCurrentUser(session);

        if (!belongsToUser(reservation, user)) {
            return "redirect:/login/home";
        }

        model.addAttribute("reservacion", reservation);
        model.addAttribute("usuario", reservation.getUser());
        model.addAttribute("courts", courtService.findAll());
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "reservation/updateReservation :: contenido";
        }
        return "reservation/updateReservation";
    }

    @PostMapping("/user/update")
    public String updateReservation(
            @RequestParam("id") Integer id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startHour") Integer startHour,
            @RequestParam("endHour") Integer endHour,
            @RequestParam("courtId") Integer idCourt,
            HttpServletRequest request,
            Model model,
            HttpSession session) {

        User user = getCurrentUser(session);
        if (user == null) {
            return "redirect:/login/home";
        }

        if (startHour >= endHour) {
            model.addAttribute("mensaje", "La hora de fin debe ser mayor a la de inicio");
            model.addAttribute("tipo", "error");
            model.addAttribute("usuario", user);
            model.addAttribute("courts", courtService.listCourts());
            model.addAttribute("reservacion", log.findById(id));
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "reservation/updateReservation :: contenido";
            }
            return "reservation/updateReservation";
        }

        Reservation existing = log.findById(id);
        if (!belongsToUser(existing, user)) {
            return "redirect:/login/home";
        }

        Court court = courtRepo.findById(idCourt).orElse(null);
        if (court == null) {
            return "redirect:/reservations/user/list";
        }

        if (!log.isCourtAvailablee(court, date, startHour, endHour, id)) {
            model.addAttribute("mensaje", "La cancha ya está reservada en esa fecha y hora.");
            model.addAttribute("tipo", "error");
            model.addAttribute("usuario", user);
            model.addAttribute("courts", courtService.listCourts());
            model.addAttribute("reservacion", existing);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "reservation/updateReservation :: contenido";
            }
            return "reservation/updateReservation";
        }

        double totalPrice = log.calculateTotalPrice(court, startHour, endHour);

        existing.setDate(date);
        existing.setStartHour(startHour);
        existing.setEndHour(endHour);
        existing.setCourt(court);
        existing.setTotalPrice(totalPrice);

        model.addAttribute("mensaje", "Reservación actualizada con éxito");
        model.addAttribute("tipo", "success");
        model.addAttribute("usuario", user);
        model.addAttribute("courts", courtService.listCourts());
        model.addAttribute("reservacion", existing);
        log.save(existing);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            model.addAttribute("reservacion", log.getAllByUser(user));
            model.addAttribute("usuario", user);
            return "reservation/list_reservation :: contenido";
        }
        return "redirect:/reservations/user/list";
    }
}
