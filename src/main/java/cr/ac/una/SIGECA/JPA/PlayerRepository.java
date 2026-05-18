/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.SIGECA.JPA;

import cr.ac.una.SIGECA.domain.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author ferna
 */
public interface PlayerRepository extends JpaRepository<Player, Integer> {
 @Query("SELECT p FROM Player p WHERE p.gender = :gender")
    List<Player> findByGender(@Param("gender") char gender);
    
    @Query("SELECT p FROM Player p WHERE p.position = :position")
    List<Player> findByPosition(@Param("position") String position);
    
    @Query("SELECT p FROM Player p WHERE p.idCard = :idCard")
Player findByIdCard(@Param("idCard") String idCard);

}