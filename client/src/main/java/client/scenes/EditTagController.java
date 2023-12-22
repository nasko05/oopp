package client.scenes;

import client.components.TaskView;
import client.service.EditTagService;
import com.google.inject.Inject;
import commons.Tag;
import commons.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EditTagController {
    private final EditTagService editTagService;
    private Stage stage;
    private TaskView taskView;
    private Tag currentTag;
    @FXML
    private TextArea editTagDescription;
    @FXML
    private ColorPicker tagColorPicker;
    @FXML
    private ColorPicker tagFontColorPicker;
    private Task associatedTask;

    /**
     * Constructor
     * @param editTagService instance of editTag
     */
    @Inject
    public EditTagController(EditTagService editTagService) {
        this.editTagService = editTagService;
        editTagService.setEditTagController(this);
    }

    /**
     * Setter for TaskView
     * @param taskView current TaskView
     */
    public void setTaskView(TaskView taskView) {
        this.taskView = taskView;
    }

    /**
     * Sets the new value for the associated task
     * @param associatedTask the new associated task
     */
    public void setAssociatedTask(Task associatedTask) {
        this.associatedTask = associatedTask;
    }

    /**
     * Displays the current attributes of the tag when the editTag view is open
     * @param tag to be displayed
     */
    public void displayTag(Tag tag) {
        currentTag = tag;
        editTagDescription.setText(tag.getDescription());
        tagColorPicker.setValue(Color.web(tag.getTagColor()));
        tagFontColorPicker.setValue(Color.web(tag.getTagFontColor()));
    }

    /**
     * Saves the new edits of the tag
     */
    public void save(){
        if(!editTagDescription.getText().equals("")) {
            editTagService.save(taskView, currentTag, editTagDescription.getText(),
                    "#" + tagColorPicker.getValue().toString().substring(2,8),
                    "#" + tagFontColorPicker.getValue().toString().substring(2,8),
                    associatedTask);
            stage.close();
        } else {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setContentText("Please enter description");
            box.showAndWait();
        }
    }

    /**
     * Displays an error, if the AddTagService failed to add a task
     */
    public void displayFailedToSaveTag() {
        Alert box = new Alert(Alert.AlertType.ERROR);
        box.setContentText("Couldn't save the given tag");
        box.showAndWait();
    }

    /**
     * Setter
     * @param stage current stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
