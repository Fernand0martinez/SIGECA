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
            resolve();
            return;
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
    const routes = {
        usuario: async () => {
            await cargarContenido("/users/list", "Cargando usuarios...");
            if (typeof iniciarEventosUsuarios === "function") {
                iniciarEventosUsuarios();
            }
        },
        producto: async () => {
            await cargarContenido("/products/admin/list", "Cargando productos...");
            inyectarCSS("/css/products.css");
            await inyectarScript("/functionProduct.js");
            if (typeof iniciarEventosProductos === "function") {
                iniciarEventosProductos();
            }
        },
        cancha: async () => {
            await cargarContenido("/courts/client/list", "Cargando canchas...");
            inyectarCSS("/css/styles.css");
            await inyectarScript("/js/functionCourt.js");
            if (typeof initMasks === "function") initMasks();
            if (typeof initFilters === "function") initFilters();
            if (typeof initDeleteHandler === "function") initDeleteHandler();
            if (typeof initNewCourtButton === "function") initNewCourtButton();
        },
        perfil: () => cargarContenido("/supervisors/profile", "Cargando perfil...")
    };

    function activarBoton(view) {
        buttons.forEach((btn) => {
            btn.classList.toggle("active", btn.dataset.view === view);
        });
    }

    async function navegar(view) {
        if (!routes[view]) {
            return;
        }
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

    document.addEventListener("click", async function (e) {
        const btn = e.target.closest(".nav-button");
        if (!btn) return;
        await navegar(btn.dataset.view);
    });

    const params = new URLSearchParams(window.location.search);
    const defaultView = document.querySelector(".container")?.dataset.defaultView || "usuario";
    navegar(params.get("cargarVista") || defaultView).catch(console.error);
});
