function getPlayerContentHost() {
    return document.getElementById("content") || document.querySelector(".player-list-container") || document.body;
}

function ensurePlayerFormAssets() {
    if (!document.querySelector('link[href="/css/player-form.css"]')) {
        const formCss = document.createElement("link");
        formCss.rel = "stylesheet";
        formCss.href = "/css/player-form.css";
        document.head.appendChild(formCss);
    }

    if (!document.querySelector('link[href="/css/back-button.css"]')) {
        const backCss = document.createElement("link");
        backCss.rel = "stylesheet";
        backCss.href = "/css/back-button.css";
        document.head.appendChild(backCss);
    }
}

function loadPlayerView(url) {
    ensurePlayerFormAssets();
    const xml = new XMLHttpRequest();
    xml.open("GET", url, true);
    xml.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xml.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            getPlayerContentHost().innerHTML = this.responseText;
        } else {
            Swal.fire("Error", "No se pudo cargar la vista del jugador.", "error");
        }
    };
    xml.send();
}

function submitPlayerForm(form) {
    const xml = new XMLHttpRequest();
    xml.open(form.method || "POST", form.action, true);
    xml.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xml.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            getPlayerContentHost().innerHTML = this.responseText;
        } else {
            Swal.fire("Error", "No se pudo guardar la información del jugador.", "error");
        }
    };
    xml.send(new FormData(form));
}

document.addEventListener("click", function (e) {
    const editBtn = e.target.closest(".edit");
    if (editBtn) {
        const id = editBtn.getAttribute("data-id");
        Swal.fire({
            title: "¿Editar jugador?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Sí, editar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                loadPlayerView("/players/admin/edit?id=" + encodeURIComponent(id));
            }
        });
        return;
    }

    if (e.target.closest(".create-player-btn")) {
        loadPlayerView("/players/admin/create");
        return;
    }

    if (e.target.closest(".player-list-btn")) {
        if (window.navegar) {
            window.navegar("jugadores");
        } else {
            loadPlayerView("/players/filter");
        }
        return;
    }

    if (e.target.closest(".player-dashboard-home") && window.navegar) {
        window.navegar("jugadores");
    }
});

document.addEventListener("submit", function (e) {
    const playerFilterForm = e.target.closest("form[action='/players/filter']");
    if (playerFilterForm) {
        e.preventDefault();
        const params = new URLSearchParams(new FormData(playerFilterForm)).toString();
        loadPlayerView("/players/filter" + (params ? "?" + params : ""));
        return;
    }

    const form = e.target.closest(".player-form");
    if (!form) {
        return;
    }
    e.preventDefault();
    submitPlayerForm(form);
});

document.addEventListener("click", function (e) {
    const pageLink = e.target.closest(".player-list-container .pagination a");
    if (!pageLink) {
        return;
    }
    e.preventDefault();
    loadPlayerView(pageLink.getAttribute("href"));
});
