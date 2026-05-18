
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 *
 * @author ferna
 */
@Entity
@Table(name="tb_payment")
public class Payment {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "id_reservation", referencedColumnName = "id")
    private Reservation reservation;
    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method; 
    private LocalDateTime paymentDate;
    private String description;
    
    public Payment() {
        
    }

    public Payment(int id, Reservation reservation, double amount, PaymentMethod method, LocalDateTime paymentDate,String description) {
        this.id = id;
        this.reservation = reservation;
        this.amount = amount;
        this.method = method;
        this.paymentDate = paymentDate;
        this.description= description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
