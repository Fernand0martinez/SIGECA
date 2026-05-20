package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.repository.MatchRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FixtureService {

    @Autowired
    private MatchRepository matchRepository;

    @Transactional
    public List<Match> generateFixture(Tournament tournament) {
        List<Team> teams = new ArrayList<>(tournament.getTeams());
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos 2 equipos para generar un fixture.");
        }

        // Si es impar, añadimos null como un equipo fantasma (representa jornada de descanso o BYE)
        boolean hasBye = false;
        if (teams.size() % 2 != 0) {
            teams.add(null);
            hasBye = true;
        }

        int numTeams = teams.size();
        int numRounds = numTeams - 1;
        int matchesPerRound = numTeams / 2;

        List<Match> generatedMatches = new ArrayList<>();
        List<Referee> referees = tournament.getReferees();
        int refereeIndex = 0;

        // Configuración de fechas
        DayOfWeek targetDay = mapDayOfWeek(tournament.getMatchDay());
        LocalTime gameTime = parseGameTime(tournament.getMatchTime());
        
        // Buscar el primer día de juego a partir de la fecha de inicio del torneo
        LocalDate firstMatchDate = tournament.getStartDate();
        while (firstMatchDate.getDayOfWeek() != targetDay) {
            firstMatchDate = firstMatchDate.plusDays(1);
        }

        // Algoritmo de Rotación de Berger / Round Robin
        for (int round = 0; round < numRounds; round++) {
            // Fecha para esta jornada
            LocalDate roundDate = firstMatchDate.plusWeeks(round);
            LocalDateTime matchDateTime = LocalDateTime.of(roundDate, gameTime);

            for (int i = 0; i < matchesPerRound; i++) {
                int homeIndex = (round + i) % (numTeams - 1);
                int awayIndex = (numTeams - 1 - i + round) % (numTeams - 1);

                if (i == 0) {
                    awayIndex = numTeams - 1;
                }

                Team homeTeam = teams.get(homeIndex);
                Team awayTeam = teams.get(awayIndex);

                // Si alguno de los equipos es null, representa una jornada de descanso para el otro equipo
                if (homeTeam == null || awayTeam == null) {
                    continue;
                }

                // Alternar localía en rondas pares/impares para equilibrio
                Match match;
                if (round % 2 == 0) {
                    match = new Match(tournament, homeTeam, awayTeam, matchDateTime, round + 1);
                } else {
                    match = new Match(tournament, awayTeam, homeTeam, matchDateTime, round + 1);
                }

                // Asignación rotativa de árbitros
                if (referees != null && !referees.isEmpty()) {
                    Referee referee = referees.get(refereeIndex % referees.size());
                    match.setReferee(referee);
                    refereeIndex++;
                }

                generatedMatches.add(match);
            }
        }

        // Guardar todos los partidos generados
        return matchRepository.saveAll(generatedMatches);
    }

    private DayOfWeek mapDayOfWeek(String dayStr) {
        if (dayStr == null) {
            return DayOfWeek.SATURDAY;
        }
        switch (dayStr.toUpperCase()) {
            case "LUNES":
                return DayOfWeek.MONDAY;
            case "MARTES":
                return DayOfWeek.TUESDAY;
            case "MIERCOLES":
            case "MIÉRCOLES":
                return DayOfWeek.WEDNESDAY;
            case "JUEVES":
                return DayOfWeek.THURSDAY;
            case "VIERNES":
                return DayOfWeek.FRIDAY;
            case "SABADO":
            case "SÁBADO":
                return DayOfWeek.SATURDAY;
            case "DOMINGO":
                return DayOfWeek.SUNDAY;
            default:
                return DayOfWeek.SATURDAY;
        }
    }

    private LocalTime parseGameTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return LocalTime.of(15, 0); // 3:00 PM por defecto
        }
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            return LocalTime.of(15, 0);
        }
    }
}
