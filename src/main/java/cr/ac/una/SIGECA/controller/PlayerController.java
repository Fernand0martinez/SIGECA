package cr.ac.una.SIGECA.controller;

import cr.ac.una.SIGECA.data.UserData;
import cr.ac.una.SIGECA.domain.Player;
import cr.ac.una.SIGECA.domain.User;
import cr.ac.una.SIGECA.logic.LogicPlayer;
import cr.ac.una.SIGECA.service.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/players")
@Controller
public class PlayerController {

    private final PlayerService ser;
    public static List<Player> players = new ArrayList<>();
    private static final int PLAYERS_PER_PAGE = 5;

    public PlayerController(PlayerService ser) {
        this.ser = ser;
    }

    private void populatePlayersModel(Model model, List<Player> sourcePlayers, int page) {
        int totalPlayers = sourcePlayers.size();
        int totalPages = (int) Math.ceil((double) totalPlayers / PLAYERS_PER_PAGE);

        if (page < 1) {
            page = 1;
        }
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<Player> currentPagePlayers = getPagedPlayers(sourcePlayers, page);
        model.addAttribute("players", currentPagePlayers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
    }

    private String resolvePlayerListView(User user, boolean isAjax) {
        if (user != null && user.getRole() == 'U') {
            return isAjax ? "player/list_players2 :: contenido" : "player/list_players2";
        }
        return isAjax ? "player/list_players :: contenido" : "player/list_players";
    }

    @GetMapping("/admin/details")
    public String detailsPlayer(@RequestParam("id") int id, Model model, HttpServletRequest request) {
        Player p = ser.getById(id);
        model.addAttribute("player", p);
        model.addAttribute("position", LogicPlayer.getPosition(p.getPosition()));
        model.addAttribute("gender", LogicPlayer.getGender(p.getGender()));
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "player/detallesJugador :: contenido";
        }
        return "player/detallesJugador";
    }

    @GetMapping("/admin/create")
    public String formPlayer(HttpServletRequest request, Model model) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "player/form_jugador :: contenido";
        }
        return "player/form_jugador";
    }

    @GetMapping("/admin/home")
    public String inicio() {
        return "index";
    }

    @PostMapping("/admin/save")
    public String savePlayer(
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") int age,
            @RequestParam("position") String position,
            @RequestParam(value = "mail", required = false) String mail,
            @RequestParam("gender") char gender,
            @RequestParam(value = "goal", required = false) Integer goal,
            @RequestParam(value = "assist", required = false) Integer assist,
            @RequestParam("admonished") String admonished,
            HttpServletRequest request,
            Model model
    ) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        User user = UserData.getUser();
        String mensaje = LogicPlayer.validateRegister(id, name, lastName, age, position, mail, gender, goal, assist, admonished, ser);

        if (!mensaje.isEmpty()) {
            model.addAttribute("mensaje", mensaje);
            model.addAttribute("tipo", "Alerta");
            return isAjax ? "player/form_jugador :: contenido" : "player/form_jugador";
        }

        Player player = new Player(id, name, lastName, mail == null ? "" : mail, age, gender, 0, position, goal == null ? 0 : goal, assist == null ? 0 : assist, admonished);
        ser.save(player);
        players = ser.getAll();

        model.addAttribute("mensaje", "Registro exitoso del jugador.");
        model.addAttribute("tipo", "success");
        populatePlayersModel(model, players, 1);

        return resolvePlayerListView(user, isAjax);
    }

    @GetMapping("/admin/list")
    public String listPlayer(
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model) {
        model.addAttribute("mensaje", "Debe seleccionar un tipo de filtro de jugadores.");
        model.addAttribute("tipo", "Alerta");
        populatePlayersModel(model, players, page);

        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "player/list_players :: contenido";
        }
        return "player/list_players";
    }

    @GetMapping("/filter")
    public String filtrarPlayers(
            @RequestParam(value = "genero", required = false) String genero,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model) {
        User user = UserData.getUser();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if ((genero == null || genero.isBlank()) && (position == null || position.isBlank())) {
            players = ser.getAll();
            populatePlayersModel(model, players, page);
            return resolvePlayerListView(user, isAjax);
        }

        if ((position != null && position.contains("T")) || (genero != null && genero.contains("T"))) {
            players = ser.getAll();
        } else if (genero != null && !genero.isBlank()) {
            players = ser.getIdByGender(genero.charAt(0));
        } else if (position != null && !position.isBlank()) {
            players = ser.getIdByPosition(position);
        }

        if (players.isEmpty()) {
            model.addAttribute("mensaje", "No hay jugadores asociados.");
            model.addAttribute("tipo", "error");
            return resolvePlayerListView(user, isAjax);
        }

        populatePlayersModel(model, players, page);
        model.addAttribute("genero", genero);
        model.addAttribute("position", position);
        return resolvePlayerListView(user, isAjax);
    }

    private List<Player> getPagedPlayers(List<Player> allPlayers, int page) {
        int fromIndex = (page - 1) * PLAYERS_PER_PAGE;
        int toIndex = Math.min(fromIndex + PLAYERS_PER_PAGE, allPlayers.size());

        if (fromIndex >= allPlayers.size()) {
            return new ArrayList<>();
        }

        return allPlayers.subList(fromIndex, toIndex);
    }

    @GetMapping("/admin/edit")
    public String edit(@RequestParam("id") Integer id,
            HttpServletRequest request,
            Model model) {
        Player player = ser.getById(id);
        model.addAttribute("p", player);
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "player/editar_jugador :: contenido";
        }
        return "player/editar_jugador";
    }

    @PostMapping("/admin/delete")
    public String delete(@RequestParam("id") int id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model) {

        ser.delete(id);
        model.addAttribute("mensaje", "Jugador eliminado exitosamente.");
        model.addAttribute("tipo", "success");

        players = ser.getAll();
        populatePlayersModel(model, players, page);

        User user = UserData.getUser();
        return resolvePlayerListView(user, "XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
    }

    @PostMapping("/admin/update")
    public String editPlayer(
            @RequestParam("id") Integer id,
            @RequestParam("idCard") String idCard,
            @RequestParam("name") String name,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") Integer age,
            @RequestParam("position") String position,
            @RequestParam(value = "mail", required = false) String mail,
            @RequestParam("gender") char gender,
            @RequestParam(value = "goal", required = false) Integer goal,
            @RequestParam(value = "assist", required = false) Integer assist,
            @RequestParam("admonished") String admonished,
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request,
            Model model
    ) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        User user = UserData.getUser();
        String mensaje = LogicPlayer.validateRegister("-", name, lastName, age, position, mail, gender, goal, assist, admonished, ser);
        if (mensaje.length() > 0) {
            model.addAttribute("mensaje", mensaje);
            model.addAttribute("tipo", "Alerta");
            Player player = new Player(id, 0, position, goal == null ? 0 : goal, assist == null ? 0 : assist, admonished, idCard, name, lastName, mail == null ? "" : mail, age, gender);
            model.addAttribute("p", player);
            return isAjax ? "player/editar_jugador :: contenido" : "player/editar_jugador";
        }

        Player player = new Player(id, 0, position, goal == null ? 0 : goal, assist == null ? 0 : assist, admonished, idCard, name, lastName, mail == null ? "" : mail, age, gender);
        ser.save(player);
        model.addAttribute("mensaje", "Jugador editado exitosamente.");
        model.addAttribute("tipo", "success");

        players = ser.getAll();
        populatePlayersModel(model, players, page);

        return resolvePlayerListView(user, isAjax);
    }
}
