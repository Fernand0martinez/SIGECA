/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author kenda
 */
public interface TournamentRepository extends JpaRepository<Tournament, Integer>{
    
}
