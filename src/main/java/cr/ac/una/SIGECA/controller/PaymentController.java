package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.data.UserData;
import cr.ac.una.SIGECA.domain.Payment;
import cr.ac.una.SIGECA.domain.PaymentMethod;
import cr.ac.una.SIGECA.domain.Reservation;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicData_Reservation;
import cr.ac.una.SIGECA.service.EmailService;
import cr.ac.una.SIGECA.service.PaymentService;
import cr.ac.una.SIGECA.service.PdfService;
import cr.ac.una.SIGECA.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/payments")
@Controller
public class PaymentController {

    @Autowired
    private LogicData_Reservation log;
    @Autowired
    PaymentService pay;
    @Autowired
    ReservationService re;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private EmailService emailService;

    private User getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("usuarioLogueado");
        return user != null ? user : UserData.getUser();
    }

    private boolean ownsReservation(Reservation reservation, User user) {
        return reservation != null
                && user != null
                && reservation.getUser() != null
                && reservation.getUser().getId() == user.getId();
    }

    private boolean ownsPayment(Payment payment, User user) {
        return payment != null && ownsReservation(payment.getReservation(), user);
    }

    @GetMapping("/user/download")
    public ResponseEntity<byte[]> downloadInvoice(@RequestParam("id") Integer id, HttpSession session) {
        Payment payment = pay.getById(id);
        User user = getCurrentUser(session);
        if (!ownsPayment(payment, user)) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("payment", payment);

        byte[] pdfBytes = pdfService.generatePdfFromHtml("invoice/invoice", data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Factura_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/user/send-email")
    @ResponseBody
    public ResponseEntity<String> sendEmail(@RequestParam("id") Integer id, HttpSession session) {
        try {
            Payment payment = pay.getById(id);
            User user = getCurrentUser(session);
            if (!ownsPayment(payment, user)) {
                return ResponseEntity.status(404).body("Pago no encontrado");
            }

            String userEmail = payment.getReservation().getUser().getMail();
            if (userEmail == null || userEmail.isEmpty()) {
                return ResponseEntity.status(400).body("El usuario no tiene un correo registrado");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("payment", payment);

            byte[] pdfBytes = pdfService.generatePdfFromHtml("invoice/invoice", data);

            emailService.sendEmailWithAttachment(
                    userEmail,
                    "Comprobante de Pago - SIGECA #" + id,
                    "Hola " + payment.getReservation().getUser().getName() + ",\n\nAdjunto encontraras el comprobante de tu pago por la reservacion de la cancha " + payment.getReservation().getCourt().getNameCourt() + ".\n\nGracias por utilizar SIGECA.",
                    pdfBytes,
                    "Factura_" + id + ".pdf"
            );

            return ResponseEntity.ok("Correo enviado con exito");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al enviar el correo: " + e.getMessage());
        }
    }

    @GetMapping("/user/filter")
    public String filterByMethodPayment(
            @RequestParam(value = "metodo", defaultValue = "T") String metodo,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model,
            HttpSession session) {

        User user = getCurrentUser(session);
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (user == null) {
            return "redirect:/login/home";
        }

        List<Payment> paymentList = new ArrayList<>();
        model.addAttribute("method", PaymentMethod.values());
        model.addAttribute("metodos", PaymentMethod.values());
        model.addAttribute("metodoSeleccionado", metodo);

        if (metodo == null || metodo.isEmpty()) {
            model.addAttribute("message", "Seleccione un tipo de filtrado.");
            model.addAttribute("type", "warning");
            return isAjax ? "invoice/list_payment2 :: contenido" : "invoice/list_payment2";
        }

        if ("T".equals(metodo)) {
            paymentList = pay.getByIdUser(user.getId());
        } else {
            try {
                PaymentMethod selectedMethod = PaymentMethod.valueOf(metodo);
                paymentList = pay.findByUserIdAndMethod(user.getId(), selectedMethod);
                if (paymentList.isEmpty()) {
                    model.addAttribute("message", "No hay datos relacionados a ese metodo de pago.");
                    model.addAttribute("type", "info");
                }
            } catch (IllegalArgumentException e) {
                model.addAttribute("message", "Metodo de pago invalido.");
                model.addAttribute("type", "error");
                return isAjax ? "invoice/list_payment2 :: contenido" : "invoice/list_payment2";
            }
        }

        if (paymentList.isEmpty()) {
            model.addAttribute("message", "No hay pagos con ese metodo.");
            model.addAttribute("type", "info");
            return isAjax ? "invoice/list_payment2 :: contenido" : "invoice/list_payment2";
        }

        int pageSize = 5;
        int totalPayments = paymentList.size();
        int totalPages = (int) Math.ceil((double) totalPayments / pageSize);

        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalPayments);
        List<Payment> currentPagePayments = paymentList.subList(fromIndex, toIndex);

        model.addAttribute("payment", currentPagePayments);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return isAjax ? "invoice/list_payment2 :: contenido" : "invoice/list_payment2";
    }

    @GetMapping("/user/pay-form")
    public String doPayment(@RequestParam("id") Integer id, Model model, HttpServletRequest request, HttpSession session) {
        Reservation reserv = re.getById(id);
        User user = getCurrentUser(session);
        if (!ownsReservation(reserv, user)) {
            return "redirect:/login/home";
        }
        model.addAttribute("reservation", reserv);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "invoice/do_payment :: contenido";
        }
        return "invoice/do_payment";
    }

    @PostMapping("/user/process")
    public String realizarPago(
            @RequestParam("reservationId") int reservationId,
            @RequestParam("amount") double amount,
            @RequestParam("method") PaymentMethod method,
            @RequestParam("description") String description,
            HttpServletRequest request,
            Model model,
            HttpSession session
    ) {
        Reservation reservation = re.getById(reservationId);
        User user = getCurrentUser(session);
        if (!ownsReservation(reservation, user)) {
            return "redirect:/login/home";
        }

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setDescription(description);

        pay.save(payment);

        reservation.setStatus("Pagada");
        re.save(reservation);

        List<Reservation> reservations = log.getAllByUser(user);
        model.addAttribute("reservacion", reservations);
        model.addAttribute("message", "Pago realizado con exito");
        model.addAttribute("type", "success");

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "reservation/list_reservation :: contenido";
        }
        return "reservation/list_reservation";
    }

    @GetMapping("/user/details")
    public String viewDetails(@RequestParam("id") Integer id, Model model, HttpServletRequest request, HttpSession session) {
        try {
            Payment payment = pay.getById(id);
            User user = getCurrentUser(session);
            if (!ownsPayment(payment, user)) {
                model.addAttribute("error", "No se encontro el pago con ID: " + id);
                return "fragments/error :: error";
            }
            model.addAttribute("payment", payment);
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "invoice/invoice :: contenido";
            }
            return "invoice/invoice";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los detalles del pago: " + e.getMessage());
            return "fragments/error :: error";
        }
    }
}
