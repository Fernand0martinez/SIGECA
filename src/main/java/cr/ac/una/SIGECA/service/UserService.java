/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.UserRepository;
import cr.ac.una.SIGECA.domain.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author crist
 */
@Service
public class UserService implements CRUD<User> {
    @Autowired
    private UserRepository userRepo;
    
    @Override
    public void save(User u){
        userRepo.save(u);
    }

    @Override
    public void delete(int i) {
        userRepo.deleteById(i);
    }

    @Override
    public List<User> getAll() {
        return userRepo.findAll();
    }
    
    public boolean existsByIdCard(String idCard){
        return userRepo.existsByIdCard(idCard);
    }
    
    public boolean existsByMail(String mail){
        return userRepo.existsByMail(mail);
    }
    
    public User getUserByNameUser(String nameUser){
        return userRepo.findByNameUser(nameUser).orElse(null);
    }

    @Override
    public User getById(int i) {
        return userRepo.getReferenceById(i);
    }
}
