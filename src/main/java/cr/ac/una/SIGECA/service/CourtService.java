package cr.ac.una.SIGECA.service;

import cr.ac.una.SIGECA.domain.Court;
import cr.ac.una.SIGECA.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourtService {

    private static final List<String> VALID_COURT_TYPES = List.of(
            "Fútbol 5", "Fútbol 6", "Fútbol 7", "Fútbol 11", "Futsal", "Fútbol Playa"
    );

    @Autowired
    private CourtRepository courtRepository;

    public List<Court> listCourts() {
        return courtRepository.findAll();
    }

    public List<Court> listAvailableCourts() {
        return courtRepository.findAll().stream()
                .filter(c -> "Disponible".equalsIgnoreCase(c.getStatusCourt()))
                .toList();
    }

    public Court saveCourt(Court court) {
        // Validar campos requeridos
        if (court.getNameCourt() == null || court.getNameCourt().isEmpty()
                || court.getTypeOfCourt() == null || court.getTypeOfCourt().isEmpty()
                || !VALID_COURT_TYPES.contains(court.getTypeOfCourt())
                || court.getLocation() == null || court.getLocation().isEmpty()
                || court.getPriceByHour() <= 0
                || court.getStatusCourt() == null || court.getStatusCourt().isEmpty()
                || court.getSurfaceType() == null || court.getSurfaceType().isEmpty()) {

            throw new IllegalArgumentException("Todos los campos son obligatorios y deben contener valores válidos. "
                    + "El precio por hora debe ser mayor que 0.");
        }
        return courtRepository.save(court);
    }

    public void deleteCourt(int idCourt) {
        courtRepository.deleteById(idCourt);
    }

    public List<Court> filterCourtsByQuery(String query) {
        return query != null && !query.isEmpty()
                ? courtRepository.findByQuery(query)
                : courtRepository.findAll();
    }

    public Court findById(int idCourt) {
        return courtRepository.findById(idCourt).orElse(null);
    }

    public List<Court> findAll() {
        return courtRepository.findAll(); // Método que obtiene todas las canchas
    }
}
