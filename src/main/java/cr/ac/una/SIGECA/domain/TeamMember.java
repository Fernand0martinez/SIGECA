package cr.ac.una.SIGECA.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_team_member")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Integer dorsal;

    @Column(name = "is_captain", nullable = false)
    private boolean captain;

    public TeamMember() {
    }

    public TeamMember(Team team, Player player, Integer dorsal, boolean captain) {
        this.team = team;
        this.player = player;
        this.dorsal = dorsal;
        this.captain = captain;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getDorsal() {
        return dorsal;
    }

    public void setDorsal(Integer dorsal) {
        this.dorsal = dorsal;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(boolean captain) {
        this.captain = captain;
    }
}
