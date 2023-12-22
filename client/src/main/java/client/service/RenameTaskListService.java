package client.service;

import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import client.scenes.RenameTaskListController;
import client.utils.ServerUtils;
import commons.Board;
import commons.TaskList;

import javax.inject.Inject;

public class RenameTaskListService {
    private final ServerUtils serverUtils;
    private final OverviewController overviewController;
    private final MainCtrl mainCtrl;
    private RenameTaskListController renameTaskListController;

    /**
     * An injectable constructor
     *
     * @param serverUtils        server utilities
     * @param overviewController the overview controller
     * @param mainCtrl           the main controller
     */
    @Inject
    public RenameTaskListService(ServerUtils serverUtils,
                                 OverviewController overviewController,
                                 MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.overviewController = overviewController;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Takes a taskList object that needs to be renamed, takes its new name and renames it.
     * Then calls the respective refresh methods.
     *
     * @param taskList the taskList that needs to be renamed
     * @param name     the new name of the given taskList
     */
    public void rename(TaskList taskList, String name) {
        TaskList updatedTaskList = new TaskList(taskList);
        updatedTaskList.setTitle(name);
        updatedTaskList = serverUtils.saveTaskList(updatedTaskList);
        Board updatedBoard = new Board(
            overviewController.getSelectedBoardView().getAssociatedBoard());
        updatedBoard.addNewTaskList(updatedTaskList);
        overviewController.getSelectedBoardView().updateOverview(updatedBoard);
        mainCtrl.sendToOthers(updatedTaskList);
    }

    /**
     * A setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param renameTaskListController the controller to which this service belongs
     */
    public void setRenameTaskListController(RenameTaskListController renameTaskListController) {
        this.renameTaskListController = renameTaskListController;
    }

    /**
     * A getter for the controller. This is required for testing.
     * @return the renameTaskList controller
     */
    public RenameTaskListController getRenameTaskListController() {
        return renameTaskListController;
    }
}
