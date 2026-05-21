package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.domain.StandingRow;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandingService {

    @Autowired
    private MatchRepository matchRepository;

    public List<StandingRow> calculateStandings(Tournament tournament) {
        // 1. Obtener todos los partidos jugados del torneo
        List<Match> matches = matchRepository.findByTournamentId(tournament.getId())
                .stream()
                .filter(Match::isPlayed)
                .collect(Collectors.toList());

        // 2. Inicializar el mapa de estadísticas para todos los equipos inscritos
        Map<Integer, StandingRow> standingsMap = new HashMap<>();
        for (Team team : tournament.getTeams()) {
            standingsMap.put(team.getId(), new StandingRow(team));
        }

        // 3. Procesar resultados de los partidos
        for (Match match : matches) {
            StandingRow homeStats = standingsMap.get(match.getHomeTeam().getId());
            StandingRow awayStats = standingsMap.get(match.getAwayTeam().getId());

            if (homeStats == null || awayStats == null) continue;

            homeStats.addMatch();
            awayStats.addMatch();

            int homeGoals = match.getHomeGoals() != null ? match.getHomeGoals() : 0;
            int awayGoals = match.getAwayGoals() != null ? match.getAwayGoals() : 0;

            homeStats.addGoalsFor(homeGoals);
            homeStats.addGoalsAgainst(awayGoals);
            awayStats.addGoalsFor(awayGoals);
            awayStats.addGoalsAgainst(homeGoals);

            if (homeGoals > awayGoals) {
                homeStats.addWin(tournament.getPointsWin());
                awayStats.addLoss(tournament.getPointsLoss());
            } else if (homeGoals < awayGoals) {
                awayStats.addWin(tournament.getPointsWin());
                homeStats.addLoss(tournament.getPointsLoss());
            } else {
                homeStats.addDraw(tournament.getPointsDraw());
                awayStats.addDraw(tournament.getPointsDraw());
            }
        }

        // 4. Convertir a lista y ordenar por criterios de desempate
        List<StandingRow> standings = new ArrayList<>(standingsMap.values());
        standings.sort((a, b) -> {
            // 1. Puntos
            if (b.getPoints() != a.getPoints()) {
                return b.getPoints() - a.getPoints();
            }
            // 2. Diferencia de Goles
            if (b.getGoalDifference() != a.getGoalDifference()) {
                return b.getGoalDifference() - a.getGoalDifference();
            }
            // 3. Goles a Favor
            return b.getGoalsFor() - a.getGoalsFor();
        });

        return standings;
    }
}
