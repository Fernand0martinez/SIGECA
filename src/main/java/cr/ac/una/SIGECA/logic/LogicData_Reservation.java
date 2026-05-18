/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.domain.Court;
import cr.ac.una.SIGECA.domain.Reservation;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.service.ReservationService;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */
@Service
public class LogicData_Reservation {

    @Autowired
    private ReservationService resv;

    public LinkedList<Reservation> getAll() {

        List<Reservation> list = resv.getAll();
        return new LinkedList<>(list);

    }

    public void save(Reservation sponsorship) {
        resv.save(sponsorship);
    }

    public void delete(int id) {
        resv.delete(id);
    }

    public Reservation getReservation(int id) {
        return resv.getById(id);
    }

    public List<Reservation> getAllByUser(User user) {
        return resv.getByUser(user);
    }

    public boolean isCourtAvailable(Court court, LocalDate date, int startHour, int endHour) {
        List<Reservation> existingReservations = getAll();

        for (Reservation res : existingReservations) {
            if (res.getCourt().getIdCourt() == court.getIdCourt() && res.getDate().equals(date)) {
                if (startHour < res.getEndHour() && endHour > res.getStartHour()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCourtAvailablee(Court court, LocalDate date, int startHour, int endHour, Integer excludeId) {
        List<Reservation> existingReservations = getAll();

        for (Reservation res : existingReservations) {

            if (res.getId() == excludeId) {
                continue;
            }

            if (res.getCourt().getIdCourt() == court.getIdCourt() && res.getDate().equals(date)) {
                if (startHour < res.getEndHour() && endHour > res.getStartHour()) {
                    return false;
                }
            }
        }
        return true;
    }

    public double calculateTotalPrice(Court court, int startHour, int endHour) {
        int hoursReserved = endHour - startHour;
        return hoursReserved * court.getPriceByHour();
    }

    public Reservation findById(Integer id) {

        return resv.findById(id); // esto funciona si resv está bien inyectado

    }

}
