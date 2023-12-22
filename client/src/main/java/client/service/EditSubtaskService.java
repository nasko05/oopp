package client.service;

import client.components.TaskView;
import client.scenes.EditSubtaskController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.SubTask;
import commons.Task;

import javax.inject.Inject;

public class EditSubtaskService {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;

    private EditSubtaskController editSubtaskController;

    /**
     * Constructor (injectable)
     * @param mainCtrl the main controller to inject
     * @param serverUtils the server utilities to inject
     */
    @Inject
    public EditSubtaskService(MainCtrl mainCtrl, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * Setter for the controller
     * @param editSubtaskController the subtask controller to inject
     */
    public void setEditSubtaskController(EditSubtaskController editSubtaskController) {
        this.editSubtaskController = editSubtaskController;
    }

    /**
     * Updates a subtask with the given description
     * @param newDescription the new description for the subtask
     */
    public void updateSubtask(String newDescription) {
        SubTask currentSubtask = editSubtaskController.getCurrentSubtask();
        TaskView taskView = editSubtaskController.getTaskView();
        currentSubtask.setDescription(newDescription);
        serverUtils.saveSubTask(currentSubtask);
        Task task = new Task(taskView.getAssociatedTask());
        for(int i = 0; i < task.getSubTasks().size(); ++i){
            var curr = task.getSubTasks().get(i);
            if(curr.getId().equals(currentSubtask.getId())){
                task.getSubTasks().set(i, currentSubtask);
            }
        }
        taskView.updateOverview(task);
        editSubtaskController.closeWindow();
        mainCtrl.sendToOthers(task);
    }
}
