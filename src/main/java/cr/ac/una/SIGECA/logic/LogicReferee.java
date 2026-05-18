/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.domain.Referee;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kenda
 */
public class LogicReferee {

    public static LinkedList<Referee> getRefereesByIds(List<Referee> referees, List<Integer> ids) {
        
         LinkedList<Referee> filteredList = new LinkedList<>();
         for(Integer i:ids){
             for(Referee r:referees){
                 if(r.getId() == i){
                     filteredList.add(r);
                 }
             }
         }
         
         return filteredList;
  
    }
    
    //Condiciones para el filtrado
    public static LinkedList<Referee> filterByGender(LinkedList<Referee> referees, String gender) {
        char genderChar = gender.equalsIgnoreCase("Masculino") ? 'M' : 'F';
        LinkedList<Referee> filteredList = new LinkedList<>();

        for (Referee r : referees) {
            if (r.getGender() == genderChar) {
                filteredList.add(r);
            }
        }
        return filteredList;
    }

    public static LinkedList<Referee> filterByAvailability(LinkedList<Referee> referees, String available) {
        boolean isAvailable = available.equalsIgnoreCase("true");
        LinkedList<Referee> filteredList = new LinkedList<>();

        for (Referee r : referees) {
            if (r.isAvailable() == isAvailable) {
                filteredList.add(r);
            }
        }
        return filteredList;
    }

    public static boolean isValidFilterValue(String value) {
        return value != null && !value.isEmpty() && !value.equals("Todos");
    }
    
    
    public static String validateFields(String id, String name, String lastname, String mail, int age, char gender, int experience) {
        String errors = "";

        if (id == null || !id.matches("\\d{1,12}")) {
            errors += "El ID debe contener solo dígitos y tener un maximo de 12 caracteres.";
        }

        if (name == null || !name.matches("[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{3,40}")) {
            errors += "El nombre debe contener solo letras y espacios, y tener entre 3 y 40 caracteres.";
        }
        
        if (lastname == null || !lastname.matches("[A-Za-zÁÉÍÓÚáéíóúñÑ\\s]{3,40}")) {
            errors += "El apellido debe contener solo letras y espacios, y tener entre 3 y 40 caracteres.";
        }

        if (mail == null || !mail.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$") || mail.length() > 45) {
            errors += "El correo debe ser valido y no exceder los 45 caracteres.";
        }

        if (age < 18 || age > 70) {
            errors += "La edad debe estar entre 18 y 70 años.";
        }

        if (gender != 'M' && gender != 'F') {
            errors += "Debe seleccionar un genero valido (M o F).";
        }

        if (experience < 0 || experience > 50) {
            errors += "La experiencia debe estar entre 0 y 50 años.";
        }
         
        return errors;
    }

}
