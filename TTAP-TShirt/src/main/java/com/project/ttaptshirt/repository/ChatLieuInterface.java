package demo.ttap.service;

import demo.ttap.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLieuInterface extends JpaRepository<ChatLieu,Integer> {
}
