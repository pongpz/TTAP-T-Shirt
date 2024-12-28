package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.DiaChi;
import com.project.ttaptshirt.entity.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<TaiKhoan, Long> {
    TaiKhoan findByUsername(String userName);

    @Query("select us from TaiKhoan us where us.nhanVien.id =:id")
    TaiKhoan findByNhanVienId(Long id);

    @Query("select us from TaiKhoan us where us.username =:username")
    TaiKhoan findUserByUsername(String username);



    @Query("select us from TaiKhoan us where us.username =:username and us.id <>:idUS")
    TaiKhoan findUserByUsernameUpdate(String username, Long idUS);

    @Query("SELECT t.defaultAddress FROM TaiKhoan t WHERE t.id = :userId")
    Optional<DiaChi> findDefaultAddressByUserId(@Param("userId") Long userId);


    @Query("SELECT u FROM TaiKhoan u  where u.role= :roleName")
    Page<TaiKhoan> findUsersByRoleName(String roleName, Pageable pageable);

//    Optional<User> findByEmail(String email);
//
//   List<User> findByCv_Ten(String cv);
//
//    List<User> findByHoTenContainingIgnoreCase(String name);
//    Optional<TaiKhoan> findBySoDienthoai(String sdt);

//    Page<TaiKhoan> findBySoDienthoaiContaining(String phoneNumber, Pageable pageable);

    // Repository cho User
    @Modifying
    @Query("UPDATE TaiKhoan u SET u.defaultAddress = NULL WHERE u.defaultAddress.id = :addressId")
    void updateAddressToNull(@Param("addressId") Long addressId);

}
