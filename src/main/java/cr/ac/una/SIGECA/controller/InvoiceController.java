
package cr.ac.una.SIGECA.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/*
 * @author ferna
 */
@RequestMapping("/invoices")
@Controller
public class InvoiceController{
    @GetMapping("/user/details")
    public String getInvoice(){
        return "invoice/payment";
    }
}
