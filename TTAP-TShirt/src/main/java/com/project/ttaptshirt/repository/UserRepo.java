package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.User;
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


//    Optional<User> findByEmail(String email);
//
//   List<User> findByCv_Ten(String cv);
//
//    List<User> findByHoTenContainingIgnoreCase(String name);
}
