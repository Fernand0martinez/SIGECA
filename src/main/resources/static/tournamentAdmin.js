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
            icon: "warning",
            showCancelButton: true
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
                }
            };
            xhttp.send(new FormData(form));
        });
    }
});

document.addEventListener("submit", function (e) {
    const form = e.target.closest(".tournament-form");
    if (!form) {
        return;
    }
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
        } else {
            Swal.fire("Error", "No se pudo guardar el torneo.", "error");
        }
    };
    xhttp.send(new FormData(form));
});
