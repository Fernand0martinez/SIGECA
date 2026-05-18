
package cr.ac.una.SIGECA.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 *
 * @author ferna
 */
@Entity
@Table(name="tb_player")
public class Player extends Person {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    private int dorsal;
    private String  position;
    private int goal;
    private int assist;
    private String admonished;
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Player(){
        
    }

    public Player(String id, String name, String lastName, String mail, int age, char gender,int dorsal, String position, int goal, int assist, String admonished) {
        super(id, name, lastName, mail, age, gender);
        this.dorsal = dorsal;
        this.position = position;
        this.goal = goal;
        this.assist = assist;
        this.admonished = admonished;
    }

    public Player(int id, int dorsal, String position, int goal, int assist, String admonished, String idCard, String name, String lastName, String mail, int age, char gender) {
        super(idCard, name, lastName, mail, age, gender);
        this.id = id;
        this.dorsal = dorsal;
        this.position = position;
        this.goal = goal;
        this.assist = assist;
        this.admonished = admonished;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }  
       
    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getAssist() {
        return assist;
    }

    public void setAssist(int assist) {
        this.assist = assist;
    }

    public String getAdmonished() {
        return admonished;
    }

    public void setAdmonished(String admonished) {
        this.admonished = admonished;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }       
    
    @Override
    public String toString() {
        return  super.toString() + "dorsal=" + dorsal +  ", position=" + position + ", goal=" + goal + ", assist=" + assist + ", admonished=" + admonished ;
    }
}