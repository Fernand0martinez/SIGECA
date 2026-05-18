/* ========== CONSTANTES Y CONFIGURACIONES ========== */
const TOAST_CONFIG = {
    success: {
        background: '#27ae60',
        text: '✓'
    },
    error: {
        background: '#e74c3c',
        text: '✕'
    }
};

const SWAL_CONFIG = {
    delete: {
        title: 'Confirmar Eliminación',
        htmlTemplate: name => `¿Está seguro de eliminar la cancha <strong>"${name}"</strong>?`,
        confirmText: 'Sí, eliminar',
        cancelText: 'Cancelar',
        confirmColor: '#e74c3c',
        cancelColor: '#95a5a6'
    },

    save: {
        title: "¿Guardar cambios?",
        confirmText: "Guardar",
        denyText: "No guardar",
        cancelText: "Cancelar",
        confirmColor: '#27ae60',
        denyColor: '#e74c3c',
        cancelColor: '#95a5a6'
    }
};

/* ========== MANEJADORES DE EVENTOS DEL DOM ========== */
document.addEventListener('DOMContentLoaded', initApp);

function initApp() {
    initMasks();
    initFilters();

    if (window.location.pathname.includes('/admin')) {
        initDeleteHandler();
        initNewCourtButton();
    }
    const msgContainer = document.getElementById('container-messages');
    if (msgContainer && msgContainer.style) {
        msgContainer.style.display = 'none';
    }
}

/* ========== CONFIGURACIÓN INICIAL ========== */
function initMasks() {
    // Máscara para precio
    const priceInput = document.getElementById('priceByHour');
    if (priceInput) {
        new Inputmask({
            alias: 'numeric',
            digits: 2,
            digitsOptional: false,
            placeholder: '0.00',
            allowMinus: false,
            autoGroup: true,
            unmaskAsNumber: true
        }).mask(priceInput);
    }

    // Máscara para ubicación
    const locationInput = document.getElementById('location');
    if (locationInput) {
        new Inputmask({
            placeholder: ' ',
            showMaskOnHover: false
        }).mask(locationInput);
    }

    // Estilos para selects
    document.querySelectorAll('select.form-input').forEach(select => {
        select.addEventListener('change', handleSelectChange);
    });
}

function handleSelectChange() {
    const span = this.parentElement.querySelector('span');
    span.style.opacity = this.value ? '1' : '0';
}

/* ========== MANEJADORES DE FORMULARIOS ========== */
function toggleForm(editMode, courtData) {
    const formContainer = document.getElementById("court-form-container");
    const url = editMode ? `/courts/admin/form?idCourt=${courtData.idCourt}` : "/courts/admin/form";

    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url, true);
    xhttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            formContainer.innerHTML = xhttp.responseText;
            attachFormListener();
            initMasks();
        }
    };
    xhttp.send();
}

function attachFormListener() {
    const form = document.querySelector("#court-form-container form");
    if (!form)
        return;

    form.addEventListener("submit", handleFormSubmit);
}

function handleFormSubmit(e) {
    e.preventDefault();
    const form = this;
    const formData = new FormData(form);

    Swal.fire({
        title: SWAL_CONFIG.save.title,
        showDenyButton: true,
        showCancelButton: true,
        confirmButtonText: SWAL_CONFIG.save.confirmText,
        denyButtonText: SWAL_CONFIG.save.denyText,
        cancelButtonText: SWAL_CONFIG.save.cancelText,
        confirmButtonColor: SWAL_CONFIG.save.confirmColor,
        denyButtonColor: SWAL_CONFIG.save.denyColor,
        cancelButtonColor: SWAL_CONFIG.save.cancelColor,
        customClass: "custom-swal"
    }).then((result) => {
        if (result.isConfirmed) {
            submitFormData(form.action, formData);
        } else if (result.isDenied) {
            document.getElementById("court-form-container").innerHTML = "";
            showToast("info", "Cambios no guardados");
        }
    });
}

