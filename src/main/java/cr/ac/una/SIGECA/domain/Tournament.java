/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kenda
 */
@Entity
@Table(name = "tb_tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private LocalDate startDate;
    private LocalDate endDate;
    private boolean status;
    
    private String tournamentState = "DRAFT"; // DRAFT, ACTIVE, FINISHED
    private String matchDay;                  // SABADO, DOMINGO, etc.
    private String matchTime;                 // 14:00, 16:30, etc.

    // Relacion muchos a muchos con Referee
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "tournament_referee",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "referee_id")
    )
    private List<Referee> referees = new ArrayList<>();       

    // Relacion muchos a uno con Sponsorship (opcional)
    @ManyToOne
    @JoinColumn(name = "sponsor_id", nullable = true)
    private Sponsorship sponsor;
    
    // Relacion 
    @ManyToMany(mappedBy = "tournaments")
    private List<Team> teams = new ArrayList<>();

    
    public Tournament() {
    }

    public Tournament(int id, String name, List<Referee> referees, LocalDate startDate, LocalDate endDate, boolean status) {
        this.id = id;
        this.name = name;
        this.referees = referees;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Tournament(int id, String name, List<Referee> referees, Sponsorship sponsor, LocalDate startDate, LocalDate endDate, boolean status) {
        this.id = id;
        this.name = name;
        this.referees = referees;
        this.sponsor = sponsor;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Referee> getReferees() {
        return referees;
    }

    public void setReferees(List<Referee> referees) {
        this.referees = referees;
    }

    public Sponsorship getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsorship sponsor) {
        this.sponsor = sponsor;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }    
    
    public String getTournamentState() {
        return tournamentState;
    }

    public void setTournamentState(String tournamentState) {
        this.tournamentState = tournamentState;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }
}