package server.api;

import commons.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.service.TaskService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;

    /**
     * Constructor for the task controller
     * @param taskService the task service (injected)
     */
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Handles a GET request that returns a task by its ID
     * @param id the ID of the task
     * @return the task with the given ID (or a failed response)
     */
    @GetMapping("/get/{id}")
    ResponseEntity<Task> getTaskById(@PathVariable("id") long id) {
        Task task = taskService.getTaskById(id);
        if(task == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(task);
        }
    }

    /**
     * Handles a POST request that adds a task (or updates it if one such task
     * already exists)
     * @param task the task that needs to be added
     * @return the task that was added
     */
    @PostMapping("/add")
    ResponseEntity<Task> addTask(@RequestBody Task task) {
        Task addedTask = taskService.addTask(task);
        if(addedTask == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(task);
        }
    }

    private Map<Object, Consumer<Long>> listeners = new HashMap<>();

    /**
     * GET mapping to check if a Task is still in the database.
     * @param id Task ID
     * @return ID if Task was removed, NOT_MODIFIED otherwise
     */
    @GetMapping("/{id}/status")
    DeferredResult<ResponseEntity<Long>> checkStatus(@PathVariable("id") Long id){
        var timeoutValue = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        var res = new DeferredResult<ResponseEntity<Long>>(5000L, timeoutValue);
        var key = new Object();
        listeners.put(key, b -> {
            if(b.equals(id)){
                res.setResult(ResponseEntity.ok(id));
            }
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });
        return res;
    }

    /**
     * Mapping for deleting a task
     * @param id of the task to be deleted
     * @return deleted task or not found
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    ResponseEntity<Task> removeTask(@PathVariable("id") Long id){
        Task removed = taskService.removeTaskById(id);
        if(removed == null){
            return ResponseEntity.notFound().build();
        } else {
            listeners.forEach((k, v) -> {
                v.accept(id);
            });
            return ResponseEntity.ok(removed);
        }
    }
}
