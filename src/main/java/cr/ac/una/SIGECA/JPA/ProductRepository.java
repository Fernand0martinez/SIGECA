/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.JPA;
import cr.ac.una.SIGECA.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author crist
 */
public interface ProductRepository extends JpaRepository<Product,Integer> {
    
}
