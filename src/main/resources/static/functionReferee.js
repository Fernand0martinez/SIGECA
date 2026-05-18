function getDashboardContent() {
    return document.getElementById("content") || document.body;
}

function loadIntoDashboard(url) {
    const content = getDashboardContent();
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url, true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            content.innerHTML = this.responseText;
            initRefereeFilter();
        }
    };
    xhttp.send();
}

function submitRefereeForm(form) {
    const xhttp = new XMLHttpRequest();
    xhttp.open(form.method || "POST", form.action, true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.onreadystatechange = function () {
        if (this.readyState !== 4) {
            return;
        }
        if (this.status === 200) {
            getDashboardContent().innerHTML = this.responseText;
            initRefereeFilter();
        } else {
            Swal.fire("Error", "No se pudo procesar el árbitro.", "error");
        }
    };
    xhttp.send(new FormData(form));
}

function initRefereeFilter() {
    const form = document.getElementById("filter-form");
    const genSelect = document.getElementById("gen");
    const availableSelect = document.getElementById("available");
    const tableBody = document.getElementById("referee-table-body");
    const resetBtn = document.getElementById("reset-btn");

    if (!form || !genSelect || !availableSelect || !tableBody || !resetBtn) {
        return;
    }

    function fetchAndUpdateTable(params) {
        fetch("/referees/admin/list" + (params ? "?" + params : ""), {
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
                .then(response => response.text())
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, "text/html");
                    const newTbody = doc.getElementById("referee-table-body");
                    if (newTbody) {
                        tableBody.innerHTML = newTbody.innerHTML;
                    }
                });
    }

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        const params = new URLSearchParams();
        if (genSelect.value) {
            params.append("gen", genSelect.value);
        }
        if (availableSelect.value) {
            params.append("available", availableSelect.value);
        }
        fetchAndUpdateTable(params.toString());
    });

    resetBtn.addEventListener("click", function () {
        genSelect.value = "";
        availableSelect.value = "";
        fetchAndUpdateTable("");
    });
}

document.addEventListener("click", function (e) {
    const addLink = e.target.closest(".add-referee-link");
    if (addLink) {
        e.preventDefault();
        loadIntoDashboard(addLink.getAttribute("href"));
        return;
    }

    const editLink = e.target.closest(".btn-edit-referee");
    if (editLink) {
        e.preventDefault();
        loadIntoDashboard(editLink.getAttribute("href"));
        return;
    }

    const backLink = e.target.closest(".back-referee-list");
    if (backLink) {
        e.preventDefault();
        if (window.navegar) {
            window.navegar("arbitros");
        } else {
            loadIntoDashboard("/referees/admin/list");
        }
        return;
    }

    const deleteButton = e.target.closest(".referee-delete-form button");
    if (deleteButton) {
        e.preventDefault();
        const form = deleteButton.closest(".referee-delete-form");
        Swal.fire({
            title: "¿Eliminar árbitro?",
            icon: "warning",
            showCancelButton: true
        }).then((result) => {
            if (!result.isConfirmed) {
                return;
            }
            const xhttp = new XMLHttpRequest();
            xhttp.open(form.method || "POST", form.action, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.onreadystatechange = function () {
                if (this.readyState !== 4) {
                    return;
                }
                if (this.status === 200) {
                    getDashboardContent().innerHTML = this.responseText;
                    initRefereeFilter();
                }
            };
            xhttp.send(new FormData(form));
        });
    }
});

document.addEventListener("submit", function (e) {
    const form = e.target.closest(".referee-form");
    if (!form) {
        return;
    }
    e.preventDefault();
    submitRefereeForm(form);
});

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initRefereeFilter);
} else {
    initRefereeFilter();
}
