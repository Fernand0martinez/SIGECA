/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Reservation;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.repository.ReservationRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */
@Service
public class ReservationService implements CRUD<Reservation> {

    @Autowired
    private ReservationRepository resv;

    @Override
    public void save(Reservation r) {
        resv.save(r);
    }

    @Override
    public void delete(int i) {
        resv.deleteById(i);
    }

    @Override
    public List<Reservation> getAll() {
        return resv.findAll();
    }

    @Override
    public Reservation getById(int i) {
        return resv.getReferenceById(i);
    }

    public List<Reservation> getByUser(User user) {
        return resv.findByUser(user);
    }

    public Reservation findById(Integer id) {
        return resv.getReferenceById(id.intValue());
    }

}
