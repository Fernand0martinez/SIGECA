package cr.ac.una.SIGECA.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_team")
    private Integer id;

    @NotBlank(message = "Team name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    // Un equipo tiene un capitán (uno de sus jugadores)
    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player captain;

    // Un equipo tiene muchos jugadores
    @OneToMany(
            mappedBy = "team",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Player> players = new ArrayList<>();

    // Un equipo puede inscribirse en muchos torneos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tournament_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    private List<Tournament> tournaments = new ArrayList<>();

    public Team() {
    }

    public Team(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getCaptain() {
        return captain;
    }

    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    // metodos de conveniencia
    public void addPlayer(Player p) {
        players.add(p);
        p.setTeam(this);
    }

    public void removePlayer(Player p) {
        players.remove(p);
        p.setTeam(null);
    }

    public void addTournament(Tournament t) {
        tournaments.add(t);
        t.getTeams().add(this);
    }

    public void removeTournament(Tournament t) {
        tournaments.remove(t);
        t.getTeams().remove(this);
    }

    @Override
    public String toString() {
        return "Team{"
                + "id=" + id
                + ", name='" + name + '\''
                + '}';
    }
}
