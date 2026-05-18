/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.SIGECA.JPA;
import cr.ac.una.SIGECA.domain.Invoice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author ferna
 */

public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{
      @Query("SELECT i FROM Invoice i WHERE i.payment.reservation.user.id = :id_user")
List<Invoice> findByUserId(@Param("id_user") int id_user);
}

