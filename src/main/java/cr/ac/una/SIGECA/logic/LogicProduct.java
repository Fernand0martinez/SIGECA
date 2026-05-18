/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.logic;

/**
 *
 * @author crist
 */
import cr.ac.una.SIGECA.domain.Product;
import cr.ac.una.SIGECA.service.CourtService;
import cr.ac.una.SIGECA.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class LogicProduct {

    private final ProductService productService;

    @Autowired
    private CourtService courtService;

    public LogicProduct(ProductService productService) {
        this.productService = productService;
    }

    public boolean saveProduct(Product product) {
        // Verificá duplicados si querés
        List<Product> existingProducts = productService.getAll();
        for (Product p : existingProducts) {
            if (p.getProductName().equalsIgnoreCase(product.getProductName())
                    && p.getCourt().getIdCourt().equals(product.getCourt().getIdCourt())) {
                return false;
            }
        }

        if (product.getCourt() != null && product.getCourt().getIdCourt() != null) {
            product.setCourt(courtService.findById(product.getCourt().getIdCourt()));
        }

        productService.save(product);
        return true;
    }

    public List<Product> getProducts() {
        List<Product> list = productService.getAll();
        if (list.isEmpty()) {
            System.out.println("Lista de productos vacía");
        }
        return list;
    }

    public boolean updateProduct(Product product) {
        Product existing = productService.getById(product.getIdProduct());
        if (existing != null) {
            if (product.getCourt() != null && product.getCourt().getIdCourt() != null) {
                product.setCourt(courtService.findById(product.getCourt().getIdCourt()));
            }
            productService.save(product);
            return true;
        }
        return false;
    }

    public void deleteProduct(int id) {
        Product product = productService.getById(id);
        if (product != null) {
            productService.delete(id);
        } else {
            System.err.println("No se encontró el producto");
        }
    }

    public LinkedList<Product> filterProducts(String courtName, String category) {
        List<Product> productList = productService.getAll();
        LinkedList<Product> productFilter = new LinkedList<>();

        for (Product product : productList) {
            boolean matchesCourt = (courtName == null || courtName.isEmpty()
                    || product.getCourt().getNameCourt().equalsIgnoreCase(courtName));

            boolean matchesCategory = (category == null || category.isEmpty()
                    || product.getCategory().equalsIgnoreCase(category));

            if (matchesCourt && matchesCategory) {
                productFilter.add(product);
            }
        }

        return productFilter;
    }

    public boolean existProductById(int idProduct) {
        try {
            return productService.getById(idProduct) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Product getProductById(int idProduct) {
        return productService.getById(idProduct);
    }
}
