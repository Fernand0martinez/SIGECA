document.addEventListener('DOMContentLoaded', initTeamUser);

function initTeamUser() {
    initTeamFilters();
    initNewTeamButton();
    initDeleteHandlerUser();
}

function toggleFormUser(editMode, data) {
    const container = document.getElementById('team-form-container');
    const url = editMode
            ? `/teams/user/form?idTeam=${data.id}`
            : '/teams/user/form';

    ajaxGet(url, html => {
        container.innerHTML = html;
        attachFormListenerUser();
    });
}

function attachFormListenerUser() {
    const form = document.querySelector('#team-form-container form');
    if (!form)
        return;
    form.addEventListener('submit', handleFormSubmitUser);
}

function handleFormSubmitUser(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    ajaxPost(form.action, formData, html => {
        document.getElementById('team-form-container').innerHTML = '';
        document.getElementById('team-table-container').innerHTML = html;
        initDeleteHandlerUser();
        initNewTeamButton();
        initTeamFilters();
        showToast('success', 'Team saved');
    });
}

function initDeleteHandlerUser() {
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.onclick = () => {
            const id = btn.dataset.id;
            ajaxPost('/teams/user/delete', `id=${id}`, html => {
                document.getElementById('team-table-container').innerHTML = html;
                showToast('success', 'Team deleted');
            });
        };
    });
}

function initNewTeamButton() {
    const btn = document.getElementById('btn-new-team');
    if (btn)
        btn.onclick = () => toggleFormUser(false);
}

function initTeamFilters() {
    const form = document.getElementById('filter-form');
    if (!form)
        return;
    form.onsubmit = e => {
        e.preventDefault();
        const q = document.getElementById('search_query').value;
        ajaxGet(`/teams/user/filter?query=${encodeURIComponent(q)}`, html => {
            document.getElementById('team-table-container').innerHTML = html;
            initDeleteHandlerUser();
        });
    };
}

// Helpers AJAX
function ajaxGet(url, cb) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.onload = () => {
        if (xhr.status === 200)
            cb(xhr.responseText);
    };
    xhr.send();
}
function ajaxPost(url, data, cb) {
    const xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = () => {
        if (xhr.status === 200)
            cb(xhr.responseText);
    };
    xhr.send(data instanceof FormData ? formDataToUrlEncoded(data) : data);
}
function formDataToUrlEncoded(fd) {
    return [...fd.entries()].map(([k, v]) => `${k}=${encodeURIComponent(v)}`).join('&');
}
function showToast(type, msg) {
    if (typeof Swal !== "undefined") {
        Swal.fire({
            icon: type === "success" ? "success" : "info",
            text: msg,
            timer: 1800,
            showConfirmButton: false
        });
        return;
    }
    if (typeof Toastify !== "undefined") {
        Toastify({text: msg, duration: 3000, gravity: 'top', position: 'right'}).showToast();
    }
}
function cancelFormUser() {
    document.getElementById('team-form-container').innerHTML = '';
}
