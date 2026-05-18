document.addEventListener("DOMContentLoaded", function () {
    document.addEventListener("click", function (e) {
        if (e.target && e.target.classList.contains("btn-ver-detalle")) {
            let codigo = e.target.getAttribute("data-codigo");
            cargarDetallePatrocinio(codigo);
        }
    });

    function cargarDetallePatrocinio(codigo) {
        console.log("Cargando detalles del producto:", codigo);

        var contenedor = document.getElementById("detalle-patrocinio");
        contenedor.innerHTML = "<p style='color: gray;'>Cargando detalles...</p>";

        var xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/sponsorships/admin/details/" + codigo, true);

        xhttp.send();

        xhttp.onreadystatechange = function () {
            if (this.readyState === 4 && this.status === 200) {
                contenedor.innerHTML = this.responseText;
            } else if (this.readyState === 4 && this.status !== 200) {
                contenedor.innerHTML = "<p style='color: red;'>Error al cargar los detalles del patrocinio.</p>";
            }
        };
    }
});


