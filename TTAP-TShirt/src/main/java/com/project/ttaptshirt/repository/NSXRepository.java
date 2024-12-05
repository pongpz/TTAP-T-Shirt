
package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.ChatLieu;
import com.project.ttaptshirt.entity.NSX;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NSXRepository extends JpaRepository<NSX,Long> {

    @Query("select cl from NSX cl where cl.ten =:ten")
    NSX getNSXByTen(String ten);
}
