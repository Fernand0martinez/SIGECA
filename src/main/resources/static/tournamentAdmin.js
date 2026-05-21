function getTournamentContent() {
    return document.getElementById("content") || document.body;
}

function tournamentResponseHasError(html) {
    return typeof html === "string" && html.includes("alert-danger");
}

function switchTournamentTab(tabId, clickedButton) {
    const contentRoot = getTournamentContent();
    const tabContents = contentRoot.querySelectorAll(".tab-content");
    const tabButtons = contentRoot.querySelectorAll(".tab-btn");

    tabContents.forEach((tabContent) => tabContent.classList.remove("active"));
    tabButtons.forEach((tabButton) => tabButton.classList.remove("active"));

    const targetTab = contentRoot.querySelector(`#${tabId}`);
    if (targetTab) {
        targetTab.classList.add("active");
    }
    if (clickedButton) {
        clickedButton.classList.add("active");
    }
}

function filterTournamentRound(roundClass) {
    const contentRoot = getTournamentContent();
    const cards = contentRoot.querySelectorAll(".match-card");
    cards.forEach((card) => {
        card.style.display = roundClass === "all" || card.classList.contains(roundClass) ? "block" : "none";
    });
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
    const teamDetailsButton = e.target.closest(".btn-team-details");
    if (teamDetailsButton) {
        e.preventDefault();
        const targetId = teamDetailsButton.dataset.teamTarget;
        const detailsPanel = targetId ? document.getElementById(targetId) : null;
        if (detailsPanel) {
            const isActive = detailsPanel.classList.toggle("active");
            teamDetailsButton.textContent = isActive ? "Ocultar detalles" : "Ver detalles";
        }
        return;
    }

    const tabButton = e.target.closest(".tab-btn[data-tab-target]");
    if (tabButton) {
        e.preventDefault();
        switchTournamentTab(tabButton.dataset.tabTarget, tabButton);
        return;
    }

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
            if (document.querySelector('.nav-button[data-view="torneos"]')) {
                window.navegar("torneos");
            } else {
                window.navegar("torneo");
            }
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
                    if (tournamentResponseHasError(this.responseText)) {
                        Swal.fire("Error", "No se pudo eliminar el torneo.", "error");
                    } else {
                        Swal.fire("Eliminado", "El torneo ha sido eliminado.", "success");
                    }
                }
            };
            xhttp.send(new FormData(form));
        });
        return;
    }

    const removeTeamButton = e.target.closest(".remove-team-btn");
    if (removeTeamButton) {
        e.preventDefault();
        const form = removeTeamButton.closest(".remove-team-form");
        const xhttp = new XMLHttpRequest();
        xhttp.open("POST", form.action, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                getTournamentContent().innerHTML = this.responseText;
                if (tournamentResponseHasError(this.responseText)) {
                    Swal.fire("Error", "No se pudo eliminar el equipo del torneo.", "error");
                } else {
                    Swal.fire("Removido", "El equipo ha sido eliminado del torneo.", "success");
                }
            }
        };
        xhttp.send(new FormData(form));
        return;
    }
});

document.addEventListener("change", function (e) {
    const roundSelect = e.target.closest("#roundSelect");
    if (!roundSelect) {
        return;
    }
    filterTournamentRound(roundSelect.value);
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
                    if (tournamentResponseHasError(this.responseText)) {
                        Swal.fire("Error", "No se pudo inscribir el equipo en el torneo.", "error");
                    } else {
                        Swal.fire("Éxito", "Equipo inscrito en el torneo.", "success");
                    }
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
            confirmButtonText: "Sí, lanzar campeonato",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) {
                return;
            }

            const xhttp = new XMLHttpRequest();
            xhttp.open("POST", launchForm.action, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) {
                    return;
                }
                if (this.status === 200) {
                    getTournamentContent().innerHTML = this.responseText;
                    if (tournamentResponseHasError(this.responseText)) {
                        Swal.fire("Error", "No se pudo lanzar el torneo.", "error");
                    } else {
                        Swal.fire("Lanzado", "El torneo está en curso y los partidos se han calendarizado.", "success");
                    }
                } else {
                    Swal.fire("Error", "No se pudo lanzar el torneo.", "error");
                }
            };
            xhttp.send(new FormData(launchForm));
        });
    }
});
