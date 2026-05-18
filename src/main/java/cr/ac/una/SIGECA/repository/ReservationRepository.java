/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Reservation;
import cr.ac.una.SIGECA.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author Usuario
 */
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser(User user);
}
