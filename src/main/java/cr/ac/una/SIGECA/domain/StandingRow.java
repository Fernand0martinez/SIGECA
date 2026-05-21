package cr.ac.una.SIGECA.domain;

public class StandingRow {
    private Team team;
    private int matchesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public StandingRow(Team team) {
        this.team = team;
        this.matchesPlayed = 0;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.points = 0;
    }

    public Team getTeam() {
        return team;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void addMatch() {
        this.matchesPlayed++;
    }

    public int getWins() {
        return wins;
    }

    public void addWin(int points) {
        this.wins++;
        this.points += points;
    }

    public int getDraws() {
        return draws;
    }

    public void addDraw(int points) {
        this.draws++;
        this.points += points;
    }

    public int getLosses() {
        return losses;
    }

    public void addLoss(int points) {
        this.losses++;
        this.points += points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void addGoalsFor(int goals) {
        this.goalsFor += goals;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void addGoalsAgainst(int goals) {
        this.goalsAgainst += goals;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "StandingRow{" +
                "team=" + team.getName() +
                ", PJ=" + matchesPlayed +
                ", PG=" + wins +
                ", PE=" + draws +
                ", PP=" + losses +
                ", GF=" + goalsFor +
                ", GC=" + goalsAgainst +
                ", DG=" + getGoalDifference() +
                ", Pts=" + points +
                '}';
    }
}
