package client.scenes;

import client.service.AddSubtaskService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import javax.inject.Inject;

public class AddSubTaskController {

    private final AddSubtaskService addSubtaskService;
    @FXML
    private TextArea description;

    /**
     * Injector constructor
     *
     * @param addSubtaskService used for logic handling
     */
    @Inject
    public AddSubTaskController(AddSubtaskService addSubtaskService) {
        this.addSubtaskService = addSubtaskService;
        addSubtaskService.setAddSubTaskController(this);
    }

    /**
     * Save method that writes the tag to the database
     * using serverUtils to make POST request
     */
    @FXML
    private void addNewSubtaskClicked() {
        if(!description.getText().equals("")) {
            addSubtaskService.addNewSubtask(description.getText());
        } else {
            Alert box = new Alert(Alert.AlertType.ERROR);
            box.setContentText("Please enter description");
            box.showAndWait();
        }
    }
    /**
     * Resets the text field
     */
    public void reset() {
        description.setText("");
    }

    /**
     * Alerts and shows error
     */
    public void showError() {
        Alert box = new Alert(Alert.AlertType.ERROR);
        box.setContentText("Couldn't save the given sub task");
        box.showAndWait();
    }
}
