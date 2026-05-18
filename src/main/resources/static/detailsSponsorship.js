function initDetailsSponsorship() {
    document.addEventListener("click", function (e) {
        const button = e.target.closest(".btn-ver-detalle");
        if (!button) {
            return;
        }
        const codigo = button.getAttribute("data-codigo");
        const contenedor = document.getElementById("detalle-patrocinio");
        if (!contenedor) {
            return;
        }
        contenedor.innerHTML = "<p style='color: gray;'>Cargando detalles...</p>";
        const xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/sponsorships/admin/details/" + codigo, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                contenedor.innerHTML = this.responseText;
            } else {
                contenedor.innerHTML = "<p style='color: red;'>Error al cargar los detalles del patrocinio.</p>";
            }
        };
        xhttp.send();
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initDetailsSponsorship);
} else {
    initDetailsSponsorship();
}
