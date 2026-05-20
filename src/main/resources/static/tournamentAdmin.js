function getTournamentContent() {
    return document.getElementById("content") || document.body;
}

function loadTournamentView(url) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url, true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            getTournamentContent().innerHTML = this.responseText;
        }
    };
    xhttp.send();
}

document.addEventListener("click", function (e) {
    const addLink = e.target.closest(".add-tournament-link");
    if (addLink) {
        e.preventDefault();
        loadTournamentView(addLink.getAttribute("href"));
        return;
    }

    const detailsLink = e.target.closest(".details-tournament-link");
    if (detailsLink) {
        e.preventDefault();
        loadTournamentView(detailsLink.getAttribute("href"));
        return;
    }

    const backLink = e.target.closest(".back-tournament-list");
    if (backLink) {
        e.preventDefault();
        if (window.navegar) {
            window.navegar("torneo");
        } else {
            loadTournamentView("/tournaments/admin/list");
        }
        return;
    }

    const deleteButton = e.target.closest(".tournament-delete-form button");
    if (deleteButton) {
        e.preventDefault();
        const form = deleteButton.closest(".tournament-delete-form");
        Swal.fire({
            title: "¿Estás seguro de eliminar este torneo?",
            text: "Esta acción no se puede deshacer.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) {
                return;
            }
            const xhttp = new XMLHttpRequest();
            xhttp.open(form.method || "POST", form.action, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) {
                    return;
                }
                if (this.status === 200) {
                    getTournamentContent().innerHTML = this.responseText;
                    Swal.fire("Eliminado", "El torneo ha sido eliminado.", "success");
                }
            };
            xhttp.send(new FormData(form));
        });
        return;
    }
    
    // Botón para remover equipo en details
    const removeTeamButton = e.target.closest(".remove-team-btn");
    if (removeTeamButton) {
        e.preventDefault();
        const form = removeTeamButton.closest(".remove-team-form");
        const xhttp = new XMLHttpRequest();
        xhttp.open("POST", form.action, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) return;
            if (this.status === 200) {
                getTournamentContent().innerHTML = this.responseText;
                Swal.fire("Removido", "El equipo ha sido eliminado del torneo.", "success");
            }
        };
        xhttp.send(new FormData(form));
        return;
    }
});

document.addEventListener("submit", function (e) {
    const form = e.target.closest(".tournament-form, .tournament-details-form");
    if (form) {
        e.preventDefault();
        const xhttp = new XMLHttpRequest();
        xhttp.open(form.method || "POST", form.action, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                getTournamentContent().innerHTML = this.responseText;
                if (form.classList.contains("tournament-details-form")) {
                    Swal.fire("Éxito", "Equipo inscrito en el torneo.", "success");
                }
            } else {
                Swal.fire("Error", "No se pudo realizar la acción.", "error");
            }
        };
        xhttp.send(new FormData(form));
        return;
    }

    const launchForm = e.target.closest(".tournament-launch-form");
    if (launchForm) {
        e.preventDefault();
        Swal.fire({
            title: "¿Estás seguro de lanzar el torneo?",
            text: "Se generará el calendario automático y no se podrán modificar los equipos inscritos.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#28a745",
            cancelButtonColor: "#d33",
            confirmButtonText: "¡Sí, lanzar campeonato!",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                const xhttp = new XMLHttpRequest();
                xhttp.open("POST", launchForm.action, true);
                xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhttp.onreadystatechange = function () {
                    if (this.readyState !== 4) return;
                    if (this.status === 200) {
                        getTournamentContent().innerHTML = this.responseText;
                        Swal.fire("¡Lanzado!", "El torneo está en curso y los partidos se han calendarizado.", "success");
                    } else {
                        Swal.fire("Error", "No se pudo lanzar el torneo.", "error");
                    }
                };
                xhttp.send(new FormData(launchForm));
            }
        });
    }
});
