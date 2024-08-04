const deleteSpBtns = document.querySelectorAll(".deleteSpBtn");

console.log("hello world")
deleteSpBtns.forEach((element) => {
    element.addEventListener("click", (event) => {
        const userConfirmed = confirm('bạn có chắc chắn muốn xóa ?');
        if (!userConfirmed) {
            event.preventDefault();
        }
    });
});
const closeModal = $('.close');
const quantityForm = $('#quantityForm');


function editbtnOnClick(id) {
    // id modal
    var modalId = "#modal-suaSoLuong-" + id;
    //xu ly logic
    const quantityModal = $(modalId);
    console.log({quantityModal});
    $(quantityModal).css('display', 'block');

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


function updateMainContent(url) {
    $.ajax({
        url: url,
        type: 'GET',
        success: function(response) {
            $('#main-content').html(response);
        },
        error: function(error) {
            console.log('Error:', error);
        }
    });
}

updateMainContent('/admin/ban-hang');

function handleCTHD(event,idHD){
    var btnCthdId = "#chiTietHd-"+idHD;
    var btnCthd = $(btnCthdId);
        event.preventDefault();
     var urlCTHD =   btnCthd.attr("href");
    updateMainContent(urlCTHD);

}

function handleXoaSpKhoiHdct(event,idHD){
    var btnCtspId = "#btnXoaSpKhoiGioHang-"+idHD;
    var btnCthd = $(btnCtspId);
    event.preventDefault();
    var urlCTHD =   btnCthd.attr("href");
    updateMainContent(urlCTHD);

}

function btnSubmitHandle(idhdct){

    var idForm = "#quantityForm-"+idhdct;
    var form = $(idForm);
    form.submit();
}
