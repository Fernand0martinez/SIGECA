/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 *
 * @author crist
 */
@MappedSuperclass
public class Person {

    @Column(name = "identification", unique = true)
    protected String idCard;
    protected String name;
    protected String lastName;
    protected String mail;
    protected int age;
    protected char gender;

    public Person(String id, String name, String lastName, String mail, int age, char gender) {
        this.idCard = id;
        this.name = name;
        this.lastName = lastName;
        this.mail = mail;
        this.age = age;
        this.gender = gender;
    }

    public Person() {
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + idCard + ", name=" + name + ", mail=" + mail + ", age=" + age + ", gender=" + gender + '}';
    }

}
