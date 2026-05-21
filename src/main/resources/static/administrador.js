function inyectarCSS(href) {
    if (!document.querySelector(`link[href="${href}"]`)) {
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = href;
        document.head.appendChild(link);
    }
}

function inyectarScript(src) {
    return new Promise((resolve, reject) => {
        if (document.querySelector(`script[src="${src}"]`)) {
            return resolve();
        }
        const s = document.createElement("script");
        s.src = src;
        s.onload = resolve;
        s.onerror = reject;
        document.body.appendChild(s);
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const content = document.getElementById("content");
    const buttons = document.querySelectorAll(".nav-button");

    function cargarContenido(url, mensajeCarga) {
        content.innerHTML = `<div class="loading-spinner"><p>${mensajeCarga}</p></div>`;
        return new Promise((resolve, reject) => {
            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    content.innerHTML = this.responseText;
                    resolve();
                } else {
                    content.innerHTML = `<div class="error-message"><p>Error al cargar contenido (${this.status})</p></div>`;
                    reject(new Error(`HTTP ${this.status}`));
                }
            };
            xhttp.send();
        });
    }

    const routes = {
        cancha: async () => {
            await cargarContenido("/courts/admin/list", "Cargando canchas...");
            inyectarCSS("/css/styles.css");
            await inyectarScript("/js/functionCourt.js");
            if (typeof initDeleteHandler === "function") initDeleteHandler();
            if (typeof initMasks === "function") initMasks();
            if (typeof initNewCourtButton === "function") initNewCourtButton();
            if (typeof initFilters === "function") initFilters();
        },
        torneo: async () => {
            await cargarContenido("/tournaments/admin/list", "Cargando torneos...");
            inyectarCSS("/css/tournament.css");
            inyectarCSS("/css/back-button.css");
            await inyectarScript("/tournamentAdmin.js");
        },
        reservaciones: async () => {
            await cargarContenido("/reservations/admin/list", "Cargando reservaciones...");
            await inyectarScript("/detailsReservation.js");
        },
        productos: async () => {
            await cargarContenido("/products/admin/list", "Cargando productos...");
            inyectarCSS("/css/products.css");
            await inyectarScript("/functionProduct.js");
            if (typeof iniciarEventosProductos === "function") iniciarEventosProductos();
        },
        arbitros: async () => {
            await cargarContenido("/referees/admin/list", "Cargando arbitros...");
            await inyectarScript("/functionReferee.js");
            if (typeof initRefereeFilter === "function") initRefereeFilter();
        },
        jugador: async () => {
            await cargarContenido("/players/admin/list", "Cargando jugadores...");
            inyectarCSS("/jugador/styles.css");
            await inyectarScript("/player/deletePlayer.js");
            await inyectarScript("/player/editPlayer.js");
            await inyectarScript("/player/detallesJugador.js");
        },
        perfil: async () => {
            await cargarContenido("/admin/profile", "Cargando perfil...");
        },
        patrocinio: async () => {
            await cargarContenido("/sponsorships/admin/list", "Cargando patrocinios...");
            inyectarCSS("/css/sponsorship.css");
            await inyectarScript("/deleteSponsorship.js");
            await inyectarScript("/detailsSponsorship.js");
            await inyectarScript("/updateSponsorship.js");
        }
    };

    function activarBoton(view) {
        buttons.forEach((btn) => btn.classList.toggle("active", btn.dataset.view === view));
    }

    async function navegar(view) {
        if (!routes[view]) return;
        activarBoton(view);
        const url = new URL(window.location.href);
        url.searchParams.set("cargarVista", view);
        window.history.replaceState({}, "", url);
        await routes[view]();
    }
    window.navegar = navegar; // Exportar globalmente

    window.invoice = function(id) {
        Swal.fire({
            title: "¿Ver factura?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Si, ver",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) return;
            const contenedor = document.getElementById("details");
            if (!contenedor) return;
            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", `/payments/user/details?id=${id}`, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    contenedor.innerHTML = this.responseText;
                } else {
                    Swal.fire("Error", "No se pudieron cargar los detalles.", "error");
                }
            };
            xhttp.send();
        });
    };

    window.sendInvoiceEmail = function(id) {
        Swal.fire({
            title: 'Enviando correo...',
            text: 'Por favor espere mientras procesamos el envío.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        const xhttp = new XMLHttpRequest();
        xhttp.open("GET", `/payments/user/send-email?id=${id}`, true);
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status === 200) {
                    Swal.fire('¡Enviado!', 'La factura ha sido enviada al correo del cliente.', 'success');
                } else {
                    Swal.fire('Error', 'No se pudo enviar el correo. Verifique la configuración del servidor.', 'error');
                }
            }
        };
        xhttp.send();
    };

    window.dashboardBack = function (...views) {
        for (const view of views) {
            if (document.querySelector(`.nav-button[data-view="${view}"]`)) {
                navegar(view).catch(console.error);
                return false;
            }
        }
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = "/login/home";
        }
        return false;
    };

    document.addEventListener("click", async function (e) {
        const btn = e.target.closest(".nav-button");
        if (btn) {
            await navegar(btn.dataset.view);
            return;
        }

        if (e.target.classList.contains("invoice")) {
            invoice(e.target.getAttribute("data-id"));
        }
    });

    const params = new URLSearchParams(window.location.search);
    const defaultView = document.querySelector(".container")?.dataset.defaultView || "cancha";
    navegar(params.get("cargarVista") || defaultView).catch(console.error);
});
