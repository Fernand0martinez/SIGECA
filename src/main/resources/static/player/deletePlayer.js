function getPlayerContentHost() {
    return document.getElementById("content") || document.querySelector(".player-list-container") || document.body;
}

function renderPlayerResponse(html) {
    getPlayerContentHost().innerHTML = html;
}

document.addEventListener("click", function (e) {
    if (e.target && e.target.classList.contains("delete")) {
        const id = e.target.getAttribute("data-id");
        const page = e.target.getAttribute("data-page") || 1;
        deletePlayer(id, page);
    }
});

function deletePlayer(id, page) {
    Swal.fire({
        title: "¿Eliminar jugador?",
        text: "No podrás deshacer esta acción.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (!result.isConfirmed) {
            return;
        }

        const xml = new XMLHttpRequest();
        xml.open("POST", "/players/admin/delete", true);
        xml.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xml.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xml.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                renderPlayerResponse(this.responseText);
                Swal.fire("Éxito", "Jugador eliminado correctamente", "success");
            } else {
                Swal.fire("Error", "Error al eliminar al jugador", "error");
            }
        };
        xml.send("id=" + encodeURIComponent(id) + "&page=" + encodeURIComponent(page));
    });
}
