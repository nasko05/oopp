package client.service;

import client.components.BoardView;
import client.scenes.ColorManagementController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.Board;
import commons.ColorEntity;
import commons.Task;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ColorManagementServiceTest {

    @InjectMocks
    ColorManagementService colorManagementService;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getterAndSetterTest() {
        ColorManagementController mockCtrl = Mockito.mock(ColorManagementController.class);
        colorManagementService.setColorManagementController(mockCtrl);

        assertEquals(mockCtrl, colorManagementService.getColorManagementController());
    }

    @Test
    void addColorPresetClickedTest() {
        ColorManagementController colorManagementController = Mockito.mock(ColorManagementController.class);
        colorManagementService.setColorManagementController(colorManagementController);

        Board board = new Board();
        board.setTaskDefaultColor(Mockito.mock(ColorEntity.class));
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskLists(new ArrayList<>());

        colorManagementService.addColorPresetClicked(board);

        verify(serverUtils).saveBoard(any(Board.class));
        verify(colorManagementController).updateColorPresets(any(Board.class));
        verify(mainCtrl).sendToOthers(any(Board.class));
    }

    @Test
    void deletePresetClickedTest() {
        ColorManagementController colorManagementController = Mockito.mock(ColorManagementController.class);
        colorManagementService.setColorManagementController(colorManagementController);

        Board board = new Board();
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskLists(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());

        colorManagementService.deletePresetClicked(Mockito.mock(ColorEntity.class),board);

        verify(serverUtils).saveBoard(any(Board.class));
        verify(colorManagementController).updateColorPresets(any(Board.class));
        verify(mainCtrl).sendToOthers(any(Board.class));
    }

    @Test
    void presetColorChangedTest() {
        ColorManagementController colorManagementController = Mockito.mock(ColorManagementController.class);
        colorManagementService.setColorManagementController(colorManagementController);

        Board board = new Board();
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskLists(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        ColorEntity colorEntity = Mockito.spy(new ColorEntity());
        colorEntity.setId(1L);
        board.getTaskColorPresets().add(colorEntity);

        colorManagementService.presetColorChanged(board,colorEntity);

        verify(serverUtils).saveBoard(any(Board.class));
        verify(colorManagementController).updateColorPresets(any(Board.class));
        verify(mainCtrl).sendToOthers(any(Board.class));
    }

    @Test
    void saveChange() {
        ColorManagementController colorManagementController =
                Mockito.mock(ColorManagementController.class);
        colorManagementService.setColorManagementController(colorManagementController);

        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());

        TaskList taskList = new TaskList();
        taskList.setTasks(new ArrayList<>());
        taskList.addTask(task);

        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        board.addNewTaskList(taskList);

        Mockito.doReturn(board).when(colorManagementController).getAssociatedBoard();
        Mockito.doReturn(board).when(serverUtils).saveBoard(any(Board.class));
        Mockito.doReturn(board).when(serverUtils).getBoardByID(any(Long.class));
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(boardView).when(mainCtrl).getBoardViewById(board.getId());

        colorManagementService.saveChange("1","2","3","4", mock(ColorEntity.class));

        verify(boardView).updateOverview(board);
        verify(mainCtrl).sendToOthers(board);
    }
}