/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.JPA;

import cr.ac.una.SIGECA.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author crist
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByIdCard(String idCard);
    boolean existsByMail(String mail);
    Optional<User> findByNameUser(String idCard);

}
