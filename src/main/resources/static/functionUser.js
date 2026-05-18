
function iniciarEventosUsuarios() {
    if (window.userEventsInitialized) {
        return;
    }
    window.userEventsInitialized = true;

    // FILTRADO DE USUARIOS CON AJAX
    const formFiltros = document.getElementById("form-filtros");
    if (formFiltros) {
        formFiltros.addEventListener("submit", function (e) {
            e.preventDefault();

            const formData = new FormData(formFiltros);
            const params = new URLSearchParams(formData).toString();
            const url = "/users/filter-fragment?" + params;

            var xhttp = new XMLHttpRequest();
            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.send();

            xhttp.onreadystatechange = function () {
                if (this.readyState === 4) {
                    if (this.status === 200) {
                        document.getElementById("tbodyUsuarios").innerHTML = this.responseText;
                    } else {
                        console.error("Error al aplicar filtros:", this.status);
                    }
                }
            };
        });
    }

    // BOTÓN REFRESCAR SIN RECARGAR PÁGINA
    const btnRefrescar = document.querySelector(".refresh-button");
    if (btnRefrescar) {
        btnRefrescar.addEventListener("click", function (e) {
            e.preventDefault();

            var xhttp = new XMLHttpRequest();
            xhttp.open("GET", "/users/filter-fragment", true);
            xhttp.send();

            xhttp.onreadystatechange = function () {
                if (this.readyState === 4) {
                    if (this.status === 200) {
                        document.getElementById("tbodyUsuarios").innerHTML = this.responseText;
                    } else {
                        console.error("Error al refrescar la tabla:", this.status);
                    }
                }
            };
        });
    }

    // VER DETALLES DEL USUARIO
    document.addEventListener("click", function (e) {
        if (e.target && e.target.classList.contains("btn-details")) {
            let user = e.target.getAttribute("data-user");
            cargarDetalleUsuario(user);
        }
    });

    function cargarDetalleUsuario(user) {
        console.log("Cargando detalles del usuario:", user);

        var contenedor = document.getElementById("detalle-usuario");
        if (contenedor) {
            contenedor.innerHTML = "Cargando detalles...";

            var xhttp = new XMLHttpRequest();
            let url = "/users/details/" + user;
            console.log("URL solicitada:", url);

            xhttp.open("GET", url, true);
            xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            xhttp.send();

            xhttp.onreadystatechange = function () {
                if (this.readyState === 4) {
                    if (this.status === 200) {
                        contenedor.innerHTML = this.responseText;
                        contenedor.classList.add("active");
                    } else {
                        console.log("Error en la solicitud:", this.status, this.statusText);
                        contenedor.innerHTML = "<p>Error al cargar los detalles del usuario.</p>";
                    }
                }
            };
        }
    }
    document.addEventListener("click", function (e) {
        if (e.target && e.target.classList.contains("btn-cerrar-detalles")) {
            const contenedor = document.getElementById("detalle-usuario");
            if (contenedor) {
                contenedor.innerHTML = ""; // Limpia el contenido
                contenedor.classList.remove("active"); // Opcional si usás clases CSS
            }
        }
    });

    document.addEventListener("click", function (e) {
        if (e.target && e.target.classList.contains("btn-delete")) {
            // Evitar eliminar si es el botón "Cerrar"
            if (e.target.classList.contains("btn-cerrar-detalles"))
                return;

            e.preventDefault();
            const id_user = e.target.getAttribute("data-user") || new URLSearchParams(e.target.getAttribute("href").split('?')[1]).get('id_user');

            Swal.fire({
                title: '¿Estás seguro?',
                text: "Esta acción eliminará al usuario permanentemente.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed && id_user) {
                    // Hacer AJAX para eliminar con POST
                    const xhttp = new XMLHttpRequest();
                    xhttp.open("POST", "/users/delete", true);
                    xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
                    xhttp.send("id_user=" + id_user);

                    xhttp.onreadystatechange = function () {
                        if (this.readyState === 4) {
                            if (this.status === 200) {
                                document.getElementById("tbodyUsuarios").innerHTML = this.responseText;

                                // Mostrar alerta de éxito
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Éxito',
                                    text: 'Usuario eliminado correctamente',
                                    timer: 2000,
                                    showConfirmButton: false
                                });
                            } else {
                                Swal.fire(
                                        'Error',
                                        'No se pudo eliminar el usuario.',
                                        'error'
                                        );
                            }
                        }
                    };
                }
            });
        }
    });



}
