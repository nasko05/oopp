package client.service;

import client.components.TaskView;
import client.scenes.AddSubTaskController;
import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import client.utils.ServerUtils;
import commons.SubTask;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class AddSubtaskServiceTest {

    AddSubtaskService addSubtaskService;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils serverUtils;
    TaskViewController taskViewController;
    @Mock
    AddSubTaskController addSubTaskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskViewController = Mockito.spy(new TaskViewController(mainCtrl,
                Mockito.mock(TaskViewService.class), new ServerUtils()));
        addSubtaskService = new AddSubtaskService(mainCtrl, serverUtils, taskViewController);
        addSubtaskService.setAddSubTaskController(addSubTaskController);
    }

    @Test
    void getterAndSetterTest() {
        AddSubTaskController mockCtrl = Mockito.mock(AddSubTaskController.class);
        addSubtaskService.setAddSubTaskController(mockCtrl);

        assertEquals(mockCtrl, addSubtaskService.getAddSubTaskController());
    }

    @Test
    void addNewSubtaskFailedTest() {
        Mockito.doNothing().when(addSubTaskController).showError();
        Mockito.doReturn(new Task()).when(taskViewController).getCurrentTask();
        addSubtaskService.addNewSubtask(null);
        verify(addSubTaskController).showError();
    }

    @Test
    void addNewSubtaskSucceedTest() {
        SubTask fakeSubtask = new SubTask();
        fakeSubtask.setId(10);
        Mockito.doReturn(fakeSubtask).when(serverUtils).saveSubTask(any(SubTask.class));

        Task task = new Task();
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        Mockito.doReturn(task).when(taskViewController).getCurrentTask();
        Mockito.doReturn(task).when(serverUtils).saveTaskByID(task);

        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.doReturn(taskView).when(taskViewController).getCurrentTaskView();
        Mockito.doNothing().when(taskViewController).closeAddSubtaskWindow();
        addSubtaskService.addNewSubtask("a new subtask");

        verify(taskViewController).closeAddSubtaskWindow();
        verify(taskViewController).getCurrentTaskView();
        verify(mainCtrl).sendToOthers(any());
    }
}