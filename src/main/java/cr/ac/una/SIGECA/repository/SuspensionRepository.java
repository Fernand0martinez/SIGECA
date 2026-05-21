package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspensionRepository extends JpaRepository<Suspension, Integer> {
    List<Suspension> findByPlayerIdAndActiveTrue(int playerId);
    List<Suspension> findByTournamentIdAndPlayerIdAndActiveTrue(int tournamentId, int playerId);
}
