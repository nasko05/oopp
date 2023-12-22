package server.service;

import commons.SubTask;
import commons.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.SubTaskDao;
import server.database.TaskDao;

import java.util.Optional;

@Service
public class SubtaskService {
    private final SubTaskDao subTaskDao;
    private final TaskDao taskDao;

    /**
     * Constructor for the service
     * @param subTaskDao the subtask data access object (injected)
     * @param taskDao the task data access object (injected)
     */
    @Autowired
    public SubtaskService(SubTaskDao subTaskDao, TaskDao taskDao) {
        this.subTaskDao = subTaskDao;
        this.taskDao = taskDao;
    }

    /**
     * Adds a new subtask
     * @param subTask the subtask to add
     * @return the newly added subtask
     */
    public SubTask addNewSubTask (SubTask subTask) {
        if(subTask == null) return null;
        subTask = subTaskDao.save(subTask);
        Optional<Task> parentTask = taskDao.findById(subTask.getTaskId());
        if(parentTask.isEmpty()) {
            return null;
        }
        parentTask.get().addSubtask(subTask);
        taskDao.save(parentTask.get());
        return subTask;
    }
}
