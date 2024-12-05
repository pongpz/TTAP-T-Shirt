
package com.project.ttaptshirt.repository;


import com.project.ttaptshirt.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatLieuRepository extends JpaRepository<ChatLieu,Long> {
    @Query("select cl from ChatLieu cl where cl.ten =:ten")
    ChatLieu getChatLieuByTen(String ten);
}
