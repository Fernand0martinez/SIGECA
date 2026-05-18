/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.InvoiceRepository;
import cr.ac.una.SIGECA.domain.Invoice;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ferna
 */
@Service
public class InvoiceService implements CRUD<Invoice>{
    
@Autowired
private InvoiceRepository inv;
    @Override
    public void save(Invoice t) {
        inv.save(t);
    }

    @Override
    public void delete(int i) {
        inv.deleteById(i);
    }

    @Override
    public List<Invoice> getAll() {
        return inv.findAll();
    }

    @Override
    public Invoice getById(int i) {
        return inv.getReferenceById(i);
    }
    
}
