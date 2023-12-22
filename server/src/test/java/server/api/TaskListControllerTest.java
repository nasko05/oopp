package server.api;

import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import server.service.TaskListService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskListControllerTest {

    @InjectMocks
    private TaskListController taskListController;

    @MockBean
    private final TaskListService taskListService = Mockito.mock(TaskListService.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTaskListByID() {
        TaskList taskList = new TaskList();
        when(taskListService.getTaskListById(any(long.class))).thenReturn(taskList);
        ResponseEntity<TaskList> result = taskListController.getTaskListById(1);
        assertThat(result).isEqualTo(ResponseEntity.ok(taskList));
    }

    @Test
    void getTaskListByIdNULL() {
        when(taskListService.getTaskListById(-1)).thenReturn(null);
        var result = taskListController.getTaskListById(-1);
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void getAllBoards() {
        List<TaskList> taskLists = new ArrayList<>();
        taskLists.add(new TaskList());
        when(taskListService.getAllTaskLists()).thenReturn(taskLists);
        ResponseEntity<List<TaskList>> result = taskListController.getAllTaskLists();
        assertThat(result).isEqualTo(ResponseEntity.ok(taskLists));
    }

    @Test
    void getAllBoardsNULL() {
        when(taskListService.getAllTaskLists()).thenReturn(null);
        ResponseEntity<List<TaskList>> result = taskListController.getAllTaskLists();
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void addTaskList() {
        TaskList taskList = new TaskList();
        when(taskListService.addTaskList(any(TaskList.class))).thenReturn(taskList);
        ResponseEntity<TaskList> added = taskListController.addTaskList(taskList);
        assertThat(added).isEqualTo(ResponseEntity.ok(taskList));
    }

    @Test
    void addTaskListNULL() {
        ResponseEntity<TaskList> added = taskListController.addTaskList(null);
        assertThat(added).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removeTaskList() {
        TaskList taskList = new TaskList();
        when(taskListService.removeTaskList(any(TaskList.class))).thenReturn(taskList);
        ResponseEntity<TaskList> removed = taskListController.removeTaskList(new TaskList());
        assertThat(removed).isEqualTo(ResponseEntity.ok(taskList));
    }

    @Test
    void removeTaskListNULL() {
        when(taskListService.removeTaskList(null)).thenReturn(null);
        ResponseEntity<TaskList> removed = taskListController.removeTaskList(null);
        assertThat(removed).isEqualTo(ResponseEntity.badRequest().build());
    }

}