package client.service;

import client.components.BoardView;
import client.components.TaskView;
import client.scenes.EditTagController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EditTagServiceTest {

    EditTagService editTagService;
    @Mock
    ServerUtils serverUtils;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    EditTagController editTagController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        editTagService = new EditTagService(mainCtrl, serverUtils);
        editTagService.setEditTagController(editTagController);
    }

    @Test
    void getterAndSetterTest() {
        EditTagController mockCtrl = mock(EditTagController.class);
        editTagService.setEditTagController(mockCtrl);

        assertEquals(mockCtrl, editTagService.getEditTagController());
    }

    @Test
    void saveNewTagFailedTest() {
        Mockito.doNothing().when(editTagController).displayFailedToSaveTag();
        TaskView taskView = mock(TaskView.class);
        editTagService.save(taskView, new Tag(),"description", "#ffffcc","#000000", null);
        verify(editTagController).displayFailedToSaveTag();
    }

    @Test
    void saveNewTagSucceedTest() {
        Mockito.doReturn(true).when(serverUtils).saveTag(any(Tag.class));

        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        Tag tag = new Tag();
        task.getTags().add(tag);
        task.setTaskListID(1L);
        TaskList taskList = new TaskList();
        taskList.setId(1L);
        taskList.setTasks(new ArrayList<>());
        taskList.addTask(task);
        Board board = new Board();
        board.setTitle("Title");
        board.setBoardBgColor("#f2f2f2");
        board.setTaskColorPresets(new ArrayList<>());
        board.setBoardFontColor("#000000");
        board.setTaskListsBgColor("#e6e6e6");
        board.setTaskListsFontColor("#00000");
        board.setTaskListsFontColor("#000000");
        board.setTaskLists(new ArrayList<>());
        board.setUserId("");
        board.addNewTaskList(taskList);
        board.setTaskDefaultColor(new ColorEntity());
        Mockito.doReturn(task).when(serverUtils).saveTaskByID(task);

        TaskView taskView = mock(TaskView.class);
        Mockito.doReturn(task).when(taskView).getAssociatedTask();
        BoardView boardView = mock(BoardView.class);
        Mockito.doReturn(boardView).when(mainCtrl).getBoardViewFromTask(any(Task.class));
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        editTagService.save(taskView, new Tag(),"description", "#ffffcc","#000000", null);

        verify(taskView).updateOverview(any(Task.class));
        verify(boardView).updateOverview(any(Board.class));
    }
}