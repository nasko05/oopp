package client.scenes;

import client.service.TaskColorPresetsService;
import commons.Board;
import commons.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.inject.Inject;
import java.util.Objects;

public class TaskColorPresetsController {
    private final TaskColorPresetsService taskColorPresetsService;
    private Task associatedTask;
    @FXML
    private ComboBox<Label> comboBox;
    /**
     * Injectable constructor
     * @param taskColorPresetsService the service for this controller
     */
    @Inject
    public TaskColorPresetsController(TaskColorPresetsService taskColorPresetsService) {
        this.taskColorPresetsService = taskColorPresetsService;
        taskColorPresetsService.setTaskColorPresetsController(this);
    }

    /**
     * Is called when C is clicked
     * @param task the task for which to display the presets
     */
    public void enableColorsShortcut(Task task) {
        if(task == null) return ;
        this.associatedTask = task;
        taskColorPresetsService.showWindow(task);
    }

    /**
     * Inserts the presets from the board into the combo box
     * @param associatedBoard the board that the task is in
     */
    public void insertPresets(Board associatedBoard) {
        comboBox.getItems().clear();
        for (int i = 0; i < associatedBoard.getTaskColorPresets().size(); i++){
            Label newLabel = new Label(i+1 + "");
            newLabel.setPrefWidth(100);
            newLabel.setAlignment(Pos.CENTER);
            newLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            newLabel.setStyle( "-fx-background-color: " +
                    associatedBoard.getTaskColorPresets().get(i).getBackGroundColor()
                    + ";-fx-text-fill:" +
                    associatedBoard.getTaskColorPresets().get(i).getFontColor()
            );
            comboBox.getItems().add(newLabel);
        }
        comboBox.setOnAction(event -> {
                if(comboBox.getValue() != null){
                    taskColorPresetsService.presetSelected(
                            associatedTask,
                            associatedBoard.getTaskColorPresets().
                                    get(Integer.parseInt(comboBox.getValue().getText())-1)
                    );
                }
            }
        );
        comboBox.setCellFactory(param -> new ListCell<>() {
            // Called whenever the content or state of the cell needs to be updated
            // 'item' is the label to be displayed in the cell,
            // 'empty' indicates whether the cell is empty.
            // Here we customized the view of cells in comboBox to keep it looks consistent.
            @Override
            protected void updateItem(Label item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if(item.getText() != null && !Objects.equals(item.getText(), "")) {
                        Label label = new Label(item.getText());
                        label.setPrefWidth(100);
                        label.setAlignment(Pos.CENTER);
                        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                        label.setTextFill(Color.web(associatedBoard.getTaskColorPresets().
                                get(Integer.parseInt(item.getText()) - 1).getFontColor()));
                        label.setStyle("-fx-background-color: " +
                                associatedBoard.getTaskColorPresets().
                                get(Integer.parseInt(item.getText()) - 1).getBackGroundColor());
                        setGraphic(label);
                    }
                }
            }
        });
    }

}
