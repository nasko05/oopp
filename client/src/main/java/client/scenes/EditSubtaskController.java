package client.scenes;

import client.components.TaskView;
import client.service.EditSubtaskService;
import com.google.inject.Inject;
import commons.SubTask;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EditSubtaskController {

    private Stage stage;
    private TaskView taskView;
    private SubTask currentSubtask;
    @FXML
    private TextArea subtaskDescription;
    private final EditSubtaskService editSubtaskService;

    /**
     * Constructor
     * @param editSubtaskService the edit tag service
     */
    @Inject
    public EditSubtaskController(EditSubtaskService editSubtaskService) {
        this.editSubtaskService = editSubtaskService;
        editSubtaskService.setEditSubtaskController(this);
    }

    /**
     * Setter for TaskView
     * @param taskView current TaskView
     */
    public void setTaskView(TaskView taskView) {
        this.taskView = taskView;
    }

    /**
     * Displays the current attributes of the tag when the editTag view is open
     * @param subTask to be displayed
     */
    public void displaySubtask(SubTask subTask) {
        currentSubtask = subTask;
        subtaskDescription.setText(subTask.getDescription());
    }

    /**
     * Saves the new edits of the subtask
     */
    public void save(){
        editSubtaskService.updateSubtask(subtaskDescription.getText());
    }

    /**
     * Setter
     * @param stage current stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Getter for the current subtask
     * @return the current subtask
     */
    public SubTask getCurrentSubtask() {
        return currentSubtask;
    }

    /**
     * Closes the popup
     */
    public void closeWindow() {
        stage.close();
    }

    /**
     * Getter for task view
     * @return the task view
     */
    public TaskView getTaskView() {
        return taskView;
    }
}
