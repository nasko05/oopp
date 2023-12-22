package client.service;

import client.components.BoardView;
import client.components.TaskView;
import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import client.scenes.TaskViewController;
import client.utils.ServerUtils;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskViewServiceTest {

    @InjectMocks
    TaskViewService taskViewService;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils serverUtils;
    @Mock
    OverviewController overviewController;
    @Mock
    TaskViewController taskViewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getterAndSetterTest() {
        TaskViewController mockCtrl = Mockito.mock(TaskViewController.class);
        taskViewService.setTaskViewController(mockCtrl);

        assertEquals(mockCtrl, taskViewService.getTaskViewController());
    }

    @Test
    void saveTask() {
        TaskViewController taskViewController =
                Mockito.spy(new TaskViewController(mainCtrl, taskViewService, new ServerUtils()));
        taskViewService.setTaskViewController(taskViewController);

        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        task.setTaskListID(2L);

        TaskList taskList = spy(new TaskList());
        taskList.setId(2L);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        taskList.setTasks(tasks);

        TaskView taskView = Mockito.mock(TaskView.class);

        Mockito.doReturn(task).when(taskViewController).getCurrentTask();
        Mockito.doReturn(task).when(serverUtils).saveTaskByID(any(Task.class));
        Mockito.doReturn(taskView).when(taskViewController).getCurrentTaskView();
        Mockito.doReturn(task).when(serverUtils).getTaskByID(any(Long.class));
        Board board = new Board();
        List<TaskList> taskLists = new ArrayList<>();
        taskLists.add(taskList);
        board.setTaskLists(taskLists);
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(boardView).when(mainCtrl).getBoardViewFromTask(any(Task.class));
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();

        taskViewService.saveTask("a title", "a description", "#cccccc",
                "#000000", new ArrayList<>());

        verify(serverUtils).saveTaskByID(any(Task.class));
        verify(mainCtrl).getBoardViewFromTask(task);
        verify(taskViewController).getCurrentTaskView();
        verify(taskView).updateOverview(task);
        verify(mainCtrl).sendToOthers(task);
    }

    @Test
    public void deleteSubtask() {
        TaskViewController mockCtrl = Mockito.mock(TaskViewController.class);

        taskViewService.setTaskViewController(mockCtrl);
        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        Mockito.doReturn(task).when(mockCtrl).getCurrentTask();
        Mockito.doReturn(task).when(serverUtils).saveTaskByID(any(Task.class));

        TaskView fakeTaskView = Mockito.mock(TaskView.class);
        Mockito.doReturn(fakeTaskView).when(mockCtrl).getCurrentTaskView();

        SubTask subTask = Mockito.mock(SubTask.class);
        taskViewService.deleteSubtask(subTask);
        verify(fakeTaskView).updateOverview(any(Task.class));
        verify(serverUtils).saveTaskByID(any(Task.class));
    }
    @Test
    void removeTagClicked() {
        Tag tag = new Tag();
        tag.setId(1L);
        Mockito.doReturn(tag).when(serverUtils).removeTag(any(), any());
        var ret = taskViewService.removeTagClicked(1L, tag);
        assertEquals(ret, tag);
    }

    @Test
    void addTagClickedNull() {

        var ret = taskViewService.addTagClicked();
        assertNull(ret);
    }

    @Test
    void addTagClicked() {
        taskViewService.setTaskViewController(taskViewController);
        Board board = new Board();
        board.setId(1L);
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        Mockito.doReturn(boardView).when(mainCtrl).getBoardViewFromTask(any());
        Tag tag = new Tag();
        Mockito.doReturn(tag).when(serverUtils).addNewTag(any(Tag.class));
        var ret = taskViewService.addTagClicked();
        assertEquals(tag, ret);
    }

    @Test
    void deleteTagTest() {
        TaskViewController taskViewController = Mockito.mock(TaskViewController.class);
        taskViewService.setTaskViewController(taskViewController);

        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.doReturn(taskView).when(taskViewController).getCurrentTaskView();
        Mockito.doReturn(task).when(taskView).getAssociatedTask();

        taskViewService.deleteTag(Mockito.mock(Tag.class));
        verify(serverUtils).saveTaskByID(any(Task.class));
        verify(taskViewController, times(2)).getCurrentTaskView();
    }
}