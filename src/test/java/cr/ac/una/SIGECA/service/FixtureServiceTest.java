package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Match;
import cr.ac.una.SIGECA.domain.Referee;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class FixtureServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private FixtureService fixtureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateFixture_LessThanTwoTeams_ThrowsException() {
        Tournament tournament = new Tournament();
        List<Team> teams = new ArrayList<>();
        Team team1 = new Team();
        team1.setId(1);
        teams.add(team1);
        tournament.setTeams(teams);

        assertThrows(IllegalArgumentException.class, () -> {
            fixtureService.generateFixture(tournament);
        });
    }

    @Test
    void testGenerateFixture_EvenNumberOfTeams() {
        Tournament tournament = new Tournament();
        tournament.setId(1);
        tournament.setMatchDay("Sábado");
        tournament.setMatchTime("16:00");
        tournament.setStartDate(LocalDate.of(2026, 5, 23)); // A Saturday

        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Team t = new Team();
            t.setId(i);
            t.setName("Team " + i);
            teams.add(t);
        }
        tournament.setTeams(teams);

        List<Referee> referees = new ArrayList<>();
        Referee r1 = new Referee();
        r1.setId(1);
        referees.add(r1);
        tournament.setReferees(referees);

        when(matchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Match> result = fixtureService.generateFixture(tournament);

        assertNotNull(result);
        // For 4 teams: numRounds = 3, matchesPerRound = 2. Total matches = 6.
        assertEquals(6, result.size());

        // Verify each match has a round, date, and teams are different
        for (Match match : result) {
            assertNotNull(match.getHomeTeam());
            assertNotNull(match.getAwayTeam());
            assertNotEquals(match.getHomeTeam().getId(), match.getAwayTeam().getId());
            assertEquals(16, match.getMatchDate().getHour());
            assertEquals(0, match.getMatchDate().getMinute());
            assertEquals(1, match.getReferee().getId());
            assertTrue(match.getRound() >= 1 && match.getRound() <= 3);
        }

        verify(matchRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGenerateFixture_OddNumberOfTeams() {
        Tournament tournament = new Tournament();
        tournament.setId(2);
        tournament.setMatchDay("Domingo");
        tournament.setMatchTime("10:00");
        tournament.setStartDate(LocalDate.of(2026, 5, 24)); // A Sunday

        List<Team> teams = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Team t = new Team();
            t.setId(i);
            t.setName("Team " + i);
            teams.add(t);
        }
        tournament.setTeams(teams);

        when(matchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Match> result = fixtureService.generateFixture(tournament);

        assertNotNull(result);
        // For 3 teams (augmented to 4 with a BYE slot): 
        // 3 rounds, but in each round one match is home/away vs null (which is skipped).
        // Total saved matches should be 3 (one per round).
        assertEquals(3, result.size());

        for (Match match : result) {
            assertNotNull(match.getHomeTeam());
            assertNotNull(match.getAwayTeam());
            assertEquals(10, match.getMatchDate().getHour());
            assertEquals(0, match.getMatchDate().getMinute());
        }

        verify(matchRepository, times(1)).saveAll(anyList());
    }
}
