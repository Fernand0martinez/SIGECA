/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.JPA.PaymentRepository;
import cr.ac.una.SIGECA.domain.Payment;
import cr.ac.una.SIGECA.domain.PaymentMethod;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ferna
 */
@Service
public class PaymentService implements CRUD<Payment> {
@Autowired
private PaymentRepository pay;
    @Override
    public void save(Payment t) {
        pay.save(t);
    }

    @Override
    public void delete(int i) {
        pay.deleteById(i);
    }
    

    @Override
    public List<Payment> getAll() {
        return pay.findAll();
    }

    @Override
    public Payment getById(int i) {
    return pay.getReferenceById(i);
}

    public List<Payment> getByIdUser(int id) {
        return pay.findByIdUser(id);
    }

    public List<Payment> findByUserIdAndMethod(int id, PaymentMethod selectedMethod) {
        return pay.findByUserIdAndMethod(id, selectedMethod);
    }
    
    
}
