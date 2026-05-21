package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.*;
import cr.ac.una.SIGECA.repository.SuspensionRepository;
import cr.ac.una.SIGECA.repository.MatchEventRepository;
import cr.ac.una.SIGECA.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DisciplinaryService {

    @Autowired
    private SuspensionRepository suspensionRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Transactional
    public void processMatchDiscipline(Match match, List<MatchEvent> events) {
        for (MatchEvent event : events) {
            if (event.getEventType() == MatchEventType.RED_CARD) {
                applyRedCardSuspension(match, event);
            } else if (event.getEventType() == MatchEventType.YELLOW_CARD) {
                checkYellowCardAccumulation(match, event);
            }
        }
    }

    private void applyRedCardSuspension(Match match, MatchEvent event) {
        Suspension suspension = new Suspension(
                event.getPlayer(),
                match.getTournament(),
                "Roja Directa",
                match.getTournament().getDirectRedSuspensionMatches(),
                match
        );
        suspensionRepository.save(suspension);
    }

    private void checkYellowCardAccumulation(Match match, MatchEvent event) {
        Tournament tournament = match.getTournament();
        
        // Regla no obligatoria: si el valor es 0 o negativo, no aplicar suspensión
        if (tournament.getYellowCardsForSuspension() <= 0) {
            return;
        }

        // Obtener todos los partidos del torneo para contar amarillas históricas
        List<Match> allMatches = matchRepository.findByTournamentId(tournament.getId());
        long yellowCards = 0;
        for (Match m : allMatches) {
            yellowCards += matchEventRepository.findByMatchId(m.getId()).stream()
                    .filter(e -> e.getPlayer() != null && e.getPlayer().getId().equals(event.getPlayer().getId()))
                    .filter(e -> e.getEventType() == MatchEventType.YELLOW_CARD)
                    .count();
        }

        if (yellowCards >= tournament.getYellowCardsForSuspension()) {
            Suspension suspension = new Suspension(
                    event.getPlayer(),
                    tournament,
                    "Acumulación de Amarillas: " + yellowCards,
                    tournament.getAccumulatedYellowSuspensionMatches(),
                    match
            );
            suspensionRepository.save(suspension);
        }
    }

    public boolean isPlayerEligible(Player player, Tournament tournament) {
        return suspensionRepository.findByTournamentIdAndPlayerIdAndActiveTrue(tournament.getId(), player.getId()).isEmpty();
    }
}
