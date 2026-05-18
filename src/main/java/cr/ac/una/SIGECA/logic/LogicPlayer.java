
package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.service.PlayerService;

/*
 * @author ferna
 */
public class LogicPlayer {
    public static String validateRegister(String id,String name,String lastName,int age,String position,String mail,char gender,
            int dorsal,int goal,int assist,String admonished, PlayerService ser){
           StringBuilder mens = new StringBuilder();

        if (id.isEmpty()) {
            mens.append("Debes agregar un ID.");
        }
        if (id.length() > 12) {
            mens.append("ID excede cantidad máxima de valores.");
        }
        if (name.isEmpty()) {
            mens.append("Debes agregar un nombre.");
        }
        if (lastName.isEmpty()) {
            mens.append("Debes agregar un apellido.");
        }
        if (mail.isEmpty()) {
            mens.append("Debes agregar un correo.");
        }
        if (position.isEmpty()) {
            mens.append("Debes seleccionar una posición.");
        }
        if (dorsal < 0) {
            mens.append("Debes agregar un número de dorsal válido.");
        }
        if (age <= 0) {
            mens.append("Debes agregar una edad válida.");
        }
        if (gender==' '){
            mens.append("Debes seleccionar un género.");
        }
        if (goal < 0) {
            mens.append("Datos de goles no válidos.");
        }
        if (assist < 0) {
            mens.append("Datos de asistencias no válidos.");
        }if(ser.playerExist(id)){
            mens.append("El jugador ya se encuentra registrado.");
        }
        return mens.toString();
    }
    
   public static String  getPosition(String position){
        switch (position) {
            case "P":
                return "Portero";
           case "M":
                return "Mediocampista";
           case "D":
                return "Defensa";
           case "G":
                return "Delantero";

            default:
                return "Sin asignar";
        }
   }
    public static String  getGender(char gender){
        switch (gender) {
            case 'F':
                return "Femenino";
           case 'M':
                return "Masculino";
           case 'O':
                return "Otro";
     
            default:
                return "Sin asignar";
        }
   }

}
