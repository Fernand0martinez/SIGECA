package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Court;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourtRepository extends JpaRepository<Court, Integer> {

    @Query("SELECT c FROM Court c WHERE LOWER(c.nameCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.typeOfCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.location) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.statusCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.surfaceType) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Court> findByQuery(@Param("query") String query);

    @Query("SELECT c FROM Court c WHERE (LOWER(c.nameCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.typeOfCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.location) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.statusCourt) LIKE LOWER(CONCAT('%', :query, '%')) "
            + "OR LOWER(c.surfaceType) LIKE LOWER(CONCAT('%', :query, '%'))) "
            + "AND LOWER(c.statusCourt) = 'disponible'")
    List<Court> findAvailableByQuery(@Param("query") String query);
}
