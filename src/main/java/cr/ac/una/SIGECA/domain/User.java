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
 * @author crist
 */
@Entity
@Table(name="tb_user")
public class User extends Person{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String nameUser;
    private String password;
    private char role;
    private String phone;
    private boolean verified = false;
    private String verificationCode;

    public User() {
        
    }

    public User(int id, String name_user, String password, char role, String phone, String idCard, String name, String lastName, String mail, int age, char gender) {
        super(idCard, name, lastName, mail, age, gender);
        this.id = id;
        this.nameUser = name_user;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    public User(String name_user, String password, char role, String phone, String idCard, String name, String lastName, String mail, int age, char gender) {
        super(idCard, name, lastName, mail, age, gender);
        this.nameUser = name_user;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String id_user) {
        this.nameUser = id_user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getRole() {
        return role;
    }

    public void setRole(char role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public String toString() {
        return "User{" + "id_user=" + nameUser + ", role=" + role + ", phone=" + phone + ", verified=" + verified + '}';
    }
 
}
