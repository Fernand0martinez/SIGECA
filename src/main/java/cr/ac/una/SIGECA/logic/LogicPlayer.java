package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.service.PlayerService;

public class LogicPlayer {

    public static String validateRegister(
            String id,
            String name,
            String lastName,
            int age,
            String position,
            String mail,
            char gender,
            Integer goal,
            Integer assist,
            String admonished,
            PlayerService ser) {

        StringBuilder mens = new StringBuilder();
        String normalizedId = id == null ? "" : id.trim();
        int safeGoals = goal == null ? 0 : goal;
        int safeAssists = assist == null ? 0 : assist;

        if (normalizedId.isEmpty()) {
            mens.append("Debes agregar un ID.");
        }
        if (normalizedId.length() > 12) {
            mens.append("ID excede cantidad máxima de valores.");
        }
        if (name == null || name.isEmpty()) {
            mens.append("Debes agregar un nombre.");
        }
        if (lastName == null || lastName.isEmpty()) {
            mens.append("Debes agregar un apellido.");
        }
        if (position == null || position.isEmpty()) {
            mens.append("Debes seleccionar una posición.");
        }
        if (age <= 0) {
            mens.append("Debes agregar una edad válida.");
        }
        if (gender == ' ') {
            mens.append("Debes seleccionar un género.");
        }
        if (safeGoals < 0) {
            mens.append("Datos de goles no válidos.");
        }
        if (safeAssists < 0) {
            mens.append("Datos de asistencias no válidos.");
        }
        if (!normalizedId.isBlank() && !normalizedId.equals("-") && ser.playerExist(normalizedId)) {
            mens.append("El jugador ya se encuentra registrado.");
        }
        return mens.toString();
    }

    public static String getPosition(String position) {
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

    public static String getGender(char gender) {
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
