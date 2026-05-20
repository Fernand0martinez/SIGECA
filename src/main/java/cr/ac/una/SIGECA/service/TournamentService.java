/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Tournament;
import cr.ac.una.SIGECA.repository.TournamentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author kenda
 */
@Service
public class TournamentService implements CRUD<Tournament>{
    
    @Autowired
    private TournamentRepository repoTournament;

    @Autowired
    private cr.ac.una.SIGECA.repository.TeamRepository repoTeam;

    @Autowired
    private FixtureService fixtureService;

    @Override
    public void save(Tournament t) {
        if(t.getName() == null || t.getName().isBlank()) {
            throw new IllegalArgumentException("Tournament name is required.");
        }
        if(t.getStartDate() == null) {
            throw new IllegalArgumentException("Tournament start date is required.");
        }
        if(t.getEndDate() == null) {
            throw new IllegalArgumentException("Tournament end date is required.");
        }
        repoTournament.save(t);
    }

    @Override
    public void delete(int i) {
        repoTournament.deleteById(i);
    }

    @Override
    public List<Tournament> getAll() {
        return repoTournament.findAll();
    }
    
    public boolean existReferee(int i){
        return repoTournament.existsById(i);
    }
    
    @Override
    public Tournament getById(int i) {
        return repoTournament.findById(i).orElseThrow(() -> new EntityNotFoundException("Tournament not found with id " + i));
    }

    @org.springframework.transaction.annotation.Transactional
    public void addTeamToTournament(int tournamentId, int teamId) {
        Tournament tournament = getById(tournamentId);
        if (!"DRAFT".equals(tournament.getTournamentState())) {
            throw new IllegalStateException("Solo se pueden agregar equipos si el torneo está en borrador.");
        }
        cr.ac.una.SIGECA.domain.Team team = repoTeam.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con id " + teamId));
        if (tournament.getTeams().contains(team)) {
            throw new IllegalArgumentException("El equipo ya está inscrito en este torneo.");
        }
        
        tournament.getTeams().add(team);
        team.getTournaments().add(tournament);
        
        repoTournament.save(tournament);
        repoTeam.save(team);
    }

    @org.springframework.transaction.annotation.Transactional
    public void removeTeamFromTournament(int tournamentId, int teamId) {
        Tournament tournament = getById(tournamentId);
        if (!"DRAFT".equals(tournament.getTournamentState())) {
            throw new IllegalStateException("Solo se pueden eliminar equipos si el torneo está en borrador.");
        }
        cr.ac.una.SIGECA.domain.Team team = repoTeam.findById(teamId).orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con id " + teamId));
        
        tournament.getTeams().remove(team);
        team.getTournaments().remove(tournament);
        
        repoTournament.save(tournament);
        repoTeam.save(team);
    }

    @org.springframework.transaction.annotation.Transactional
    public void launchTournament(int tournamentId) {
        Tournament tournament = getById(tournamentId);
        if (!"DRAFT".equals(tournament.getTournamentState())) {
            throw new IllegalStateException("El torneo ya ha sido lanzado o finalizado.");
        }
        if (tournament.getTeams().size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos 2 equipos inscritos para lanzar el torneo.");
        }

        // Generar fixture
        fixtureService.generateFixture(tournament);

        // Cambiar estado a ACTIVE y status a true
        tournament.setTournamentState("ACTIVE");
        tournament.setStatus(true);
        repoTournament.save(tournament);
    }
}
