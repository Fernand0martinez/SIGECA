package cr.ac.una.SIGECA.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "id_team")
    private Integer id;

    @NotBlank(message = "Team name is mandatory")
    @jakarta.persistence.Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> members = new ArrayList<>();

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

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    @Transient
    public List<Player> getPlayers() {
        return members.stream()
                .map(TeamMember::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transient
    public Player getCaptain() {
        return members.stream()
                .filter(TeamMember::isCaptain)
                .map(TeamMember::getPlayer)
                .findFirst()
                .orElse(null);
    }

    public void addMember(TeamMember member) {
        members.add(member);
        member.setTeam(this);
    }

    public void removeMember(TeamMember member) {
        members.remove(member);
        member.setTeam(null);
    }

    public void addTournament(Tournament tournament) {
        tournaments.add(tournament);
        tournament.getTeams().add(this);
    }

    public void removeTournament(Tournament tournament) {
        tournaments.remove(tournament);
        tournament.getTeams().remove(this);
    }

    @Override
    public String toString() {
        return "Team{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
