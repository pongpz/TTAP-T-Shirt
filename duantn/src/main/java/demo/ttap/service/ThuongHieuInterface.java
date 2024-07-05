package demo.ttap.service;

import demo.ttap.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThuongHieuInterface extends JpaRepository<ThuongHieu, Integer> {
}
