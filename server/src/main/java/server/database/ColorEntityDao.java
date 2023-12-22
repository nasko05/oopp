package server.database;

import commons.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorEntityDao extends JpaRepository<ColorEntity, Long> {
}
