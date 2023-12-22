package server.api;

import commons.SubTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.SubtaskService;

@RestController
@RequestMapping("/api/subtask")
public class SubtaskController {
    private final SubtaskService subTaskService;

    /**
     * Constructor for the controller
     * @param subtaskService the subtask service (injected)
     */
    @Autowired
    public SubtaskController(SubtaskService subtaskService) {
        this.subTaskService = subtaskService;
    }

    /**
     * Handles the POST request that adds a new subtask.
     * @param subTask the subtask to add
     * @return the newly added subtask
     */
    @PostMapping("/add")
    ResponseEntity<SubTask> addNewSubTask(@RequestBody SubTask subTask) {
        SubTask addedSubTask = subTaskService.addNewSubTask(subTask);
        if (addedSubTask == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(addedSubTask);
        }
    }
}
