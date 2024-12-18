package com.project.ttaptshirt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Entity
@Table(name = "san_pham")
public class SanPham extends CreatedUpdatedAt{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten", columnDefinition = "NVARCHAR(255)")
    private String ten;


    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(255)")
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "id_nha_san_xuat")
    private NSX nsx;
  
    @ManyToOne
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "id_kieu_dang")
    private KieuDang kieuDang;


    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "sanPham",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ChiTietSanPham> chiTietSanPhamList;

    @OneToMany(mappedBy = "sanPham", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HinhAnh> hinhAnhList;

    public int tongSoLuong(){
        if(chiTietSanPhamList == null || chiTietSanPhamList.isEmpty()){
            return 0;
        }
        return chiTietSanPhamList.stream().mapToInt(ChiTietSanPham::getSoLuong).sum();
    }



}
