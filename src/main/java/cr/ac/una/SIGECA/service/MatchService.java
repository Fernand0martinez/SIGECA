package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.repository.MatchRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchService implements CRUD<Match> {

    @Autowired
    private MatchRepository matchRepository;

    @Override
    public void save(Match m) {
        if (m.getTournament() == null) {
            throw new IllegalArgumentException("El partido debe pertenecer a un torneo.");
        }
        if (m.getHomeTeam() == null || m.getAwayTeam() == null) {
            throw new IllegalArgumentException("Un partido debe tener un equipo local y uno visitante.");
        }
        matchRepository.save(m);
    }

    @Override
    public void delete(int id) {
        matchRepository.deleteById(id);
    }

    @Override
    public List<Match> getAll() {
        return matchRepository.findAll();
    }

    @Override
    public Match getById(int id) {
        return matchRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Partido no encontrado con id " + id));
    }

    public List<Match> getMatchesByTournament(int tournamentId) {
        return matchRepository.findByTournamentIdOrderByRoundAscMatchDateAsc(tournamentId);
    }

    public List<Match> getMatchesByTournamentAndRound(int tournamentId, int round) {
        return matchRepository.findByTournamentIdAndRound(tournamentId, round);
    }
}