//funcion para enviar el formulario
function submitFormData(url, formData) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", url, true);

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            //para ocultar el formulario 
            document.getElementById("court-form-container").innerHTML = "";
            //actualiza la tabla
            document.getElementById("court-table-container").innerHTML = xhttp.responseText;

            // verifica y reinicia el handler
            const checkDOM = setInterval(() => {
                if (document.querySelector('.delete-btn')) {
                    initDeleteHandler();
                    initMasks();
                    clearInterval(checkDOM);
                }
            }, 200);

            showToast('success', formData.get('idCourt') ? 'Cancha actualizada' : 'Cancha creada');
        }
    };
    xhttp.send(formData);
}

/* ========== MANEJADORES DE ELIMINACIÓN ========== */
function initDeleteHandler() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.removeEventListener('click', handleDelete);
    });
    
    
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', handleDelete);
    });
}

function handleDelete() {
    const {name, id} = this.dataset;
    showDeleteConfirmation(name, id);
}

function showDeleteConfirmation(name, id) {
    Swal.fire({
        title: SWAL_CONFIG.delete.title,
        html: SWAL_CONFIG.delete.htmlTemplate(name),
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: SWAL_CONFIG.delete.confirmColor,
        cancelButtonColor: SWAL_CONFIG.delete.cancelColor,
        confirmButtonText: SWAL_CONFIG.delete.confirmText,
        cancelButtonText: SWAL_CONFIG.delete.cancelText,
        customClass: 'custom-swal'
    }).then((result) => {
        if (result.isConfirmed) {
            executeDelete(id);
        }
    });
}

function executeDelete(id) {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/courts/admin/delete", true);
    xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4) {
            if (xhttp.status === 200) {
                document.getElementById("court-table-container").innerHTML = xhttp.responseText;
                showToast('success', 'Cancha eliminada');

                initDeleteHandler();
            } else {
                handleError(xhttp);
            }
        }
    };
    xhttp.send(`id_court=${id}`);
}

/* ========== MANEJADORES DE FILTROS ========== */
function initFilters() {
    const filterForm = document.getElementById("filter-form");
    if (!filterForm)
        return;

    filterForm.addEventListener("submit", handleFilterSubmit);
}

function handleFilterSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const query = document.getElementById("search_query").value;

    const basePath = form.action.replace('/filter', '');
    const url = `${basePath}/filter${query ? `?query=${encodeURIComponent(query)}` : ''}`;

    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", url, true);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4) {
            if (xhttp.status === 200) {
                document.getElementById("court-table-container").innerHTML = xhttp.responseText;
                showToast('info', `Filtrado: ${query || 'todas'}`);

                if (document.querySelector('.delete-btn')) {
                    initDeleteHandler();
                }
            } else {
                handleError(xhttp);
            }
        }
    };
    xhttp.send();
}

/* ========== MANEJADOR DE ERRORES ========== */
function handleError(xhttp) {
    const errorMsg = xhttp.statusText || 'Error en la solicitud';
    showToast('error', errorMsg);
    console.error(`Error ${xhttp.status}: ${errorMsg}`);
}

/* ========== MANEJADORES DE NOTIFICACIONES ========== */
function showToast(type, message) {
    if (typeof Toastify === "undefined") {
        if (typeof Swal !== "undefined") {
            Swal.fire({
                icon: type === "error" ? "error" : "success",
                text: message,
                timer: 1800,
                showConfirmButton: false
            });
        }
        return;
    }
    Toastify({
        text: `${TOAST_CONFIG[type].text} ${message}`,
        duration: 3000,
        close: true,
        gravity: "top",
        position: "right",
        style: {
            background: TOAST_CONFIG[type].background,
            color: '#fff',
            fontFamily: "'Roboto', sans-serif",
            borderRadius: '4px',
            boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
        }
    }).showToast();
}

/* ========== INICIALIZACIÓN DE COMPONENTES ========== */
function initNewCourtButton() {
    const btnNewCourt = document.getElementById("btn-new-court");
    if (btnNewCourt) {
        btnNewCourt.addEventListener("click", () => toggleForm(false));
    }
}

function cancelForm() {
    document.getElementById('court-form-container').innerHTML = '';
}
