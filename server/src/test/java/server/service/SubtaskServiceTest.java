package server.service;

import commons.SubTask;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.database.SubTaskDao;
import server.database.TaskDao;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {

    @InjectMocks
    private SubtaskService subtaskService;

    @MockBean
    private final SubTaskDao subTaskDao = Mockito.mock(SubTaskDao.class);

    @MockBean
    private final TaskDao taskDao = Mockito.mock(TaskDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addSubTask() {
        SubTask subTask = new SubTask();
        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        when(subTaskDao.save(any(SubTask.class))).thenReturn(subTask);
        when(taskDao.findById(any())).thenReturn(Optional.of(task));
        when(taskDao.save(any(Task.class))).thenReturn(task);
        SubTask added = subtaskService.addNewSubTask(subTask);
        assertThat(added).isEqualTo(subTask);
    }

    @Test
    void addSubTaskNULL() {
        SubTask subTask = new SubTask();
        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        when(subTaskDao.save(any(SubTask.class))).thenReturn(subTask);
        when(taskDao.findById(any())).thenReturn(Optional.empty());
        SubTask ret = subtaskService.addNewSubTask(subTask);
        assertThat(ret).isEqualTo(null);
    }
}