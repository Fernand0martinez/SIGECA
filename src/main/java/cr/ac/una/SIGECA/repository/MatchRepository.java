package cr.ac.una.SIGECA.repository;

import cr.ac.una.SIGECA.domain.Match;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findByTournamentId(int tournamentId);

    List<Match> findByTournamentIdOrderByRoundAscMatchDateAsc(int tournamentId);

    List<Match> findByTournamentIdAndRound(int tournamentId, int round);
}
