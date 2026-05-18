/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.SponsorshipRepository;
import cr.ac.una.SIGECA.domain.Sponsorship;

import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */
@Service
public class SponsorshipService implements CRUD<Sponsorship> {

    @Autowired
    private SponsorshipRepository spon;

    @Override
    public void save(Sponsorship t) {
        spon.save(t);
    }

    @Override
    public void delete(int i) {
        spon.deleteById(i);
    }

    @Override
    public List<Sponsorship> getAll() {
        return spon.findAll();
    }

    @Override
    public Sponsorship getById(int i) {
        return spon.getReferenceById(i);
    }

    public LinkedList<Sponsorship> filterByAdvertisingSpace(String advertisingSpace) {
        return spon.findByAdvertisingSpace(advertisingSpace);
    }

    public LinkedList<Sponsorship> filterBySponsorshipType(String sponsorshipType) {
        return spon.findBySponsorshipType(sponsorshipType);
    }

    public boolean existId(int id) {
        return spon.existsById(id);
    }

    public boolean existCompany(String company) {
        return spon.findBySponsorshipCompany(company) != null;
    }

}
