package client.scenes;

import client.components.TaskListView;
import client.service.RenameTaskListService;
import commons.TaskList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.inject.Inject;

public class RenameTaskListController {

    private final RenameTaskListService renameTaskListService;
    private TaskList taskList;
    private Stage popup;
    @FXML
    private TextArea textArea;

    /**
     * Injector constructor
     *
     * @param renameTaskListService used for logic handling
     */
    @Inject
    public RenameTaskListController(RenameTaskListService renameTaskListService) {
        this.renameTaskListService = renameTaskListService;
        renameTaskListService.setRenameTaskListController(this);
    }

    /**
     * Rename method that saves the new title of the task list. It saves the new name
     * (by calling the service) and closes the popup.
     * It closes the popup stage after saved
     */
    @FXML
    private void renameClicked() {
        renameTaskListService.rename(taskList, textArea.getText());
        this.popup.close();
    }

    /**
     * Refresh the popup
     *
     * @param taskListView task list to refresh
     * @param popup    the current popup stage
     */
    public void refresh(TaskListView taskListView, Stage popup) {
        this.taskList = taskListView.getAssociatedTaskList();
        this.popup = popup;
        textArea.setText(taskList.getTitle());
    }
}
