package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.*;
import cr.ac.una.SIGECA.repository.MatchEventRepository;
import cr.ac.una.SIGECA.repository.MatchRepository;
import cr.ac.una.SIGECA.repository.SuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MatchManagementService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private DisciplinaryService disciplinaryService;

    @Transactional
    public Match registerMatchResult(int matchId, int homeGoals, int awayGoals, List<MatchEvent> events) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));

        if (match.isPlayed()) {
            throw new IllegalStateException("El resultado ya fue registrado.");
        }

        // Registrar marcador
        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        // Registrar eventos
        for (MatchEvent event : events) {
            event.setMatch(match);
            matchEventRepository.save(event);
        }

        matchRepository.save(match);

        // Procesar sanciones basadas en eventos
        disciplinaryService.processMatchDiscipline(match, events);

        return match;
    }

    @Transactional
    public void assignReferee(int matchId, Referee referee) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partido no encontrado."));
        match.setReferee(referee);
        matchRepository.save(match);
    }
}
