package client.service;

import client.scenes.AddSubTaskController;
import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import client.utils.ServerUtils;
import commons.SubTask;
import commons.Task;

import javax.inject.Inject;

public class AddSubtaskService {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private final TaskViewController taskViewController;
    private AddSubTaskController addSubTaskController;

    /**
     * Injectable constructor
     *
     * @param mainCtrl           the main controller
     * @param serverUtils        server utilities
     * @param taskViewController the task view controller
     */
    @Inject
    public AddSubtaskService(MainCtrl mainCtrl, ServerUtils serverUtils,
                             TaskViewController taskViewController) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.taskViewController = taskViewController;
    }

    /**
     * setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param addSubTaskController the controller to which this service belongs
     */
    public void setAddSubTaskController(AddSubTaskController addSubTaskController) {
        this.addSubTaskController = addSubTaskController;
    }

    /**
     * getter for the controller. This is used for testing the setter.
     * @return the addSubtask controller
     */
    public AddSubTaskController getAddSubTaskController() {
        return addSubTaskController;
    }

    /**
     * Adds a new subtask, sends it to the server and refreshes the screen
     *
     * @param description the description of the new subtask
     */
    public void addNewSubtask(String description) {
        SubTask subTask = new SubTask();
        subTask.setDescription(description);
        subTask.setTaskId(taskViewController.getCurrentTask().getId());
        subTask = serverUtils.saveSubTask(subTask);
        if (description == null || subTask == null) {
            addSubTaskController.showError();
            return;
        }

        subTask.setTaskId(taskViewController.getCurrentTask().getId());

        Task currentTask = new Task(taskViewController.getCurrentTask());
        currentTask.getSubTasks().add(subTask);

        addSubTaskController.reset();
        taskViewController.closeAddSubtaskWindow();

        taskViewController.getCurrentTaskView().updateOverview(currentTask);
        mainCtrl.sendToOthers(currentTask);
    }
}
