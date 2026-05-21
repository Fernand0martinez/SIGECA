package cr.ac.una.SIGECA.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_suspension")
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private String reason;
    private int remainingMatches;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match createdFromMatch;

    private LocalDateTime createdAt;

    public Suspension() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public Suspension(Player player, Tournament tournament, String reason, int remainingMatches, Match createdFromMatch) {
        this();
        this.player = player;
        this.tournament = tournament;
        this.reason = reason;
        this.remainingMatches = remainingMatches;
        this.createdFromMatch = createdFromMatch;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRemainingMatches() {
        return remainingMatches;
    }

    public void setRemainingMatches(int remainingMatches) {
        this.remainingMatches = remainingMatches;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Match getCreatedFromMatch() {
        return createdFromMatch;
    }

    public void setCreatedFromMatch(Match createdFromMatch) {
        this.createdFromMatch = createdFromMatch;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
