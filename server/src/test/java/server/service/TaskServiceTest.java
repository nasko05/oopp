package server.service;

import commons.Task;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.database.TaskDao;
import server.database.TaskListDao;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @MockBean
    private final TaskDao taskDao = Mockito.mock(TaskDao.class);
    private final TaskListDao taskListDao = Mockito.mock(TaskListDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTaskById() {
        Task task = new Task();
        when(taskDao.existsById(any(Long.class))).thenReturn(true);
        when(taskDao.findById(any(Long.class))).thenReturn(Optional.of(task));
        Task result = taskService.getTaskById(1);
        assertThat(result).isEqualTo(task);
    }

    @Test
    void getTaskByIdNULL() {
        when(taskDao.existsById(any(long.class))).thenReturn(false);
        Task result = taskService.getTaskById(1);
        assertThat(result).isEqualTo(null);
    }
    

    @Test
    void addTask() {
        Task task = new Task();
        task.setTaskListID(3L);
        TaskList taskList = new TaskList();
        taskList.setTasks(new ArrayList<>());
        taskList.addTask(task);
        when(taskListDao.findById(any(Long.class))).thenReturn(Optional.of(taskList));
        when(taskDao.save(any(Task.class))).thenReturn(task);
        var added = taskService.addTask(task);
        assertThat(added).isEqualTo(task);
    }

    @Test
    void addTaskNonExistentNULL() {
        Task task = new Task();
        TaskList taskList = new TaskList();
        taskList.setTasks(new ArrayList<>());
        taskList.addTask(task);
        when(taskListDao.findById(any(Long.class))).thenReturn(Optional.of(taskList));
        when(taskDao.save(any(Task.class))).thenReturn(task);
        var added = taskService.addTask(task);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void addTaskNULL() {
        Task added = taskService.addTask(null);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void removeTaskById() {
        Task task = new Task();
        task.setId(1L);
        task.setTaskListID(2L);
        TaskList taskList = new TaskList();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);
        taskList.setTasks(tasks);

        task.setTaskListID(2L);
        Mockito.doReturn(Optional.of(task)).when(taskDao).findById(1L);
        Mockito.doReturn(true).when(taskListDao).existsById(any());
        Mockito.doReturn(Optional.of(taskList)).when(taskListDao).findById(2L);
        var ret = taskService.removeTaskById(1L);
        Mockito.verify(taskListDao).save(any());
        Mockito.verify(taskDao).delete(any());
        assertThat(task).isEqualTo(ret);
    }
    @Test
    void removeTaskByIdNull() {
        Task task = new Task();
        Mockito.doReturn(Optional.empty()).when(taskDao).findById(any());
        var ret = taskService.removeTaskById(1L);
        assertThat(ret).isEqualTo(null);
    }
}