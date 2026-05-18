
package cr.ac.una.SIGECA.service;
import cr.ac.una.SIGECA.JPA.PlayerRepository;
import cr.ac.una.SIGECA.domain.Player;
import java.util.List;
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
       
}
