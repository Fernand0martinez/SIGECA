document.addEventListener("click", function (e) {
    const detailBtn = e.target.closest(".detail");
    if (!detailBtn) {
        return;
    }
    detalles(detailBtn.getAttribute("data-id"));
});

function detalles(id) {
    if (!id) {
        const input = document.getElementById("input-detalles");
        id = input ? input.value : "";
    }

    const contenedor = document.getElementById("contenedor-detalles");
    if (!contenedor) {
        return;
    }
    if (!id) {
        Swal.fire("Advertencia", "No hay un id válido.", "warning");
        return;
    }

    contenedor.innerHTML = "Procesando datos...";

    const xml = new XMLHttpRequest();
    xml.open("GET", "/players/admin/details?id=" + encodeURIComponent(id), true);
    xml.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xml.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            contenedor.innerHTML = this.responseText;
            const closeButton = document.createElement("button");
            closeButton.textContent = "Cerrar detalles";
            closeButton.className = "close-details-btn";
            closeButton.style.marginTop = "10px";
            closeButton.style.padding = "5px 10px";
            closeButton.onclick = function () {
                contenedor.innerHTML = "";
            };
            contenedor.appendChild(closeButton);
            contenedor.scrollIntoView({behavior: "smooth"});
        } else {
            Swal.fire("Error", "No se pudieron cargar los detalles.", "error");
        }
    };
    xml.send();
}
