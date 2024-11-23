package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String userName);

    @Modifying
    @Transactional
    @Query(value = "insert into user_role(user_id,role_id) values(?1,?2)",nativeQuery = true)
    void insertUserRole(Long userId,Long roleId);

    @Query("select us from User us where us.username =:username")
            User findUserByUsername(String username);

    @Query("select us from User us where us.email =:email")
    User findUserByEmail(String email);


//    Optional<User> findByEmail(String email);
//
//   List<User> findByCv_Ten(String cv);
//
//    List<User> findByHoTenContainingIgnoreCase(String name);
    Optional<User> findBySoDienthoai(String sdt);

    Page<User> findBySoDienthoaiContaining(String phoneNumber, Pageable pageable);
}
