package server.api;

import commons.TaskList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.TaskListService;

import java.util.List;

@RestController
@RequestMapping("/api/tasklist")
public class TaskListController {
    private final TaskListService taskListService;

    /**
     * Constructor of taskList controller
     * @param taskListService the taskList service
     */
    @Autowired
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    /**
     * Handles a GET request that returns a taskList by its ID
     * @param id the ID of the taskList
     * @return the taskList with the given ID (or a failed response)
     */
    @GetMapping("/get/{id}")
    ResponseEntity<TaskList> getTaskListById(@PathVariable("id") long id) {
        TaskList taskList = taskListService.getTaskListById(id);
        if(taskList == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(taskList);
        }
    }

    /**
     * Get all taskLists from the database
     * @return all taskLists
     */
    @GetMapping("/get")
    public ResponseEntity<List<TaskList>> getAllTaskLists() {
        List<TaskList> taskLists = taskListService.getAllTaskLists();
        if(taskLists == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(taskLists);
        }
    }

    /**
     * Add a new taskList
     * @param taskList the taskList to add
     * @return the added taskList
     */
    @PostMapping("/add")
    public ResponseEntity<TaskList> addTaskList(@RequestBody TaskList taskList) {
        TaskList newTaskList = taskListService.addTaskList(taskList);
        if(newTaskList == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(newTaskList);
        }
    }

    /**
     * Delete a task list from the database
     * @param taskList the taskList to delete
     * @return the deleted taskList
     */
    @PostMapping("/remove")
    public ResponseEntity<TaskList> removeTaskList(@RequestBody TaskList taskList) {
        TaskList removedTaskList = taskListService.removeTaskList(taskList);
        if(removedTaskList == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(removedTaskList);
        }
    }
}
