package cr.ac.una.SIGECA.seed;

import cr.ac.una.SIGECA.JPA.PlayerRepository;
import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.domain.Team;
import cr.ac.una.SIGECA.domain.TeamMember;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.JPA.UserRepository;
import cr.ac.una.SIGECA.repository.TeamRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitialTeamSeeder implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialTeamSeeder(
            TeamRepository teamRepository,
            PlayerRepository playerRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<SeedTeam> seedTeams = List.of(
                new SeedTeam("Halcones FC", List.of(
                        new SeedPlayer("900100100", "Andres", "Vargas", "andres.vargas@sigeca.test", 24, 1, "P"),
                        new SeedPlayer("900100101", "Luis", "Mora", "luis.mora@sigeca.test", 22, 4, "D"),
                        new SeedPlayer("900100102", "Carlos", "Rojas", "carlos.rojas@sigeca.test", 25, 8, "M"),
                        new SeedPlayer("900100103", "Diego", "Soto", "diego.soto@sigeca.test", 21, 10, "G"),
                        new SeedPlayer("900100104", "Marco", "Campos", "marco.campos@sigeca.test", 27, 12, "D"),
                        new SeedPlayer("900100105", "Jose", "Navarro", "jose.navarro@sigeca.test", 23, 15, "M")
                )),
                new SeedTeam("Tigres del Norte", List.of(
                        new SeedPlayer("900100200", "Sebastian", "Arias", "sebastian.arias@sigeca.test", 26, 1, "P"),
                        new SeedPlayer("900100201", "Kevin", "Castro", "kevin.castro@sigeca.test", 24, 3, "D"),
                        new SeedPlayer("900100202", "Bryan", "Salas", "bryan.salas@sigeca.test", 20, 6, "M"),
                        new SeedPlayer("900100203", "Daniel", "Jimenez", "daniel.jimenez@sigeca.test", 22, 9, "G"),
                        new SeedPlayer("900100204", "Esteban", "Alfaro", "esteban.alfaro@sigeca.test", 25, 11, "M"),
                        new SeedPlayer("900100205", "Pablo", "Vega", "pablo.vega@sigeca.test", 28, 14, "D")
                )),
                new SeedTeam("Leones Athletic", List.of(
                        new SeedPlayer("900100300", "Adrian", "Sanchez", "adrian.sanchez@sigeca.test", 23, 1, "P"),
                        new SeedPlayer("900100301", "Javier", "Pineda", "javier.pineda@sigeca.test", 29, 2, "D"),
                        new SeedPlayer("900100302", "Mauricio", "Lopez", "mauricio.lopez@sigeca.test", 21, 5, "M"),
                        new SeedPlayer("900100303", "Cristian", "Gomez", "cristian.gomez@sigeca.test", 24, 7, "G"),
                        new SeedPlayer("900100304", "Oscar", "Rivera", "oscar.rivera@sigeca.test", 26, 13, "D"),
                        new SeedPlayer("900100305", "Felipe", "Chaves", "felipe.chaves@sigeca.test", 22, 16, "M")
                )),
                new SeedTeam("Panteras United", List.of(
                        new SeedPlayer("900100400", "Rafael", "Torres", "rafael.torres@sigeca.test", 27, 1, "P"),
                        new SeedPlayer("900100401", "Manuel", "Acuna", "manuel.acuna@sigeca.test", 23, 4, "D"),
                        new SeedPlayer("900100402", "Gabriel", "Solano", "gabriel.solano@sigeca.test", 20, 8, "M"),
                        new SeedPlayer("900100403", "Anthony", "Brenes", "anthony.brenes@sigeca.test", 25, 10, "G"),
                        new SeedPlayer("900100404", "Hector", "Mendez", "hector.mendez@sigeca.test", 24, 12, "D"),
                        new SeedPlayer("900100405", "Emilio", "Quesada", "emilio.quesada@sigeca.test", 22, 18, "M")
                )),
                new SeedTeam("Toros Cartago", List.of(
                        new SeedPlayer("900100500", "Alejandro", "Murillo", "alejandro.murillo@sigeca.test", 26, 1, "P"),
                        new SeedPlayer("900100501", "Roberto", "Cordero", "roberto.cordero@sigeca.test", 28, 3, "D"),
                        new SeedPlayer("900100502", "Nicolas", "Herrera", "nicolas.herrera@sigeca.test", 21, 6, "M"),
                        new SeedPlayer("900100503", "Fernando", "Calderon", "fernando.calderon@sigeca.test", 24, 9, "G"),
                        new SeedPlayer("900100504", "Sergio", "Ramirez", "sergio.ramirez@sigeca.test", 23, 11, "M"),
                        new SeedPlayer("900100505", "David", "Castillo", "david.castillo@sigeca.test", 25, 17, "D")
                ))
        );

        User owner = findOrCreateDemoOwner();

        for (SeedTeam seedTeam : seedTeams) {
            Optional<Team> existingTeam = findTeamByName(seedTeam.name());
            if (existingTeam.isPresent()) {
                Team team = existingTeam.get();
                if (team.getOwner() == null) {
                    team.setOwner(owner);
                    teamRepository.save(team);
                }
                continue;
            }
            createTeam(seedTeam, owner);
        }
    }

    private Optional<Team> findTeamByName(String name) {
        return teamRepository.findAll().stream()
                .filter(team -> team.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private void createTeam(SeedTeam seedTeam, User owner) {
        Team team = new Team(seedTeam.name());
        team.setOwner(owner);

        for (int index = 0; index < seedTeam.players().size(); index++) {
            SeedPlayer seedPlayer = seedTeam.players().get(index);
            Player player = findOrCreatePlayer(seedPlayer);
            TeamMember member = new TeamMember();
            member.setPlayer(player);
            member.setDorsal(seedPlayer.dorsal());
            member.setCaptain(index == 0);
            team.addMember(member);
            player.setTeam(team);
        }

        teamRepository.save(team);
    }

    private User findOrCreateDemoOwner() {
        return userRepository.findByNameUser("demo_torneos")
                .orElseGet(() -> {
                    User user = new User(
                            "demo_torneos",
                            passwordEncoder.encode("Demo1234"),
                            'U',
                            "88889999",
                            "900199999",
                            "Demo",
                            "Torneos",
                            "demo.torneos@sigeca.test",
                            30,
                            'M'
                    );
                    user.setVerified(true);
                    return userRepository.save(user);
                });
    }

    private Player findOrCreatePlayer(SeedPlayer seedPlayer) {
        Player existingPlayer = playerRepository.findByIdCard(seedPlayer.idCard());
        if (existingPlayer != null) {
            return existingPlayer;
        }

        Player player = new Player(
                seedPlayer.idCard(),
                seedPlayer.name(),
                seedPlayer.lastName(),
                seedPlayer.mail(),
                seedPlayer.age(),
                'M',
                seedPlayer.dorsal(),
                seedPlayer.position(),
                0,
                0,
                "No"
        );
        return playerRepository.save(player);
    }

    private record SeedTeam(String name, List<SeedPlayer> players) {
    }

    private record SeedPlayer(String idCard, String name, String lastName, String mail, int age, int dorsal, String position) {
    }
}
