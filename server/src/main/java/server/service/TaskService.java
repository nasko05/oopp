package server.service;

import commons.Task;
import commons.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.TaskDao;
import server.database.TaskListDao;

import java.util.Optional;

@Service
public class TaskService {
    private final TaskDao taskDao;
    private final TaskListDao taskListDao;

    /**
     * Injector constructor
     * @param taskDao task DB interface
     * @param taskListDao taskList DB interface
     */
    @Autowired
    public TaskService(TaskDao taskDao, TaskListDao taskListDao) {
        this.taskDao = taskDao;
        this.taskListDao = taskListDao;
    }

    /**
     * Returns a task given its id
     * @param id the id of the task
     * @return the found task
     */
    public Task getTaskById(long id) {
        if(!taskDao.existsById(id)) {
            return null;
        }
        Optional<Task> result = taskDao.findById(id);
        return result.orElse(null);
    }

    /**
     * Adds a new task to the db. The task can't be null
     * @param task the task to add
     * @return the newly added task
     */
    public Task addTask(Task task) {
        if(task == null) {
            return null;
        }
        task = taskDao.save(task);
        Optional<TaskList> taskList = taskListDao.findById(task.getTaskListID());
        if (taskList.isEmpty()) {
            return null;
        }
        taskList.get().addTask(task);
        taskListDao.save(taskList.get());
        return task;
    }

    /**
     * Remove a Task from its TaskList
     * @param id the if of the task that will be removed
     * @return the Task that is removed
     */
    public Task removeTaskById(long id){
        var task = taskDao.findById(id);
        if(task.isEmpty() || !taskListDao.existsById(task.get().getTaskListID())){
            return null;
        }
        var taskListOptional = taskListDao.findById(task.get().getTaskListID());
        if(taskListOptional.isEmpty()) return null;
        TaskList taskList = taskListOptional.get();
        taskList.getTasks().remove(task.get());
        taskListDao.save(taskList);
        taskDao.delete(task.get());
        return task.get();
    }
}
