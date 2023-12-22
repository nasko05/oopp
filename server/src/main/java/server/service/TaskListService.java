package server.service;

import commons.Board;
import commons.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.BoardDao;
import server.database.TaskListDao;

import java.util.List;
import java.util.Optional;

@Service
public class TaskListService {

    private final TaskListDao taskListDao;
    private final BoardDao boardDao;

    /**
     * Injector constructor
     * @param taskListDao task list(list) interface
     * @param boardDao the dao for the boards
     */
    @Autowired
    public TaskListService(TaskListDao taskListDao, BoardDao boardDao) {
        this.taskListDao = taskListDao;
        this.boardDao = boardDao;
    }

    /**
     * Get a task list by the given id
     * @param id id of the task list
     * @return the task list with thw given id
     */
    public TaskList getTaskListById(long id) {
        if(!taskListDao.existsById(id)) {
            return null;
        }
        return taskListDao.findById(id).orElse(null);
    }

    /**
     * Get all task lists from database
     * @return all task lists
     */
    public List<TaskList> getAllTaskLists() {
        return taskListDao.findAll();
    }

    /**
     * Add a task list to the database
     * @param taskList the task list to add
     * @return the added task list
     */
    public TaskList addTaskList(TaskList taskList) {
        if(taskList == null) {
            return null;
        }
        Optional<Board> board = boardDao.findById(taskList.getBoardId());
        if(board.isEmpty()) return null;
        taskList = taskListDao.save(taskList);
        TaskList ret = board.get().addNewTaskList(taskList);
        boardDao.save(board.get());
        return ret;
    }

    /**
     * Delete a task list from the database
     * @param taskList taskList of the task list to be removed
     * @return the removed task list
     */
    public TaskList removeTaskList(TaskList taskList){
        if(!taskListDao.existsById(taskList.getId())) {
            return null;
        }
        Optional<Board> board = boardDao.findById(taskList.getBoardId());
        if(board.isEmpty()) return null;
        TaskList ret = board.get().removeTaskList(taskList);

        boardDao.save(board.get());
        taskListDao.delete(taskList);
        return ret;
    }

}
