
package cr.ac.una.SIGECA.JPA;

import cr.ac.una.SIGECA.domain.Payment;
import cr.ac.una.SIGECA.domain.PaymentMethod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author ferna
 */

public interface PaymentRepository extends JpaRepository<Payment,Integer>{
     @Query("SELECT p FROM Payment p WHERE p.reservation.user.id = :id_user")
    List<Payment> findByIdUser(@Param("id_user") int id_user);
    
    @Query("SELECT p FROM Payment p WHERE p.reservation.user.id = :userId AND p.method = :method")
    List<Payment> findByUserIdAndMethod(@Param("userId") int userId, @Param("method") PaymentMethod method);
    
}
