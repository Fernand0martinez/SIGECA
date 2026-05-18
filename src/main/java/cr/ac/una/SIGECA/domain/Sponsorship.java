/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_sponsorship")
public class Sponsorship {

    @Id
    private int id;

    private String company;
    private double amount;

    private LocalDate contractStart;
    private LocalDate contractEnd;

    private String advertisingSpace;
    private boolean isActive;
    private String sponsorshipType;
    private String phoneNumber;

    public Sponsorship() {
    }

    public Sponsorship(int id, String company, double amount, LocalDate contractStart, LocalDate contractEnd,
            String advertisingSpace, boolean isActive, String sponsorshipType, String phoneNumber) {
        this.id = id;
        this.company = company;
        this.amount = amount;
        this.contractStart = contractStart;
        this.contractEnd = contractEnd;
        this.advertisingSpace = advertisingSpace;
        this.isActive = isActive;
        this.sponsorshipType = sponsorshipType;
        this.phoneNumber = phoneNumber;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getContractStart() {
        return contractStart;
    }

    public void setContractStart(LocalDate contractStart) {
        this.contractStart = contractStart;
    }

    public LocalDate getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(LocalDate contractEnd) {
        this.contractEnd = contractEnd;
    }

    public String getAdvertisingSpace() {
        return advertisingSpace;
    }

    public void setAdvertisingSpace(String advertisingSpace) {
        this.advertisingSpace = advertisingSpace;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getSponsorshipType() {
        return sponsorshipType;
    }

    public void setSponsorshipType(String sponsorshipType) {
        this.sponsorshipType = sponsorshipType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return id + "-" + company + "-" + amount + "-" + contractStart + "-" + contractEnd + "-"
            + advertisingSpace + "-" + isActive + "-" + sponsorshipType + "-" + phoneNumber;
    }
}

