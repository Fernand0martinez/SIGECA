/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 * @author kenda
 */
@Entity
@Table(name="tb_referee")
public class Referee extends Person{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private int experience;
    private boolean available;
    
    public Referee(){}
    
    public Referee(String id, String name, String lastname, String mail, int age, char gender, int experience, boolean available) {
        super(id, name, lastname, mail, age, gender);
        this.experience = experience;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Referee{" + "experience=" + experience + ", available=" + available + '}';
    }
    
}
