package client.service;

import client.components.BoardView;
import client.components.TaskListView;
import client.components.Workspace;
import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import client.utils.ServerUtils;
import commons.Board;
import commons.ColorEntity;
import commons.Task;
import commons.TaskList;
import javafx.scene.input.Clipboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OverviewServiceTest {

    @InjectMocks
    OverviewService overviewService;
    @Mock
    ServerUtils serverUtils;
    @Mock
    MainCtrl mainCtrl;
    OverviewController overviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        overviewController = Mockito.spy(new OverviewController(overviewService));
        overviewService.setOverviewController(overviewController);
    }

    @Test
    void getterAndSetterTest() {
        OverviewController mockCtrl = Mockito.mock(OverviewController.class);
        overviewService.setOverviewController(mockCtrl);

        assertEquals(mockCtrl, overviewService.getOverviewController());
    }

    @Test
    void canNotParseBoardIDTest() {
        Mockito.doNothing().when(overviewController).resetJoinBoardText();
        Mockito.doNothing().when(overviewController).showError(any(String.class));
        overviewService.joinBoard("a string");
        verify(overviewController).showError(any(String.class));
    }

    @Test
    void boardAlreadyOpenTest() {
        HashSet<Long> openedBoards = new HashSet<>();
        openedBoards.add(1L);
        Mockito.doNothing().when(overviewController).resetJoinBoardText();
        Mockito.doReturn(openedBoards).when(overviewController).getOpenedBoards();
        Mockito.doNothing().when(overviewController).showBoardAlreadyOpenError();
        Board board = new Board();
        board.setId(1L);
        Mockito.doReturn(board).when(serverUtils).getBoardByID(1);
        overviewService.joinBoard("1");
        verify(overviewController).showBoardAlreadyOpenError();
    }

    @Test
    void boardDoesNotExistTest() {
        Mockito.doNothing().when(overviewController).showError(any(String.class));
        Mockito.doNothing().when(overviewController).resetJoinBoardText();

        overviewService.joinBoard("1");

        verify(serverUtils).getBoardByID(1);
        verify(overviewController).showError(any(String.class));
    }

    @Test
    void joinBoardSucceedTest() {
        Board board = new Board();
        board.setId(1);
        Mockito.doNothing().when(overviewController).resetJoinBoardText();
        Mockito.doReturn(board).when(serverUtils).getBoardByID(1);
        Mockito.doNothing().when(overviewController).displayBoard(any(Board.class));
        Mockito.doNothing().when(overviewController).showError("Please enter correct board_id");
        Mockito.doNothing().when(overviewController).showBoardAlreadyOpenError();
        Mockito.doNothing().when(overviewController).addRecentBoard(1L);
        overviewService.joinBoard("1");

        verify(overviewController).displayBoard(board);
    }

    @Test
    void addNewTaskListFailedTest() {
        Board board = new Board();
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        Mockito.doNothing().when(overviewController).showError("Please enter correct board_id");

        overviewService.addNewTaskList(boardView);

        verify(boardView).getAssociatedBoard();
        verify(serverUtils).addNewTaskList(any(TaskList.class));
        verify(overviewController).showError("Please enter correct board_id");
    }

    @Test
    void addNewTaskListInterruptedTest() {
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        doNothing().when(boardView).updateOverview(board);

        TaskList taskList = new TaskList();
        Mockito.doReturn(taskList).when(serverUtils).addNewTaskList(any(TaskList.class));

        Thread.currentThread().interrupt();
        assertThrows(RuntimeException.class, () -> overviewService.addNewTaskList(boardView));
    }

    @Test
    void addNewTaskListSucceedTest() {
        Board board = new Board();
        board.setTaskLists(new ArrayList<>());
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        doNothing().when(boardView).updateOverview(board);

        TaskList taskList = new TaskList();
        Mockito.doReturn(taskList).when(serverUtils).addNewTaskList(any(TaskList.class));

        overviewService.addNewTaskList(boardView);

        verify(boardView, times(2)).getAssociatedBoard();
        verify(serverUtils).addNewTaskList(any(TaskList.class));
        verify(boardView).updateOverview(any(Board.class));
        verify(mainCtrl).sendToOthers(any(Board.class));
    }

    @Test
    void removeTaskListFailedTest() {
        Board board = Mockito.mock(Board.class);
        BoardView boardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(board).when(boardView).getAssociatedBoard();

        TaskList taskList = new TaskList();
        TaskListView taskListView = Mockito.mock(TaskListView.class);
        Mockito.doReturn(taskList).when(taskListView).getAssociatedTaskList();
        Mockito.doReturn(null).when(serverUtils).removeTaskList(taskList);
        Mockito.doNothing().when(overviewController).showError("Please enter correct board_id");

        overviewService.removeTaskList(taskListView, boardView);

        verify(serverUtils).removeTaskList(taskList);
        verify(taskListView).getAssociatedTaskList();
        verify(overviewController).showError("Please enter correct board_id");
    }

    @Test
    void removeTaskListSucceedTest() {
        Board board = Mockito.spy(new Board());
        board.setTaskLists(new ArrayList<>());
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        BoardView boardView = Mockito.mock(BoardView.class);
        doReturn(board).when(boardView).getAssociatedBoard();

        TaskList taskList = new TaskList();
        taskList.setTasks(new ArrayList<>());
        TaskListView taskListView = Mockito.mock(TaskListView.class);
        Mockito.doReturn(taskList).when(taskListView).getAssociatedTaskList();
        Mockito.doReturn(taskList).when(serverUtils).removeTaskList(any(TaskList.class));

        overviewService.removeTaskList(taskListView, boardView);

        verify(serverUtils).removeTaskList(taskList);
        verify(boardView).getAssociatedBoard();
        verify(boardView).updateOverview(any(Board.class));
        verify(mainCtrl).sendToOthers(any(Board.class));
    }

    @Test
    void addNewTask() {
        Task task = new Task();
        task.setTags(new ArrayList<>());
        task.setSubTasks(new ArrayList<>());
        task.setTaskListID(2L);

        TaskList taskList = spy(new TaskList());
        taskList.setId(2L);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        taskList.setTasks(tasks);

        TaskListView taskListView = Mockito.mock(TaskListView.class);

        Board board = spy(new Board());
        List<TaskList> taskLists = new ArrayList<>();
        ColorEntity colorEntity = Mockito.mock(ColorEntity.class);
        taskLists.add(taskList);
        board.setTaskLists(taskLists);
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(colorEntity);
        BoardView boardView = mock(BoardView.class);

        Mockito.doReturn(board).when(boardView).getAssociatedBoard();
        Mockito.doReturn(task).when(serverUtils).addNewTask(any(Task.class));
        Mockito.doReturn(taskList).when(taskListView).getAssociatedTaskList();
        Mockito.doReturn(boardView).when(overviewController).getSelectedBoardView();

        overviewService.addNewTask(taskListView);

        verify(taskList).getId();
        verify(serverUtils).addNewTask(any(Task.class));
        verify(overviewController, times(2)).getSelectedBoardView();
        verify(boardView).getAssociatedBoard();
        verify(mainCtrl).sendToOthers(any());
    }

    @Test
    void getServerURL() {
        Mockito.doReturn("SOMEURL").when(serverUtils).getServer();
        assertEquals("SOMEURL", overviewService.getServerURL());
    }

    @Test
    void createNewBoard() {
        Board fakeBoard = new Board();
        fakeBoard.setId(10L);
        Mockito.doReturn(fakeBoard).when(serverUtils).saveBoard(any());
        var ret = overviewService.createNewBoard();
        verify(mainCtrl).rememberBoard(10L);
        verify(mainCtrl).saveStorage();
        assertEquals(ret, fakeBoard);
    }

    @Test
    void getAllBoardsOK(){
        var allBoards = Mockito.mock(ArrayList.class);
        Mockito.doReturn(allBoards).when(serverUtils).getAllBoards();
        var ret = overviewService.getAllBoards();
        assertEquals(allBoards, ret);
    }

    @Test
    void getAllBoardsFail(){
        Mockito.doReturn(null).when(serverUtils).getAllBoards();
        assertThrows(IllegalArgumentException.class, () -> {
            overviewService.getAllBoards();
        });
    }

    @Test
    void checkAdminPassword() {
        Mockito.doReturn(true).when(serverUtils).checkAdminPassword("123");
        boolean ret = overviewService.checkAdminPassword("123");
        assertEquals(true, ret);
    }

    @Test
    void copyID() {
        Board board = new Board();
        board.setUserId("123");
        board.setId(123);
        Clipboard fakeClipboard = Mockito.mock(Clipboard.class);
        Mockito.doReturn(1).when(overviewController).getNumberOfTimesCopied();
        Mockito.doNothing().when(mainCtrl).setClipboardContent(any(String.class));
        Mockito.doNothing().when(overviewController).showIDCopiedMessage();
        overviewService.copyID(board);
        Mockito.verify(mainCtrl).setClipboardContent("123");
        Mockito.verify(overviewController).setNumberOfTimesCopied(2);
    }
    @Test
    void testSaveStorage(){
        Mockito.doNothing().when(mainCtrl).saveStorage();
        overviewService.saveStorage();
        verify(mainCtrl, times(1)).saveStorage();
    }
    @Test
    void testDisconnect(){
        Mockito.doNothing().when(mainCtrl).showServerConnect();
        Mockito.doNothing().when(mainCtrl).loadServers();
        overviewService.disconnectClicked();
        verify(mainCtrl, times(1)).showServerConnect();
        verify(mainCtrl, times(1)).loadServers();
    }
    @Test
    void testClearMaps(){
        Mockito.doNothing().when(mainCtrl).clearBoardViewMap();
        Mockito.doNothing().when(mainCtrl).clearTaskListViewMap();
        overviewService.clearMaps();
        verify(mainCtrl, times(1)).clearBoardViewMap();
        verify(mainCtrl, times(1)).clearTaskListViewMap();
    }
    @Test
    void testRemoveBoardView(){
        Mockito.doNothing().when(mainCtrl).deleteBoardView(any(Long.class));
        overviewService.removeBoardView(5L);
        verify(mainCtrl, times(1)).deleteBoardView(5L);
    }
    @Test
    void testRemoveTaskListView(){
        Mockito.doNothing().when(mainCtrl).deleteTaskListView(any(Long.class));
        overviewService.removeTaskListView(5L);
        verify(mainCtrl, times(1)).deleteTaskListView(5L);
    }
    @Test
    void testRemoveRecentBoard(){
        Mockito.doNothing().when(serverUtils).removeBoard(any(Board.class));
        Mockito.doNothing().when(mainCtrl).removeRecentBoard(any(Long.class));
        Mockito.doNothing().when(mainCtrl).saveStorage();
        Board toBeDeleted = new Board();
        toBeDeleted.setId(5L);
        overviewService.deleteRecentBoard(toBeDeleted);
        verify(serverUtils, times(1)).removeBoard(toBeDeleted);
        verify(mainCtrl, times(1)).removeRecentBoard(5L);
        verify(mainCtrl, times(1)).saveStorage();
    }
    @Test
    void testUpdateTitle(){
        BoardView mockBoardView = Mockito.mock(BoardView.class);
        Board mockBoard = new Board();
        Mockito.doReturn(mockBoardView).when(overviewController).getSelectedBoardView();
        Mockito.doReturn(mockBoard).when(mockBoardView).getAssociatedBoard();
        mockBoard.setTitle("new");
        Mockito.doReturn(mockBoard).when(serverUtils).saveBoard(mockBoard);
        Mockito.doNothing().when(mockBoardView).updateOverview(any(Board.class));
        Mockito.doNothing().when(mainCtrl).sendToOthers(any(Board.class));
        overviewService.updateTitle("new", mockBoardView);
        verify(mockBoardView, times(1)).getAssociatedBoard();
        verify(serverUtils, times(1)).saveBoard(mockBoard);
        verify(mockBoardView, times(1)).updateOverview(mockBoard);
        verify(mainCtrl, times(1)).sendToOthers(mockBoard);
        assertEquals("new", mockBoard.getTitle());
    }
    @Test
    void showColorManagement(){
        BoardView mockBoardView = Mockito.mock(BoardView.class);
        Mockito.doReturn(new Board()).when(mockBoardView).getAssociatedBoard();
        overviewService.showColorManagement(mockBoardView);
        verify(mockBoardView, times(1)).getAssociatedBoard();
        verify(mainCtrl, times(1)).showColorManagement(any(Board.class));
    }
    @Test
    void testCreateBoardView(){
        Board board = new Board();
        try(MockedConstruction<BoardView> BoardViewClass = Mockito.mockConstruction(BoardView.class)) {
            overviewService.createBoardView(board);
            assertEquals(1, BoardViewClass.constructed().size());
        }
    }
    @Test
    void testCreateWorkspace(){
        Mockito.doReturn(5D).when(overviewController).getTabPaneHeight();
        Mockito.doReturn(5D).when(overviewController).getTabPaneWidth();
        try(MockedConstruction<Workspace> WorkspaceClass = Mockito.mockConstruction(Workspace.class)) {
            overviewService.createWorkspace();
            assertEquals(1, WorkspaceClass.constructed().size());
        }
    }

}