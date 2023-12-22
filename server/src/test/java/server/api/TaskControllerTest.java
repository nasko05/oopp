package server.api;

import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import server.service.TaskService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @MockBean
    private final TaskService taskService = Mockito.mock(TaskService.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTaskById() {
        when(taskService.getTaskById(1)).thenReturn(new Task());
        var result = taskController.getTaskById(1);
        assertThat(result).isEqualTo(ResponseEntity.ok(new Task()));
    }

    @Test
    void getTaskByIdNULL() {
        when(taskService.getTaskById(-1)).thenReturn(null);
        var result = taskController.getTaskById(-1);
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void addTask() {
        Task task = new Task();
        when(taskService.addTask(any(Task.class))).thenReturn(new Task());
        ResponseEntity<Task> added = taskController.addTask(task);
        assertThat(added).isEqualTo(ResponseEntity.ok(task));
    }

    @Test
    void addTaskNULL() {
        ResponseEntity<Task> added = taskController.addTask(null);
        assertThat(added).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removeTask(){
        Task task = new Task();
        when(taskService.removeTaskById(1L)).thenReturn(task);
        var ret = taskController.removeTask(1L);
        assertThat(ret).isEqualTo(ResponseEntity.ok(task));
    }

    @Test
    void removeTaskNULL(){
        when(taskService.removeTaskById(1L)).thenReturn(null);
        var ret = taskController.removeTask(1L);
        assertThat(ret).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void checkStatusNULL(){
        when(taskService.removeTaskById(1L)).thenReturn(null);
        var ret = taskController.checkStatus(1L);
        assertThat(ret.getResult()).isEqualTo(null);
    }
}