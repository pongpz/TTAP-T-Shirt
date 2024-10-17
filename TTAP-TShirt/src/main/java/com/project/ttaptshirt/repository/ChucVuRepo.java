package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChucVuRepo extends JpaRepository<Role, Long> {
//    Role findByTen(String name);
}
