package cr.ac.una.SIGECA.logic;


import cr.ac.una.SIGECA.domain.Sponsorship;
import cr.ac.una.SIGECA.service.SponsorshipService;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Usuario
 */
@Service
public class LogicData {

    @Autowired
    private SponsorshipService spo;

    public LinkedList<Sponsorship> getAll() {

        List<Sponsorship> list = spo.getAll();
        return new LinkedList<>(list);

    }

    public void save(Sponsorship sponsorship) {
        spo.save(sponsorship);
    }

    public void delete(int id) {
        spo.delete(id);
    }

    public Sponsorship getSponsorship(int id) {
        return spo.getById(id);
    }

    //metodo para filtrar los patrocinios
    public void filterSponsorship(LinkedList<Sponsorship> list, StringBuilder message, String sponsorshipType, String advertisingSpace) {

        boolean typeIsValid = sponsorshipType != null && !sponsorshipType.isEmpty() && !sponsorshipType.equals("0");
        boolean spaceIsValid = advertisingSpace != null && !advertisingSpace.isEmpty() && !advertisingSpace.equals("0");

        if (!typeIsValid && !spaceIsValid) {
            list.clear();
            list.addAll(spo.getAll());

            message.append("No se seleccionaron filtros. Mostrando todos los patrocinios.");
        } else {
            if (typeIsValid) {
                list.clear();
                list.addAll(spo.filterBySponsorshipType(sponsorshipType));
                if (list.isEmpty()) {
                    message.append("No hay patrocinios registrados para el tipo seleccionado.");
                }
            }

            if (spaceIsValid) {
                LinkedList<Sponsorship> filteredBySpace = spo.filterByAdvertisingSpace(advertisingSpace);
                if (!filteredBySpace.isEmpty()) {
                    if (list.isEmpty()) {
                        list.addAll(filteredBySpace);
                    } else {
                        list.retainAll(filteredBySpace);
                    }
                } else if (list.isEmpty()) {
                    message.append("No hay patrocinios registrados para el espacio seleccionado.");
                }
            }
        }

    }
}
