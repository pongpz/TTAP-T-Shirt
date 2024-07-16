// function displayHoaDonChiTiet(url){
//     var hoadonId = url.split('?')[1].split('=')[1];
//     $.ajax({
//         url: url,
//         type: 'GET',
//         success: function(response) {
//             displayHDCT(response);
//         },
//         error: function(error) {
//             console.log('Error:', error);
//         }
//     });
// }
// function displayHDCT(listHDCT){
//     var tbody = $(".thongTinHoaDon");
//     tbody.empty();
//     listHDCT.forEach(function(item) {
//         var row = `
//             <tr>
//                 <td>${item.id}</td>
//                 <td>${item.chiTietSanPham.sanPham.ten}</td>
//                 <td>${item.chiTietSanPham.mauSac.ten}</td>
//                 <td>${item.chiTietSanPham.kichCo.ten}</td>
//                 <td>${item.chiTietSanPham.chatLieu.ten}</td>
//                 <td>${item.chiTietSanPham.kieuDang.ten}</td>
//                 <td>${item.chiTietSanPham.thuongHieu.ten}</td>
//                 <td>${item.chiTietSanPham.nsx.ten}</td>
//                 <td>${item.soLuong}</td>
//                 <td>${item.chiTietSanPham.giaBan}</td>
//                 <td>${item.soLuong*item.chiTietSanPham.giaBan}</td>
//             </tr>
//         `;
//         tbody.append(row);
//     });
// }