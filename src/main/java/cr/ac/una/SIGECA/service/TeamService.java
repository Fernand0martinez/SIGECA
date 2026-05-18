package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.repository.TeamRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<Team> listAllTeams() {
        return teamRepository.findAll();
    }

    public Team findById(int id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team saveTeam(Team team) {
        if (team.getName() == null || team.getName().isBlank()) {
            throw new IllegalArgumentException("Team name is required.");
        }
        return teamRepository.save(team);
    }

    public void deleteTeam(int id) {
        teamRepository.deleteById(id);
    }

    public List<Team> filterTeams(String query) {
        if (query == null || query.isBlank()) {
            return listAllTeams();
        }
        return teamRepository.findByNameContaining(query);
    }

    public List<Team> findByTournament(int tournamentId) {
        return teamRepository.findByTournamentId(tournamentId);
    }
}
