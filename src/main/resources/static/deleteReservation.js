function initDeleteReservation() {
    document.addEventListener("click", function (e) {
        const deleteButton = e.target.closest(".btn-eliminar");
        if (!deleteButton) {
            return;
        }

        const id = deleteButton.getAttribute("data-id");
        Swal.fire({
            title: "Eliminar reservacion?",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Si, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (!result.isConfirmed) {
                return;
            }

            const xhr = new XMLHttpRequest();
            xhr.open("POST", "/reservations/user/delete", true);
            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhr.onload = function () {
                if (xhr.status === 200) {
                    const content = document.getElementById("content");
                    if (content) {
                        content.innerHTML = xhr.responseText;
                    } else {
                        location.reload();
                    }
                    return;
                }
                Swal.fire("Error", "No se pudo eliminar la reservacion.", "error");
            };
            xhr.send("id=" + encodeURIComponent(id));
        });
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initDeleteReservation);
} else {
    initDeleteReservation();
}
