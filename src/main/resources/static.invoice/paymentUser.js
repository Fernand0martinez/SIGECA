   
   // El código dentro de DOMContentLoaded para la delegación de eventos sigue siendo útil
document.addEventListener("DOMContentLoaded", function () {
    document.addEventListener("click", function (e) {
        if (e.target && e.target.classList.contains("paid")) {
            const id = e.target.getAttribute("data-id");
            doPayment(id);
        }
    });
});
function doPayment(id) {
  Swal.fire({
    title: '¿Realizar pago?',
    text: 'No podrás deshacer esta acción.',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, pagar',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      
      window.location.href = '/payments/pay-form?id=' + id;
    }
  });
}



