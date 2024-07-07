package demo.ttap.service;

import demo.ttap.entity.NSX;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NSXInterface extends JpaRepository<NSX,Integer> {
}
