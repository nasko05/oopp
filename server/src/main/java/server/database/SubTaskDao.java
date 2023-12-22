package server.database;


import commons.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubTaskDao extends JpaRepository<SubTask, Long> {
}
