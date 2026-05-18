function initDetailsReservation() {
    document.addEventListener("click", function (e) {
        const detailButton = e.target.closest(".btn-ver-detalle");
        if (!detailButton) {
            return;
        }
        cargarDetalleReservacion(detailButton.getAttribute("data-codigo"));
    });

    function cargarDetalleReservacion(codigo) {
        const content = document.getElementById("content");
        if (!content) {
            return;
        }

        content.innerHTML = "<p style='color: gray;'>Cargando detalles...</p>";

        const xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/reservations/user/details/" + encodeURIComponent(codigo), true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                content.innerHTML = this.responseText;
                return;
            }
            content.innerHTML = "<p style='color: red;'>Error al cargar los detalles de la reservacion.</p>";
        };
        xhttp.send();
    }
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initDetailsReservation);
} else {
    initDetailsReservation();
}
