package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team,Integer> {

    List<Team> findByOwnerId(Integer ownerId);

    // busca por fragmento de nombre (campo "name" en la entidad)
    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Team> findByNameContaining(@Param("query") String query);

    @Query("SELECT t FROM Team t WHERE t.owner.id = :ownerId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Team> findByOwnerIdAndNameContaining(@Param("ownerId") Integer ownerId, @Param("query") String query);

    // equipos inscritos en un torneo dado
    @Query("SELECT t FROM Team t JOIN t.tournaments tr WHERE tr.id = :tournamentId")
    List<Team> findByTournamentId(@Param("tournamentId") Integer tournamentId); 
}
