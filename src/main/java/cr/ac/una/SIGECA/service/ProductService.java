/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.ProductRepository;
import cr.ac.una.SIGECA.domain.Product;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author crist
 */
@Service
public class ProductService implements CRUD<Product> {
    
    @Autowired
    private ProductRepository productRepo;

    @Override
    public void save(Product t) {
        if(t.getProductName() == null || t.getProductName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if(t.getDescription() == null || t.getDescription().isBlank()) {
            throw new IllegalArgumentException("Product description is required.");
        }
        if(t.getPrice() == null || t.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price is required and must be greater than 0.");
        }
        productRepo.save(t);
    }

    @Override
    public void delete(int i) {
        productRepo.deleteById(i);
    }

    @Override
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    @Override
    public Product getById(int i) {
        return productRepo.getReferenceById(i);
    }
    
}
