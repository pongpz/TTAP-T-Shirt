
        const userConfirmed = confirm('bạn có chắc chắn muốn xóa ?');
        if (!userConfirmed) {
            event.preventDefault();
        }
const closeModal = $('.close');
const quantityForm = $('#quantityForm');


function editbtnOnClick(id) {
    // id modal
    var modalId = "#modal-suaSoLuong-" + id;
    //xu ly logic
    const quantityModal = $(modalId);
    console.log({quantityModal});

}

// function closeQuantityModal(element) {
//     const modal = element.closest('.modal');
//     bootstrap.Modal.getInstance(modal).hide();
// }


function handleSubmitUpdateQuantity(id, event) {
    event.preventDefault(); // Prevent default form submission behavior

    const detailInvoiceId = document.getElementById('detailInvoiceId-' + id).value;
    const quantity = document.getElementById('quantityModal-' + id).value;
    const form = document.getElementById("quantityForm-"+id);
    form.action = `/admin/ban-hang/hoa-don/sua-so-luong?soLuongSua=${quantity}&&idHdctSua=${detailInvoiceId}`;
    console.log(form.action);
    form.submit();
}


