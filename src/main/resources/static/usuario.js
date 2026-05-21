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
        if (document.querySelector(`script[src="${src}"]`))
            return resolve();
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

    async function cargarContenido(url, mensajeCarga) {
        content.innerHTML = `<p>${mensajeCarga}</p>`;
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

    async function verCanchas() {
        await cargarContenido("/courts/client/list", "Cargando canchas...");
        inyectarCSS("/css/styles.css");
        await inyectarScript("/js/functionCourt.js");
        if (typeof initFilters === "function") initFilters();
        if (typeof initMasks === "function") initMasks();
        if (typeof initDeleteHandler === "function") initDeleteHandler();
        if (typeof initNewCourtButton === "function") initNewCourtButton();
    }

    async function verTorneos() {
        await cargarContenido("/tournaments/user/registration", "Cargando torneos...");
        inyectarCSS("/css/tournament.css");
    }

    async function gestionarEquipo() {
        await cargarContenido("/teams/user/list", "Cargando equipos...");
        inyectarCSS("/css/styles.css");
        inyectarCSS("/css/team-user.css");
        inyectarCSS("/css/back-button.css");
        await inyectarScript("/js/functionTeamUser.js");
        if (typeof initNewTeamButton === "function") initNewTeamButton();
        if (typeof initDeleteHandlerUser === "function") initDeleteHandlerUser();
        if (typeof initTeamFilters === "function") initTeamFilters();
    }

    async function verProducto() {
        await cargarContenido("/products/user/list", "Cargando productos...");
        inyectarCSS("/css/products.css");
        await inyectarScript("/functionProduct.js");
        if (typeof iniciarEventosProductosCliente === "function") {
            iniciarEventosProductosCliente();
        }
    }

    async function verPerfil() {
        await cargarContenido("/users/profile", "Cargando perfil...");
    }

    async function verReservaciones() {
        await cargarContenido("/reservations/user/list", "Cargando reservaciones...");
        inyectarCSS("/StyleList_reservation.css");
        inyectarCSS("/css/reservation-form.css");
        inyectarCSS("/css/back-button.css");
        await inyectarScript("/detailsReservation.js");
        await inyectarScript("/deleteReservation.js");
        await inyectarScript("/updateReservation.js");
    }

    async function verPagos() {
        await cargarContenido("/payments/user/filter", "Cargando pagos...");
    }

    async function verJugadores() {
        await cargarContenido("/players/filter?position=T", "Cargando jugadores...");
        inyectarCSS("/StylesPlayer.css");
        await inyectarScript("/player/deletePlayer.js");
        await inyectarScript("/player/editPlayer.js");
        await inyectarScript("/player/detallesJugador.js");
    }

    const routes = {
        canchas: verCanchas,
        torneos: verTorneos,
        equipo: gestionarEquipo,
        producto: verProducto,
        perfil: verPerfil,
        reservaciones: verReservaciones,
        pago: verPagos,
        jugadores: verJugadores
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
    window.navegar = navegar;
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

    function doPayment(id) {
        Swal.fire({
            title: "¿Realizar pago?",
            text: "No podras deshacer esta accion.",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Si, pagar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) return;
            inyectarCSS("/StylesPayment.css");
            inyectarCSS("/css/back-button.css");
            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", `/payments/user/pay-form?id=${id}`, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    content.innerHTML = this.responseText;
                } else {
                    Swal.fire("Error", "No se pudo cargar el pago.", "error");
                }
            };
            xhttp.send();
        });
    }

    function invoice(id) {
        Swal.fire({
            title: "¿Ver factura?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Si, ver",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) return;
            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", `/payments/user/details?id=${id}`, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    content.innerHTML = this.responseText;
                } else {
                    Swal.fire("Error", "No se pudieron cargar los detalles.", "error");
                }
            };
            xhttp.send();
        });
    }

    function descargarFactura(downloadLink) {
        const href = downloadLink.getAttribute("href");
        if (!href) {
            Swal.fire("Error", "No se encontro la ruta del PDF.", "error");
            return;
        }
        window.open(href, "_blank");
    }

    function procesarPago(paymentForm) {
        const methodField = paymentForm.querySelector("[name='method']");
        const httpMethod = paymentForm.getAttribute("method") || "POST";
        if (methodField && !methodField.value) {
            Swal.fire("Validacion", "Seleccione un metodo de pago.", "warning");
            methodField.focus();
            return;
        }

        const submitButton = paymentForm.querySelector(".payment-confirm-btn, button[type='submit']");
        if (submitButton) {
            submitButton.disabled = true;
        }

        const xhttp = new XMLHttpRequest();
        xhttp.open(httpMethod, paymentForm.action, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) return;
            if (submitButton) {
                submitButton.disabled = false;
            }
            if (this.status === 200) {
                content.innerHTML = this.responseText;
                inyectarCSS("/StyleList_reservation.css");
            } else {
                Swal.fire("Error", "No se pudo procesar el pago.", "error");
            }
        };
        xhttp.send(new FormData(paymentForm));
    }

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
                    Swal.fire('¡Enviado!', 'La factura ha sido enviada a su correo.', 'success');
                } else {
                    Swal.fire('Error', 'No se pudo enviar el correo. Verifique la configuración del servidor.', 'error');
                }
            }
        };
        xhttp.send();
    };

    document.addEventListener("click", async function (e) {
        const btn = e.target.closest(".nav-button");
        if (btn) {
            await navegar(btn.dataset.view);
            return;
        }
        const paidButton = e.target.closest(".paid");
        if (paidButton) {
            doPayment(paidButton.getAttribute("data-id"));
            return;
        }
        const invoiceButton = e.target.closest(".invoice");
        if (invoiceButton) {
            invoice(invoiceButton.getAttribute("data-id"));
            return;
        }
        const downloadButton = e.target.closest(".download-btn");
        if (downloadButton) {
            e.preventDefault();
            descargarFactura(downloadButton);
            return;
        }
        const emailButton = e.target.closest(".email-btn");
        if (emailButton) {
            e.preventDefault();
            window.sendInvoiceEmail(emailButton.getAttribute("data-id"));
            return;
        }
        const paymentSubmitButton = e.target.closest(".payment-confirm-btn");
        if (paymentSubmitButton) {
            e.preventDefault();
            const paymentForm = paymentSubmitButton.closest(".payment-process-form");
            if (paymentForm) {
                procesarPago(paymentForm);
            }
            return;
        }
        const paymentFilterForm = e.target.closest(".payment-filter-form");
        if (paymentFilterForm) {
            return;
        }
        const dashboardHome = e.target.closest(".dashboard-home-btn, .dashboard-home-link");
        if (dashboardHome) {
            e.preventDefault();
            const defaultView = document.querySelector(".container")?.dataset.defaultView || "canchas";
            navegar(defaultView);
        }
    });

    document.addEventListener("submit", function (e) {
        const tournamentRegistrationForm = e.target.closest("form[action='/tournaments/user/register']");
        if (tournamentRegistrationForm) {
            e.preventDefault();
            const xhttp = new XMLHttpRequest();
            xhttp.open("POST", tournamentRegistrationForm.action, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    content.innerHTML = this.responseText;
                    inyectarCSS("/css/tournament.css");
                } else {
                    Swal.fire("Error", "No se pudo completar la inscripción al torneo.", "error");
                }
            };
            xhttp.send(new FormData(tournamentRegistrationForm));
            return;
        }

        const reservationForm = e.target.closest("form[action='/reservations/user/save'], form[action='/reservations/user/update']");
        if (reservationForm) {
            e.preventDefault();
            const httpMethod = reservationForm.getAttribute("method") || "POST";
            const xhttp = new XMLHttpRequest();
            xhttp.open(httpMethod, reservationForm.action, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) return;
                if (this.status === 200) {
                    content.innerHTML = this.responseText;
                } else {
                    Swal.fire("Error", "No se pudo guardar la reservación.", "error");
                }
            };
            xhttp.send(new FormData(reservationForm));
            return;
        }

        const form = e.target.closest(".payment-filter-form");
        if (form) {
            e.preventDefault();
            const params = new URLSearchParams(new FormData(form)).toString();
            cargarContenido(form.action + "?" + params, "Cargando pagos...").catch(console.error);
            return;
        }

        const paymentForm = e.target.closest(".payment-process-form");
        if (paymentForm) {
            e.preventDefault();
            procesarPago(paymentForm);
            return;
        }
    });

    document.addEventListener("click", function (e) {
        const paymentPageLink = e.target.closest(".payment .pagination a");
        if (!paymentPageLink) {
            return;
        }
        e.preventDefault();
        cargarContenido(paymentPageLink.getAttribute("href"), "Cargando pagos...").catch(console.error);
    });

    const params = new URLSearchParams(window.location.search);
    const defaultView = document.querySelector(".container")?.dataset.defaultView || "canchas";
    navegar(params.get("cargarVista") || defaultView).catch(console.error);
});
