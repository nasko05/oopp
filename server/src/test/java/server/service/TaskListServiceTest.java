package server.service;

import commons.Board;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.database.BoardDao;
import server.database.TaskListDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskListServiceTest {

    @InjectMocks
    private TaskListService taskListService;

    @MockBean
    private final TaskListDao taskListDao = Mockito.mock(TaskListDao.class);
    private final BoardDao boardDao = Mockito.mock(BoardDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTaskListById() {
        TaskList taskList = new TaskList();
        when(taskListDao.existsById(any(Long.class))).thenReturn(true);
        when(taskListDao.findById(any(Long.class))).thenReturn(Optional.of(taskList));
        TaskList result = taskListService.getTaskListById(1);
        assertThat(result).isEqualTo(taskList);
    }

    @Test
    void getTaskListByIdNULL() {
        when(taskListDao.existsById(any(long.class))).thenReturn(false);
        TaskList result = taskListService.getTaskListById(1);
        assertThat(result).isEqualTo(null);
    }

    @Test
    void getAllTaskLists() {
        List<TaskList> taskLists = new ArrayList<>();
        taskLists.add(new TaskList());
        when(taskListDao.findAll()).thenReturn(taskLists);
        var result = taskListService.getAllTaskLists();
        assertThat(result).isEqualTo(taskLists);
    }

    @Test
    void getAllTaskListsNULL() {
        when(taskListDao.findAll()).thenReturn(null);
        var result = taskListService.getAllTaskLists();
        assertThat(result).isEqualTo(null);
    }

    @Test
    void addTaskList() {
        TaskList taskList = new TaskList();
        taskList.setBoardId(33L);
        taskList.setTasks(new ArrayList<>());
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        when(taskListDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        when(taskListDao.save(any(TaskList.class))).thenReturn(taskList);
        var added = taskListService.addTaskList(taskList);
        assertThat(added).isEqualTo(taskList);
    }

    @Test
    void addTaskListNonExistentNULL() {
        TaskList taskList = new TaskList();
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        when(taskListDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        when(taskListDao.save(any(TaskList.class))).thenReturn(taskList);
        var added = taskListService.addTaskList(taskList);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void addTaskListNULL() {
        TaskList added = taskListService.addTaskList(null);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void removeTaskList() {
        TaskList taskList = new TaskList();
        taskList.setBoardId(33L);
        Board board = new Board();
        var list = new ArrayList<TaskList>();
        list.add(taskList);
        board.setTaskLists(list);
        when(taskListDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        when(taskListDao.save(any(TaskList.class))).thenReturn(taskList);
        var added = taskListService.removeTaskList(taskList);
        assertThat(added).isEqualTo(taskList);
    }

    @Test
    void removeTaskListNonExistentNULL() {
        TaskList taskList = new TaskList();
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        when(taskListDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        when(taskListDao.save(any(TaskList.class))).thenReturn(taskList);
        var added = taskListService.removeTaskList(taskList);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void removeTaskListNULL() {
        when(taskListDao.existsById(any(Long.class))).thenReturn(false);
        var added = taskListService.removeTaskList(new TaskList());
        assertThat(added).isEqualTo(null);
    }
}