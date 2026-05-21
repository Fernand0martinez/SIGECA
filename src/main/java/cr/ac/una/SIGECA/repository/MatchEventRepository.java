package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchEventRepository extends JpaRepository<MatchEvent, Integer> {
    List<MatchEvent> findByMatchId(int matchId);
}
