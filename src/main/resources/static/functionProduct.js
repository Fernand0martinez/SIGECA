function getProductDetailContainer() {
    return document.getElementById("detalle-producto");
}

function getProductFormContainer() {
    return document.getElementById("contenedor-formulario-producto");
}

function cerrarDetalleProducto() {
    const contenedor = getProductDetailContainer();
    if (!contenedor) {
        return;
    }
    contenedor.innerHTML = "";
    contenedor.classList.remove("active");
}

function cerrarFormularioProducto() {
    const contenedor = getProductFormContainer();
    if (contenedor) {
        contenedor.innerHTML = "";
    }
}

function iniciarEventosProductos() {
    const formFiltros = document.getElementById("form-filtros-productos");
    if (formFiltros && !formFiltros.dataset.bound) {
        formFiltros.dataset.bound = "true";
        formFiltros.addEventListener("submit", function (e) {
            e.preventDefault();

            const formData = new FormData(formFiltros);
            const params = new URLSearchParams(formData).toString();
            const url = "/products/admin/filter-fragment?" + params;

            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.send();

            xhttp.onreadystatechange = function () {
                if (this.readyState === 4 && this.status === 200) {
                    document.getElementById("tbodyProductos").innerHTML = this.responseText;
                }
            };
        });
    }

    const btnRefrescar = document.querySelector(".refresh-button-product");
    if (btnRefrescar && !btnRefrescar.dataset.bound) {
        btnRefrescar.dataset.bound = "true";
        btnRefrescar.addEventListener("click", function (e) {
            e.preventDefault();
            refrescarListaProductos();
        });
    }

    if (!document.body.dataset.productAdminEventsBound) {
        document.body.dataset.productAdminEventsBound = "true";

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-details-product")) {
                const id = e.target.getAttribute("data-id");
                const contenedor = getProductDetailContainer();
                if (!contenedor) {
                    return;
                }
                contenedor.innerHTML = "Cargando...";

                const xhttp = new XMLHttpRequest();
                xhttp.open("GET", "/products/admin/details/" + id, true);
                xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhttp.send();

                xhttp.onreadystatechange = function () {
                    if (this.readyState !== 4) {
                        return;
                    }
                    if (this.status === 200) {
                        contenedor.innerHTML = this.responseText;
                        contenedor.classList.add("active");
                    } else {
                        contenedor.innerHTML = "<p>Error al cargar detalles</p>";
                    }
                };
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-cerrar-detalles")) {
                cerrarDetalleProducto();
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.id === "btn-nuevo-producto") {
                cargarFormularioProducto("/products/admin/form");
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-edit")) {
                e.preventDefault();
                cargarFormularioProducto(e.target.getAttribute("href"));
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-delete-product")) {
                e.preventDefault();
                const url = e.target.getAttribute("href");
                const urlParams = new URLSearchParams(url.split("?")[1]);
                const idProduct = urlParams.get("idProduct");

                Swal.fire({
                    title: "Eliminar producto",
                    text: "Esta accion eliminara el producto permanentemente.",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#d33",
                    cancelButtonColor: "#3085d6",
                    confirmButtonText: "Si, eliminar",
                    cancelButtonText: "Cancelar"
                }).then((result) => {
                    if (!result.isConfirmed || !idProduct) {
                        return;
                    }

                    const xhttp = new XMLHttpRequest();
                    xhttp.open("POST", "/products/admin/delete", true);
                    xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    xhttp.send("idProduct=" + idProduct);

                    xhttp.onreadystatechange = function () {
                        if (this.readyState !== 4) {
                            return;
                        }
                        if (this.status === 200) {
                            document.getElementById("tbodyProductos").innerHTML = this.responseText;
                            cerrarDetalleProducto();
                            cerrarFormularioProducto();
                            Swal.fire({
                                icon: "success",
                                title: "Eliminado",
                                text: "Producto eliminado correctamente",
                                timer: 2000,
                                showConfirmButton: false
                            });
                        } else {
                            Swal.fire("Error", "No se pudo eliminar el producto.", "error");
                        }
                    };
                });
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.id === "cancelar-formulario-producto") {
                cerrarFormularioProducto();
            }
        });
    }
}

function cargarFormularioProducto(url) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url, true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.send();

    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            cerrarDetalleProducto();
            getProductFormContainer().innerHTML = this.responseText;
            activarEnvioFormularioProducto();
        }
    };
}

function activarEnvioFormularioProducto() {
    const form = document.getElementById("form-producto");
    if (!form) {
        return;
    }

    form.onsubmit = function (e) {
        e.preventDefault();

        const formData = new FormData(form);

        fetch("/products/admin/save", {
            method: "POST",
            body: formData
        })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error al guardar el producto");
                    }
                    return response.text();
                })
                .then(html => {
                    cerrarFormularioProducto();
                    document.getElementById("tbodyProductos").innerHTML = html;

                    if (html.includes("class=\"action-buttons\"")) {
                        Swal.fire({
                            icon: "success",
                            title: "Exito",
                            text: "Producto guardado correctamente",
                            timer: 2000,
                            showConfirmButton: false
                        });
                    } else {
                        Swal.fire("Error", "No se pudo guardar el producto", "error");
                    }
                })
                .catch(() => {
                    Swal.fire("Error", "No se pudo guardar el producto", "error");
                });
    };
}

function refrescarListaProductos() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/products/admin/filter-fragment", true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.send();

    xhttp.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            document.getElementById("tbodyProductos").innerHTML = this.responseText;
        }
    };
}

function iniciarEventosProductosCliente() {
    const formFiltros = document.getElementById("form-filtros-productos");
    if (formFiltros && !formFiltros.dataset.bound) {
        formFiltros.dataset.bound = "true";
        formFiltros.addEventListener("submit", function (e) {
            e.preventDefault();

            const formData = new FormData(formFiltros);
            const params = new URLSearchParams(formData).toString();
            const url = "/products/user/filter?" + params;

            const xhttp = new XMLHttpRequest();
            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.send();

            xhttp.onreadystatechange = function () {
                if (this.readyState === 4 && this.status === 200) {
                    const tabla = document.querySelector("table");
                    const tbodyActual = tabla.querySelector("tbody");
                    if (tbodyActual) {
                        tbodyActual.remove();
                    }
                    tabla.insertAdjacentHTML("beforeend", this.responseText);
                }
            };
        });
    }

    if (!document.body.dataset.productClientEventsBound) {
        document.body.dataset.productClientEventsBound = "true";

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-details-product")) {
                const id = e.target.getAttribute("data-id");
                const contenedor = getProductDetailContainer();
                if (!contenedor) {
                    return;
                }
                contenedor.innerHTML = "Cargando...";

                const xhttp = new XMLHttpRequest();
                xhttp.open("GET", "/products/admin/details/" + id, true);
                xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                xhttp.send();

                xhttp.onreadystatechange = function () {
                    if (this.readyState === 4 && this.status === 200) {
                        contenedor.innerHTML = this.responseText;
                        contenedor.classList.add("active");
                    }
                };
            }
        });

        document.addEventListener("click", function (e) {
            if (e.target && e.target.classList.contains("btn-cerrar-detalles")) {
                cerrarDetalleProducto();
            }
        });
    }
}
