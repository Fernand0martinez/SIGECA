
package cr.ac.una.SIGECA.service;
import cr.ac.una.SIGECA.JPA.PlayerRepository;
import cr.ac.una.SIGECA.domain.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ferna
 */
@Service
public class PlayerService implements CRUD<Player> {
@Autowired

     PlayerRepository player;
    @Override
    public  void save(Player i) {
        player.save(i);
    }

    @Override
    public void delete(int id) {
        player.deleteById(id);
    }

    @Override
    public List<Player> getAll() {
        return player.findAll();
    }

    public List< Player> getIdByGender(char gender) {
        return player.findByGender(gender);
    }
    
    public  boolean playerExist(String id){
       return player.findByIdCard(id)!=null;
    }
    
     public   Player getPLayerByID(String id) {
      return player.findByIdCard(id);
    }
       public   List<Player> getIdByPosition(String position) {
        return player.findByPosition(position);
    }

    @Override
    public Player getById(int i) {
        return player.getReferenceById(i);
    }

    public List<Integer> getAvailableDorsals(Integer currentPlayerId) {
        Set<Integer> usedDorsals = new HashSet<>();
        for (Player current : player.findAll()) {
            if (current.getDorsal() <= 0) {
                continue;
            }
            if (currentPlayerId != null && currentPlayerId.equals(current.getId())) {
                continue;
            }
            usedDorsals.add(current.getDorsal());
        }

        List<Integer> availableDorsals = new ArrayList<>();
        for (int dorsal = 1; dorsal <= 99; dorsal++) {
            if (!usedDorsals.contains(dorsal)) {
                availableDorsals.add(dorsal);
            }
        }
        return availableDorsals;
    }
        
}
