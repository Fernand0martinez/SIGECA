/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.JPA;

import cr.ac.una.SIGECA.domain.Sponsorship;
import java.util.LinkedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Usuario
 */
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Integer> {

    @Query("SELECT p FROM Sponsorship p WHERE p.advertisingSpace = :advertisingSpace")
    LinkedList<Sponsorship> findByAdvertisingSpace(@Param("advertisingSpace") String advertisingSpace);

    @Query("SELECT p FROM Sponsorship p WHERE p.sponsorshipType = :sponsorshipType")
    LinkedList<Sponsorship> findBySponsorshipType(@Param("sponsorshipType") String sponsorshipType);

    @Query("SELECT p FROM Sponsorship p WHERE p.company = :company")
    Sponsorship findBySponsorshipCompany(@Param("company") String company);

}
