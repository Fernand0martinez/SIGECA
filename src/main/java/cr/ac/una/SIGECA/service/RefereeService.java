/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.repository.RefereeRepository;
import cr.ac.una.SIGECA.domain.Referee;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author kenda
 */
@Service
public class RefereeService implements CRUD<Referee> {
    
    @Autowired
    private RefereeRepository repoReferee;

    @Override
    public void save(Referee t) {
        if(t.getName() == null || t.getName().isBlank()) {
            throw new IllegalArgumentException("Referee name is required.");
        }
        if(t.getLastName() == null || t.getLastName().isBlank()) {
            throw new IllegalArgumentException("Referee last name is required.");
        }
        if(t.getMail() == null || t.getMail().isBlank()) {
            throw new IllegalArgumentException("Referee mail is required.");
        }
        repoReferee.save(t);
    }

    @Override
    public void delete(int i) {
        repoReferee.deleteById(i);
    }
    
    @Override
    public List<Referee> getAll() {
        return repoReferee.findAll();
    }

    public boolean existReferee(int i){
        return repoReferee.existsById(i);
    }
    
    @Override
    public Referee getById(int i) {
        return repoReferee.findById(i).orElseThrow(() -> new EntityNotFoundException("Referee not found with id " + i));
    }
    
}
