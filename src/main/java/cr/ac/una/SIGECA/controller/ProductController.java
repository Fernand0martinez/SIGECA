/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.domain.Court;
import cr.ac.una.SIGECA.domain.Product;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicProduct;
import cr.ac.una.SIGECA.service.CourtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author crist
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private LogicProduct logicProduct;

    @Autowired
    private CourtService courtServi;

    @GetMapping("/admin/list")
    public String listProducts(HttpSession session, Model model, HttpServletRequest request) {
        User usuario = (User) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return "fragments/error :: sesion-expirada";
            }
            return "redirect:/login/iniciar";
        }

        List<Product> products = logicProduct.getProducts();
        model.addAttribute("products", products);
        model.addAttribute("canchas", courtServi.listCourts());

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/list_product :: contenido";
        }

        return "product/list_product";
    }

    @PostMapping("/admin/save")
    public String guardarProducto(@ModelAttribute Product producto, Model model) {
        boolean resultado;

        if (producto.getIdProduct() != null) {
            resultado = logicProduct.updateProduct(producto);
        } else {
            resultado = logicProduct.saveProduct(producto);
        }

        model.addAttribute("resultado", resultado);
        return "forward:/products/admin/fragment-updated";
    }

    @PostMapping("/admin/fragment-updated")
    public String devolverFragmentoActualizado(Model model) {
        List<Product> products = logicProduct.getProducts();
        model.addAttribute("products", products);
        return "product/fragments/lista_producto :: tbodyProductos";
    }

    @GetMapping("/admin/filter-fragment")
    public String filtrarFragmento(@RequestParam(required = false) String courtNameFilter,
            @RequestParam(required = false) String categoryFilter,
            Model model) {
        List<Product> products = logicProduct.filterProducts(courtNameFilter, categoryFilter);
        model.addAttribute("products", products);
        return "product/fragments/lista_producto :: tbodyProductos";
    }

    @GetMapping("/admin/form")
    public String mostrarFormularioNuevo(Model model) {

        Product p = new Product();
        p.setCourt(new Court());
        model.addAttribute("producto", p);

        model.addAttribute("modo", "crear");
        model.addAttribute("canchas", courtServi.listCourts());
        return "product/form_product :: formularioProducto";
    }

    @GetMapping("/admin/edit")
    public String mostrarFormularioEditar(@RequestParam int idProduct, Model model) {
        Product producto = logicProduct.getProductById(idProduct);
        model.addAttribute("producto", producto);
        model.addAttribute("modo", "editar");
        model.addAttribute("canchas", courtServi.listCourts());
        return "product/form_product :: formularioProducto";
    }

    @PostMapping("/admin/delete")
    public String eliminarProduct(@RequestParam int idProduct, Model model) {
        logicProduct.deleteProduct(idProduct);
        List<Product> products = logicProduct.getProducts();
        model.addAttribute("products", products);
        model.addAttribute("mensaje", "Producto eliminado correctamente");
        model.addAttribute("tipo", "success");

        return "product/fragments/lista_producto :: tbodyProductos";
    }

    @GetMapping("/admin/details/{idProduct}")
    public String showDetails(@PathVariable int idProduct, Model model) {
        Product product = logicProduct.getProductById(idProduct);
        model.addAttribute("product", product);
        return "product/detalles_product :: detalleProducto";
    }

    //Apartado para vista usuario
    @GetMapping("/user/list")
    public String vistaCliente(Model model, HttpServletRequest request) {
        model.addAttribute("products", logicProduct.getProducts());
        model.addAttribute("canchas", courtServi.listCourts());
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/product_client :: contenido";
        }
        return "product/product_client";
    }

    @GetMapping("/user/filter")
    public String filtrarCliente(@RequestParam(required = false) String courtNameFilter,
            @RequestParam(required = false) String categoryFilter,
            Model model,
            HttpServletRequest request) {
        List<Product> products = logicProduct.filterProducts(courtNameFilter, categoryFilter);
        model.addAttribute("products", products);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "product/fragments/list_client :: tbodyProductos";
        }

        // Si no viene por AJAX, devolvé la vista entera
        model.addAttribute("canchas", courtServi.listCourts());
        return "product/product_client";
    }

}
