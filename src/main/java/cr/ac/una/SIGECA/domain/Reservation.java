/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "tb_reservation")
public class Reservation {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_court", referencedColumnName = "id_court")
    private Court court;

    private LocalDate date;
    private int startHour;
    private int endHour;
    private double totalPrice;
    private String status;

    public Reservation() {
        this.status = "Reservada";
    }

    public Reservation(User user, Court court, LocalDate date, int startHour, int endHour, double totalPrice, String status) {
        this.user = user;
        this.court = court;
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Reservation(User user, Court court, LocalDate date, int startHour, int endHour, double totalPrice) {
        this();
        this.user = user;
        this.court = court;
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
        this.totalPrice = totalPrice;
    }

    public Reservation(int id, User user, Court court, LocalDate date, int startHour, int endHour, double totalPrice, String status) {
        this(user, court, date, startHour, endHour, totalPrice, status);
        this.id = id;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + user + "," + court + "," + date + ","
                + startHour + "-" + endHour + "," + totalPrice + "," + status;
    }
}
