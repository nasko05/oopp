package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
class TaskListTest {

    TaskList taskList;
    Task task;

    @BeforeEach
    void setUp(){
        taskList = new TaskList();
        task = new Task();
        taskList.setTasks(new ArrayList<>());
    }

    @Test
    void testAddTask(){
        taskList.addTask(task);
        assertEquals(task, taskList.getTasks().get(0));
        assertEquals(1, taskList.getTasks().size());
        task.setDescription("Something to differentiate");
        taskList.addTask(task);
        assertEquals(1, taskList.getTasks().size());
        Task anotherTask = new Task();
        anotherTask.setId(1);
        taskList.addTask(anotherTask);
        assertEquals(2, taskList.getTasks().size());
        assertEquals(anotherTask, taskList.getTasks().get(1));
    }

    @Test
    void testCloneConstructor(){
        TaskList differentTaskList = new TaskList(taskList);
        assertEquals(taskList, differentTaskList);
        assertNotSame(taskList, differentTaskList);
    }
}
