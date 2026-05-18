/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.SIGECA.logic;

import cr.ac.una.SIGECA.service.SponsorshipService;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */
@Service
public class LogicSponsorship {

    @Autowired
    private SponsorshipService spo;

    //metodo para validar si el contrato esta activo
    public static boolean isSponsorshipActive(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        return (!today.isBefore(startDate) && !today.isAfter(endDate));
    }

    //metodo para validar los campos en registrar
    public String validateSponsorshipInputs(Integer companyCode, String company, String phoneNumber,
            Double amount, String contractStart, String contractEnd,
            String advertisingSpace, String sponsorshipType) {

        StringBuilder errors = new StringBuilder();

        // Validaciones básicas
        if (companyCode == null) {
            errors.append("El código de la empresa es obligatorio.<br>");
            return errors.toString();

        }
        if (spo.existId(companyCode)) {
            errors.append("El código de la empresa ya se encuentra registrado.<br>");
        }

        if (company == null || company.trim().isEmpty()) {
            errors.append("El nombre de la empresa es obligatorio.<br>");
        }
        if (spo.existCompany(company)) {
            errors.append("El nombre de la empresa ya se encuentra registrado.<br>");
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errors.append("El número de teléfono es obligatorio.<br>");
        } else {
            // Eliminar espacios en blanco 
            String trimmedPhoneNumber = phoneNumber.trim();

            // Validar que sea numérico y tenga exactamente 8 dígitos
            boolean esNumerico = true;
            for (char c : trimmedPhoneNumber.toCharArray()) {
                if (!Character.isDigit(c)) {
                    esNumerico = false;
                    break;
                }
            }

            if (!esNumerico) {
                errors.append("El número de teléfono debe contener solo dígitos.<br>");
            } else if (trimmedPhoneNumber.length() != 8) {
                errors.append("El número de teléfono debe tener exactamente 8 dígitos.<br>");
            }
        }

        if (amount == null) {
            errors.append("El monto es obligatorio.<br>");
        } else {
            try {

                if (amount <= 0) {
                    errors.append("El monto debe ser un número positivo.<br>");
                }
            } catch (NumberFormatException e) {
                errors.append("El monto debe ser un valor numérico válido.<br>");
            }
        }

        if (contractStart == null || contractStart.trim().isEmpty()) {
            errors.append("La fecha de inicio del contrato es obligatoria.<br>");
        }

        if (contractEnd == null || contractEnd.trim().isEmpty()) {
            errors.append("La fecha de fin del contrato es obligatoria.<br>");
        }

        if (advertisingSpace == null || advertisingSpace.trim().isEmpty()) {
            errors.append("Debe seleccionar un espacio publicitario.<br>");
        }

        if (sponsorshipType == null || sponsorshipType.trim().isEmpty()) {
            errors.append("Debe seleccionar un tipo de patrocinio.<br>");
        }

        return errors.toString();
    }

    //metodo para validar los campos en registrar
    public String validateUpdateInputs(Integer companyCode, String company, String phoneNumber,
            Double amount, String contractStart, String contractEnd,
            String advertisingSpace, String sponsorshipType) {

        StringBuilder errors = new StringBuilder();

        // Validaciones básicas
        if (companyCode == null) {
            errors.append("El código de la empresa es obligatorio.<br>");
            return errors.toString();

        }
        if (company == null || company.trim().isEmpty()) {
            errors.append("El nombre de la empresa es obligatorio.<br>");
        }
        if (spo.existCompany(company)) {
            errors.append("El nombre de la empresa ya se encuentra registrado.<br>");
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            errors.append("El número de teléfono es obligatorio.<br>");
        } else {
            // Eliminar espacios en blanco 
            String trimmedPhoneNumber = phoneNumber.trim();

            // Validar que sea numérico y tenga exactamente 8 dígitos
            boolean esNumerico = true;
            for (char c : trimmedPhoneNumber.toCharArray()) {
                if (!Character.isDigit(c)) {
                    esNumerico = false;
                    break;
                }
            }

            if (!esNumerico) {
                errors.append("El número de teléfono debe contener solo dígitos.<br>");
            } else if (trimmedPhoneNumber.length() != 8) {
                errors.append("El número de teléfono debe tener exactamente 8 dígitos.<br>");
            }
        }

        if (amount == null) {
            errors.append("El monto es obligatorio.<br>");
        } else {
            try {

                if (amount <= 0) {
                    errors.append("El monto debe ser un número positivo.<br>");
                }
            } catch (NumberFormatException e) {
                errors.append("El monto debe ser un valor numérico válido.<br>");
            }
        }

        if (contractStart == null || contractStart.trim().isEmpty()) {
            errors.append("La fecha de inicio del contrato es obligatoria.<br>");
        }

        if (contractEnd == null || contractEnd.trim().isEmpty()) {
            errors.append("La fecha de fin del contrato es obligatoria.<br>");
        }

        if (advertisingSpace == null || advertisingSpace.trim().isEmpty()) {
            errors.append("Debe seleccionar un espacio publicitario.<br>");
        }

        if (sponsorshipType == null || sponsorshipType.trim().isEmpty()) {
            errors.append("Debe seleccionar un tipo de patrocinio.<br>");
        }

        return errors.toString();
    }
}
