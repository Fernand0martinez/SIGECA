package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.domain.ScheduleMode;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.repository.MatchRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
        // 1. Validaciones iniciales
        if (!"DRAFT".equals(tournament.getTournamentState())) {
            throw new IllegalStateException("Solo se puede generar el fixture si el torneo está en estado DRAFT.");
        }

        List<Match> existingMatches = matchRepository.findByTournamentId(tournament.getId());
        if (!existingMatches.isEmpty()) {
            throw new IllegalStateException("El fixture ya ha sido generado para este torneo.");
        }

        List<Team> teams = new ArrayList<>(tournament.getTeams());
        if (teams.size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos 2 equipos para generar un fixture.");
        }

        // 2. Preparación de equipos
        if (teams.size() % 2 != 0) {
            teams.add(null); // Equipo fantasma para BYE
        }

        int numTeams = teams.size();
        int numRoundsPerLeg = numTeams - 1;
        int legs = tournament.getRoundRobinLegs() > 0 ? tournament.getRoundRobinLegs() : 1;
        
        List<Match> generatedMatches = new ArrayList<>();
        List<Referee> referees = tournament.getReferees();
        int refereeIndex = 0;

        // 3. Configuración de fechas y horarios
        List<DayOfWeek> allowedDays = parseGameDays(tournament);
        LocalTime gameTime = parseGameTime(tournament.getMatchTime());
        LocalDateTime currentMatchDateTime = findFirstMatchDateTime(tournament.getStartDate(), allowedDays, gameTime);
        
        int matchesInCurrentWeek = 0;
        int maxMatchesPerWeek = tournament.getMatchesPerWeek() > 0 ? tournament.getMatchesPerWeek() : Integer.MAX_VALUE;

        // 4. Generación por vueltas (Legs)
        for (int leg = 0; leg < legs; leg++) {
            for (int round = 0; round < numRoundsPerLeg; round++) {
                int absoluteRound = (leg * numRoundsPerLeg) + round + 1;
                
                for (int i = 0; i < numTeams / 2; i++) {
                    int homeIndex = (round + i) % (numTeams - 1);
                    int awayIndex = (numTeams - 1 - i + round) % (numTeams - 1);

                    if (i == 0) {
                        awayIndex = numTeams - 1;
                    }

                    Team homeTeam = teams.get(homeIndex);
                    Team awayTeam = teams.get(awayIndex);

                    if (homeTeam == null || awayTeam == null) {
                        continue;
                    }

                    // Alternar localía por vuelta y por ronda
                    Match match;
                    boolean isEvenRound = round % 2 == 0;
                    boolean isSecondLeg = leg % 2 != 0;
                    
                    if (isEvenRound ^ isSecondLeg) {
                        match = new Match(tournament, homeTeam, awayTeam, currentMatchDateTime, absoluteRound);
                    } else {
                        match = new Match(tournament, awayTeam, homeTeam, currentMatchDateTime, absoluteRound);
                    }

                    // Asignación de árbitro
                    if (referees != null && !referees.isEmpty()) {
                        match.setReferee(referees.get(refereeIndex % referees.size()));
                        refereeIndex++;
                    }

                    generatedMatches.add(match);
                    
                    // Avanzar a la siguiente fecha/hora disponible
                    matchesInCurrentWeek++;
                    if (matchesInCurrentWeek >= maxMatchesPerWeek) {
                        currentMatchDateTime = findNextMatchDateTime(currentMatchDateTime, allowedDays, true);
                        matchesInCurrentWeek = 0;
                    } else {
                        currentMatchDateTime = findNextMatchDateTime(currentMatchDateTime, allowedDays, false);
                    }
                }
                
                // Si la ronda terminó y no hemos avanzado de semana, forzamos avance para la siguiente jornada si aplica
                if (matchesInCurrentWeek > 0 && tournament.getScheduleMode() == ScheduleMode.WEEKLY) {
                    currentMatchDateTime = findNextMatchDateTime(currentMatchDateTime, allowedDays, true);
                    matchesInCurrentWeek = 0;
                }
            }
        }

        return matchRepository.saveAll(generatedMatches);
    }

    private List<DayOfWeek> parseGameDays(Tournament t) {
        List<DayOfWeek> days = new ArrayList<>();
        String gameDays = t.getGameDays();
        
        if (gameDays == null || gameDays.isBlank()) {
            days.add(mapDayOfWeek(t.getMatchDay()));
        } else {
            for (String d : gameDays.split(",")) {
                days.add(mapDayOfWeek(d.trim()));
            }
        }
        return days;
    }

    private LocalDateTime findFirstMatchDateTime(LocalDate start, List<DayOfWeek> allowedDays, LocalTime time) {
        LocalDate date = start;
        while (!allowedDays.contains(date.getDayOfWeek())) {
            date = date.plusDays(1);
        }
        return LocalDateTime.of(date, time);
    }

    private LocalDateTime findNextMatchDateTime(LocalDateTime current, List<DayOfWeek> allowedDays, boolean forceNextWeek) {
        LocalDateTime next = current;
        if (forceNextWeek) {
            // Ir al primer día permitido de la siguiente semana
            next = next.plusDays(7 - next.getDayOfWeek().getValue() + allowedDays.get(0).getValue());
            return next;
        }

        // Buscar el siguiente día permitido en la misma semana o siguiente
        do {
            next = next.plusDays(1);
        } while (!allowedDays.contains(next.getDayOfWeek()));
        
        return next;
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
