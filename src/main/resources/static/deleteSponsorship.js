function initDeleteSponsorship() {
    document.addEventListener("click", function (e) {
        const button = e.target.closest(".btn-eliminar");
        if (!button) {
            return;
        }
        const id = button.getAttribute("data-id");
        Swal.fire({
            title: "¿Seguro de eliminar?",
            icon: "warning",
            showCancelButton: true
        }).then((result) => {
            if (!result.isConfirmed) {
                return;
            }
            const xhr = new XMLHttpRequest();
            xhr.open("POST", "/sponsorships/admin/delete", true);
            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhr.onload = function () {
                if (xhr.status === 200) {
                    const content = document.getElementById("content");
                    if (content) {
                        content.innerHTML = xhr.responseText;
                    }
                }
            };
            xhr.send("id=" + encodeURIComponent(id));
        });
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initDeleteSponsorship);
} else {
    initDeleteSponsorship();
}
