function initUpdateReservation() {
    function ensureReservationAssets() {
        if (!document.querySelector('link[href="/css/reservation-form.css"]')) {
            const formCss = document.createElement("link");
            formCss.rel = "stylesheet";
            formCss.href = "/css/reservation-form.css";
            document.head.appendChild(formCss);
        }

        if (!document.querySelector('link[href="/css/back-button.css"]')) {
            const backCss = document.createElement("link");
            backCss.rel = "stylesheet";
            backCss.href = "/css/back-button.css";
            document.head.appendChild(backCss);
        }
    }

    document.addEventListener("click", function (e) {
        const addLink = e.target.closest(".btn-agregar-nuevo");
        if (addLink && addLink.getAttribute("href")) {
            e.preventDefault();
            ensureReservationAssets();
            const content = document.getElementById("content");
            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", addLink.getAttribute("href"), true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState === 4 && this.status === 200 && content) {
                    content.innerHTML = this.responseText;
                }
            };
            xhttp.send();
            return;
        }

        const editButton = e.target.closest(".btn-editar");
        if (!editButton) {
            return;
        }

        e.preventDefault();
        ensureReservationAssets();
        const id = editButton.getAttribute("data-id");
        const content = document.getElementById("content");
        const xhttp = new XMLHttpRequest();
        xhttp.open("GET", "/reservations/user/edit/" + encodeURIComponent(id), true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200 && content) {
                content.innerHTML = this.responseText;
                return;
            }
            if (this.status !== 200) {
                Swal.fire("Error", "No se pudo cargar la vista de edicion.", "error");
            }
        };
        xhttp.send();
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initUpdateReservation);
} else {
    initUpdateReservation();
}
