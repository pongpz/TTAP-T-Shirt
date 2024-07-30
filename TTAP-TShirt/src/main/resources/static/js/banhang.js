const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

const deleteSpBtn = $('#deleteSpBtn');
const deleteSpBtns = $$(".deleteSpBtn");

deleteSpBtns.forEach((element)=>{
    element.onclick = (event)=>{
        const userConfirmed = confirm('bạn có chắc chắn muốn xóa ?');
        if (!userConfirmed) {
            event.preventDefault();
        }
    }
})


const closeModal = $('.close');
const quantityForm = $('#quantityForm');


function editbtnOnClick(id) {
    // id modal
    var modalId = "#modal-suaSoLuong-" + id;
    //xu ly logic
    const quantityModal = $(modalId);
    console.log({quantityModal});
    quantityModal.style.display = 'block';

}

// function closeQuantityModal(element) {
//     const modal = element.closest('.modal');
//     bootstrap.Modal.getInstance(modal).hide();
// }

window.addEventListener('click', function(event) {
    // if (event.target === quantityModal) {
    //     // quantityModal.style.display = 'none';
    // }
});
const quantityForms = $$(".quantityForm");

function handleSubmitUpdateQuantity(id, event) {
    event.preventDefault(); // Prevent default form submission behavior

    const detailInvoiceId = document.getElementById('detailInvoiceId-' + id).value;
    const quantity = document.getElementById('quantityModal-' + id).value;
    const form = document.getElementById("quantityForm-"+id);
    form.action = `/admin/ban-hang/hoa-don/sua-so-luong?soLuongSua=${quantity}&&idHdctSua=${detailInvoiceId}`;
    console.log(form.action);
    form.submit();
}


// quantityForms.forEach((quantityForm)=>{
//     quantityForm.addEventListener('submit', function(event) {
//         event.preventDefault();
//         const quantity = $('#quantity').value;
//         const modalValue = quantity;
//         const detailInvoiceId = $("")
//
//         // Assuming you're using Thymeleaf, you can update the form's action dynamically
//         quantityForm.action = `/admin/hoa-don/sua-so-luong?soLuongSua=${modalValue}?idSpSua=`;
//
//         // Submit the form
//         quantityForm.submit();
//     });
// })
// quantityForm.addEventListener('submit', function(event) {
//     event.preventDefault();
//     const quantity = $('#quantity').value;
//     const modalValue = quantity;
//     const detailInvoiceId = $("")
//
//     // Assuming you're using Thymeleaf, you can update the form's action dynamically
//     quantityForm.action = `/admin/hoa-don/sua-so-luong?soLuongSua=${modalValue}?idSpSua=`;
//
//     // Submit the form
//     quantityForm.submit();
// });
