package client.service;

import client.components.TaskView;
import client.scenes.EditSubtaskController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.SubTask;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

public class EditSubtaskServiceTest {
    @InjectMocks
    EditSubtaskService editSubtaskService;

    @Mock
    EditSubtaskController editSubtaskController;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    ServerUtils serverUtils;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        editSubtaskService.setEditSubtaskController(editSubtaskController);
    }
    @Test
    void testUpdateSubtask() {

        SubTask currentSubtask = new SubTask();
        SubTask expectedSubtask = new SubTask();
        currentSubtask.setDescription("old");
        expectedSubtask.setDescription("new");

        Task task = new Task();
        ArrayList<SubTask> subtasks = new ArrayList<>();
        subtasks.add(currentSubtask);
        task.setSubTasks(subtasks);
        task.setTags(new ArrayList<>());

        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.doReturn(currentSubtask).when(editSubtaskController).getCurrentSubtask();
        Mockito.doReturn(taskView).when(editSubtaskController).getTaskView();
        Mockito.doReturn(task).when(taskView).getAssociatedTask();
        Mockito.doReturn(null).when(serverUtils).saveSubTask(any());
        Mockito.doNothing().when(taskView).updateOverview(any());


        editSubtaskService.updateSubtask("new");

        Mockito.verify(taskView).updateOverview(any());
        Mockito.verify(mainCtrl).sendToOthers(task);
    }
}
