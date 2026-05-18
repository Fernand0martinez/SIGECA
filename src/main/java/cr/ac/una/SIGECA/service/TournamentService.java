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
        
}
