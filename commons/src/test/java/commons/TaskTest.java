package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task;
    SubTask subTask;

    @BeforeEach
    void setUp(){
        task = new Task();
        subTask = new SubTask();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
    }
    @Test
    void testAddSubTask(){
        task.addSubtask(subTask);
        assertEquals(subTask, task.getSubTasks().get(0));
        subTask.setDescription("Differences");
        task.addSubtask(subTask);
        assertEquals(1, task.getSubTasks().size());
        SubTask anotherSubTask = new SubTask();
        anotherSubTask.setId(1);
        task.addSubtask(anotherSubTask);
        assertEquals(2, task.getSubTasks().size());
    }

    @Test
    void testDeleteSubtask(){
        task.addSubtask(subTask);
        task.deleteSubTask(subTask);
        assertEquals(0, task.getSubTasks().size());
    }

    @Test
    void testInsertOrReplace(){
        task.insertOrReplace(subTask);
        assertEquals(1, task.getSubTasks().size());
        SubTask newSubTask = new SubTask(subTask);
        task.insertOrReplace(newSubTask);
        assertEquals(1, task.getSubTasks().size());
        SubTask different = new SubTask();
        different.setId(1);
        task.insertOrReplace(different);
        assertEquals(2, task.getSubTasks().size());
        assertEquals(subTask, task.getSubTasks().get(0));
        assertEquals(different, task.getSubTasks().get(1));
    }

    @Test
    void testInsertInSubTaskList(){
        task.addSubtask(subTask);
        SubTask newSubTask = new SubTask();
        subTask.setId(1);
        task.insertInSubTaskList(newSubTask, subTask, 1);
        assertEquals(2, task.getSubTasks().size());
        assertEquals(subTask, task.getSubTasks().get(0));
        assertEquals(newSubTask, task.getSubTasks().get(1));
    }

    @Test
    void testCloneConstructor(){
        Task anotherTask = new Task(task);
        assertEquals(task, anotherTask);
        assertNotSame(task, anotherTask);
    }
}
