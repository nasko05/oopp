package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    Board board;
    TaskList taskList;
    Task task;

    @BeforeEach
    void setUp(){
        board = new Board();
        taskList = new TaskList();
        task = new Task();
        board.setTaskLists(new ArrayList<>());
        board.setTaskColorPresets(new ArrayList<>());
        board.setTaskDefaultColor(new ColorEntity());
        taskList.setTasks(new ArrayList<>());
    }

    @Test
    void testGetTitle(){
        assertNull(board.getTitle());
    }

    @Test
    void testSetTitle(){
        board.setTitle("Title");
        assertEquals("Title", board.getTitle());
    }

    @Test
    void testAddNewTaskList(){
        TaskList added = board.addNewTaskList(taskList);
        assertEquals(taskList, added);
        assertEquals(1, board.getTaskLists().size());
        added = board.addNewTaskList(taskList);
        assertEquals(1, board.getTaskLists().size());
        assertEquals(added,taskList);
    }

    @Test
    void testRemoveTaskList(){
        board.addNewTaskList(taskList);
        TaskList removed = board.removeTaskList(taskList);
        assertEquals(taskList, removed);
        assertNull(taskList.getBoardId());
        assertEquals(0, board.getTaskLists().size());
        removed = board.removeTaskList(taskList);
        assertNull(removed);
    }

    @Test
    void testInsertOrReplace(){
        board.addNewTaskList(taskList);
        taskList.setBoardId(board.getId());
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        task.setSubTasks(new ArrayList<>());
        taskList.addTask(task);
        task.setTaskListID(taskList.getId());
        Task anotherTask = new Task(task);
        board.insertOrReplace(anotherTask);
        assertEquals(anotherTask, task);
        assertEquals(1, taskList.getTasks().size());
        Task differentTask = new Task();
        differentTask.setId(1);
        differentTask.setTaskListID(taskList.getId());
        board.insertOrReplace(differentTask);
        assertEquals(differentTask, taskList.getTasks().get(1));
        assertEquals(2, taskList.getTasks().size());
    }

    @Test
    void testGetTaskListById(){
        board.addNewTaskList(taskList);
        long id = taskList.getId();
        assertEquals(taskList, board.getTaskListByID(id));
        assertNull(board.getTaskListByID(id + 1));
    }

    @Test
    void testRemoveTask(){
        board.addNewTaskList(taskList);
        taskList.addTask(task);
        task.setTaskListID(taskList.getId());
        board.removeTask(task);
        assertEquals(1, board.getTaskLists().size());
        assertEquals(0, taskList.getTasks().size());
    }

    @Test
    void testInsertInList(){
        board.addNewTaskList(taskList);
        taskList.addTask(task);
        Task newTask = new Task();
        newTask.setTaskListID(taskList.getId());
        task.setTaskListID(taskList.getId());
        board.insertInList(newTask, task, 1);
        assertEquals(2, taskList.getTasks().size());
        assertEquals(task, taskList.getTasks().get(0));
        assertEquals(newTask, taskList.getTasks().get(1));
        Task anotherTask = new Task();
        anotherTask.setTaskListID(taskList.getId() + 1);
        board.insertInList(anotherTask, task, 1);
        assertEquals(2, taskList.getTasks().size());
        assertFalse(taskList.getTasks().contains(anotherTask));
    }

    @Test
    void testCloneConstructor(){
        Board differentBoard = new Board(board);
        assertEquals(board, differentBoard);
        assertNotSame(board, differentBoard);
    }
}