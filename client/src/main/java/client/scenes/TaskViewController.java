package client.scenes;

import client.components.TaskView;
import client.service.TaskViewService;
import commons.ColorEntity;
import commons.SubTask;
import client.utils.ServerUtils;
import commons.Tag;
import commons.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;

public class TaskViewController {
    private final MainCtrl mainCtrl;
    private final TaskViewService taskViewService;
    private TaskView currentTaskView;
    private String backGroundColor;
    private String fontColor;
    private final ServerUtils serverUtils;
    @FXML
    private VBox root;
    private Stage addSubtaskWindow;
    private TextField title;
    private TextArea description;
    private boolean writeAccess;

    /**
     * Injector constructor
     *
     * @param mainCtrl        it is used for overall control over the application
     * @param taskViewService is used for the logic handling
     * @param serverUtils     is used for connection to the server
     */
    @Inject
    public TaskViewController(MainCtrl mainCtrl,
                              TaskViewService taskViewService, ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.taskViewService = taskViewService;
        this.serverUtils = serverUtils;
        taskViewService.setTaskViewController(this);
    }

    /**
     * Returns the task view that is currently displayed on the board
     *
     * @return current task view
     */
    public TaskView getCurrentTaskView() {
        return currentTaskView;
    }

    /**
     * Refreshes the task details of a given task
     *
     * @param task the task to display in the scene
     */
    public void displayTask(Task task) {
        if (task == null) return;
        currentTaskView = new TaskView(mainCtrl, task, this);
        root.getChildren().clear();
        root.getChildren().add(currentTaskView);
        title = (TextField) root.lookup("#titleField");
        description = (TextArea) root.lookup("#descriptionField");
        backGroundColor = task.getBackGroundColor();
        fontColor = task.getFontColor();
    }
    /**
     * Sends the added tag to the service
     * @param taskView the taskView to which the tag is added
     */
    public void addTagClicked(TaskView taskView) {
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        Tag tag = taskViewService.addTagClicked();
        Task task = new Task(this.currentTaskView.getAssociatedTask());
        task.getTags().add(tag);
        task = serverUtils.saveTaskByID(task);
        taskView.updateOverview(task);
        this.updateOnChange();
        mainCtrl.sendToOthers(task);
        mainCtrl.showEditTagScene(tag, this, null);
    }

    /**
     * Sends the removed tag to the service
     * @param tag the tag that will be removed
     */
    public void removeTagClicked(Tag tag) {
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        taskViewService.deleteTag(tag);
    }

    /**
     * Displays the addSubTask popup window
     */
    @FXML
    public void addSubTaskClicked() {
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        Stage popUpWindow = new Stage();
        this.addSubtaskWindow = popUpWindow;
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Create Sub Task");
        popUpWindow.setScene(mainCtrl.getAddSubTask());
        popUpWindow.showAndWait();
        this.updateOnChange();
    }

    /**
     * Closes the popped-up window, that was created to add a new subtask
     */
    public void closeAddSubtaskWindow() {
        if(addSubtaskWindow != null && addSubtaskWindow.isShowing()){
            addSubtaskWindow.close();
        }
    }

    /**
     * Handles removing a certain subtask
     * @param subTask the subtask to be deleted
     */
    public void deleteSubTaskClicked(SubTask subTask) {
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        taskViewService.deleteSubtask(subTask);
    }

    /**
     * Whenever an action happens on the screen (for instance, a key is pressed), this takes the
     * values from the text fields and saves the current state of the task.
     * It also first saves the cursor positions
     * @param title new title
     * @param description new description
     */
    public void updateOnChange(String title, String description) {
        taskViewService.saveTask(title, description, backGroundColor, fontColor,
                currentTaskView.getAssociatedTask().getSubTasks());
        serverUtils.stopLongPolling();
    }
    /**
     * Whenever an action happens on the screen (for instance, a key is pressed), this takes the
     * values from the text fields and saves the current state of the task.
     * It also first saves the cursor positions
     */
    public void updateOnChange() {
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        taskViewService.saveTask(title.getText(), description.getText(), backGroundColor, fontColor,
            currentTaskView.getAssociatedTask().getSubTasks());
    }

    /**
     * Getter for current task
     *
     * @return the current task
     */
    public Task getCurrentTask(){
        return currentTaskView.getAssociatedTask();
    }

    /**
     * Save the change when color preset are selected
     *
     * @param colorEntity the new color entity
     */
    public void changeColorPreset(ColorEntity colorEntity){
        if(!writeAccess){
            showError("You do not have the right write access");
            return;
        }
        backGroundColor = colorEntity.getBackGroundColor();
        fontColor = colorEntity.getFontColor();
        this.updateOnChange();
    }

    /**
     * Setter for write access
     * @param writeAccess permission level
     */
    public void setWriteAccess(boolean writeAccess) {
        this.writeAccess = writeAccess;
    }

    /**
     * Getter
     * @return write rights
     */
    public boolean isWriteAccess() {
        return writeAccess;
    }

    /**
     * Method that shows error Alert box
     * @param text Error message
     */
    public void showError(String text) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setContentText(text);
        Platform.runLater(error::showAndWait);
    }
}
