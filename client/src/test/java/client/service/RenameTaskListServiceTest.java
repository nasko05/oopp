package client.service;

import client.components.BoardView;
import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import client.scenes.RenameTaskListController;
import client.utils.ServerUtils;
import commons.Board;
import commons.ColorEntity;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RenameTaskListServiceTest {

    @InjectMocks
    RenameTaskListService renameTaskListService;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils serverUtils;
    @Mock
    OverviewController overviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void rename() {
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(boardView).when(overviewController).getSelectedBoardView();
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();

        TaskList taskList = Mockito.spy(new TaskList());
        taskList.setTasks(new ArrayList<>());
        Mockito.doReturn(taskList).when(serverUtils).saveTaskList(any(TaskList.class));

        renameTaskListService.rename(taskList,"a new name");

        verify(overviewController, times(2)).getSelectedBoardView();
        verify(serverUtils).saveTaskList(any(TaskList.class));
        verify(boardView).updateOverview(any(Board.class));
        verify(mainCtrl).sendToOthers(taskList);
    }

    @Test
    void getterAndSetterTest() {
        RenameTaskListController mockCtrl = Mockito.mock(RenameTaskListController.class);
        renameTaskListService.setRenameTaskListController(mockCtrl);

        assertEquals(mockCtrl, renameTaskListService.getRenameTaskListController());
    }
}