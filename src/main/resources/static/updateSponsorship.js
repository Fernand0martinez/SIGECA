if (!window.updateSponsorshipInitialized) {
    window.updateSponsorshipInitialized = true;

    function renderSponsorshipResponse(html) {
        const content = document.getElementById("content");
        if (content) {
            content.innerHTML = html;
        }

        const errorsNode = document.getElementById("errores-data");
        if (errorsNode) {
            const errors = errorsNode.getAttribute("data-errores");
            if (errors) {
                Swal.fire("Errores de validacion", errors.replace(/\n/g, "<br>"), "error");
            }
        }
    }

    function requestSponsorshipView(url) {
        const xhttp = new XMLHttpRequest();
        xhttp.open("GET", url, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                renderSponsorshipResponse(this.responseText);
            }
        };
        xhttp.send();
    }

    function submitSponsorshipForm(form, successMessage) {
        const xhttp = new XMLHttpRequest();
        xhttp.open(form.method || "POST", form.action, true);
        xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        xhttp.onreadystatechange = function () {
            if (this.readyState !== 4) {
                return;
            }
            if (this.status === 200) {
                renderSponsorshipResponse(this.responseText);
                if (!this.responseText.includes("form-sponsorship-admin") && !this.responseText.includes("sponsorship-update-form")) {
                    Swal.fire({
                        icon: "success",
                        title: successMessage,
                        timer: 1800,
                        showConfirmButton: false
                    });
                }
            } else {
                Swal.fire("Error", "No se pudo guardar el patrocinio.", "error");
            }
        };
        xhttp.send(new FormData(form));
    }

    document.addEventListener("click", function (e) {
        const addLink = e.target.closest(".btn-new-sponsorship");
        if (addLink && addLink.getAttribute("href")) {
            e.preventDefault();
            requestSponsorshipView(addLink.getAttribute("href"));
            return;
        }

        const editButton = e.target.closest(".btn-editar");
        if (editButton) {
            e.preventDefault();
            const id = editButton.getAttribute("data-id");
            requestSponsorshipView("/sponsorships/admin/edit/" + id);
            return;
        }

        const resetLink = e.target.closest(".sponsorship-reset-link");
        if (resetLink) {
            e.preventDefault();
            requestSponsorshipView(resetLink.getAttribute("href"));
            return;
        }

        const backButton = e.target.closest(".sponsorship-back-list, .sponsorship-home-link, .btn-back-list");
        if (backButton) {
            e.preventDefault();
            if (window.navegar) {
                window.navegar("patrocinio");
            } else {
                requestSponsorshipView("/sponsorships/admin/list");
            }
        }
    });

    document.addEventListener("submit", function (e) {
        const filterForm = e.target.closest(".sponsorship-filter-form");
        if (filterForm) {
            e.preventDefault();
            const params = new URLSearchParams(new FormData(filterForm)).toString();
            requestSponsorshipView(filterForm.action + (params ? "?" + params : ""));
            return;
        }

        const createForm = e.target.closest(".sponsorship-create-form, #form-sponsorship-admin");
        if (createForm) {
            e.preventDefault();
            submitSponsorshipForm(createForm, "Patrocinio guardado");
            return;
        }

        const updateForm = e.target.closest(".sponsorship-update-form");
        if (updateForm) {
            e.preventDefault();
            submitSponsorshipForm(updateForm, "Patrocinio actualizado");
        }
    });
}
