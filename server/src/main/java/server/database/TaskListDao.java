package server.database;

import commons.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskListDao extends JpaRepository<TaskList, Long> {
}
