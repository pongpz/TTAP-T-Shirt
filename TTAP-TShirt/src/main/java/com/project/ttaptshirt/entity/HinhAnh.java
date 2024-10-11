package com.project.ttaptshirt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "anh_san_pham")
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten")
    private String ma;

    @Column(name = "ten_url")
    private String ten;

    @Column(name = "trang_thai")
    private String trangThai;

    @Transient
        public String getphotoPath(){
        if(ten == null) return null;
        return "/images/"+ten;
    }

    // Getters and Setters
}
