package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.PlayerRepository;
import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.TeamMember;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.repository.TeamMemberRepository;
import cr.ac.una.SIGECA.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    public List<Team> listAllTeams() {
        return teamRepository.findAll();
    }

    public List<Team> listTeamsByOwner(User owner) {
        if (owner == null) {
            return List.of();
        }
        return teamRepository.findByOwnerId(owner.getId());
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

    @Transactional
    public Team saveTeamWithMembers(Team team, List<Integer> playerIds, Integer captainId, Map<Integer, Integer> dorsalByPlayer) {
        return saveTeamWithMembers(team, playerIds, captainId, dorsalByPlayer, null);
    }

    @Transactional
    public Team saveTeamWithMembers(Team team, List<Integer> playerIds, Integer captainId, Map<Integer, Integer> dorsalByPlayer, User owner) {
        if (team.getName() == null || team.getName().isBlank()) {
            throw new IllegalArgumentException("Team name is required.");
        }

        Team managedTeam = team.getId() != null
                ? teamRepository.findById(team.getId()).orElseThrow(() -> new EntityNotFoundException("Team not found with id " + team.getId()))
                : new Team();

        managedTeam.setName(team.getName().trim());
        if (owner != null) {
            managedTeam.setOwner(owner);
        }

        List<TeamMember> currentMembers = new ArrayList<>(managedTeam.getMembers());
        for (TeamMember currentMember : currentMembers) {
            Player currentPlayer = currentMember.getPlayer();
            if (currentPlayer != null
                    && currentPlayer.getTeam() != null
                    && Objects.equals(currentPlayer.getTeam().getId(), managedTeam.getId())) {
                currentPlayer.setTeam(null);
            }
            managedTeam.removeMember(currentMember);
        }

        List<Integer> selectedPlayerIds = playerIds != null ? playerIds : List.of();
        Set<Integer> usedDorsals = new HashSet<>();

        for (Integer playerId : selectedPlayerIds) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new EntityNotFoundException("Player not found with id " + playerId));

            teamMemberRepository.findByPlayerId(playerId).ifPresent(existingMember -> {
                if (existingMember.getTeam() != null
                        && existingMember.getTeam().getId() != null
                        && !Objects.equals(existingMember.getTeam().getId(), managedTeam.getId())) {
                    throw new IllegalArgumentException("El jugador " + player.getName() + " ya pertenece a otro equipo.");
                }
            });

            Integer dorsal = dorsalByPlayer.get(playerId);
            if (dorsal == null || dorsal <= 0) {
                throw new IllegalArgumentException("Debes asignar un dorsal válido al jugador " + player.getName() + ".");
            }
            if (!usedDorsals.add(dorsal)) {
                throw new IllegalArgumentException("No se puede repetir el dorsal dentro del mismo equipo.");
            }

            TeamMember member = new TeamMember();
            member.setPlayer(player);
            member.setDorsal(dorsal);
            member.setCaptain(Objects.equals(player.getId(), captainId));
            managedTeam.addMember(member);
            player.setTeam(managedTeam);
        }

        if (captainId != null && managedTeam.getCaptain() == null) {
            throw new IllegalArgumentException("El capitán debe pertenecer a la plantilla seleccionada.");
        }

        return teamRepository.save(managedTeam);
    }

    @Transactional
    public void deleteTeam(int id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id " + id));
        for (TeamMember member : new ArrayList<>(team.getMembers())) {
            Player player = member.getPlayer();
            if (player != null
                    && player.getTeam() != null
                    && Objects.equals(player.getTeam().getId(), team.getId())) {
                player.setTeam(null);
            }
            team.removeMember(member);
        }
        teamRepository.delete(team);
    }

    public List<Team> filterTeams(String query) {
        if (query == null || query.isBlank()) {
            return listAllTeams();
        }
        return teamRepository.findByNameContaining(query);
    }

    public List<Team> filterTeamsByOwner(String query, User owner) {
        if (owner == null) {
            return List.of();
        }
        if (query == null || query.isBlank()) {
            return listTeamsByOwner(owner);
        }
        return teamRepository.findByOwnerIdAndNameContaining(owner.getId(), query);
    }

    public List<Team> findByTournament(int tournamentId) {
        return teamRepository.findByTournamentId(tournamentId);
    }
}
