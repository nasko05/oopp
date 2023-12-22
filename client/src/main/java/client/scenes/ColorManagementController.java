package client.scenes;

import client.components.ColorPresetView;
import client.service.ColorManagementService;
import commons.Board;
import commons.ColorEntity;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.inject.Inject;

public class ColorManagementController {

    private final ColorManagementService colorManagementService;
    private String boardBgColor;
    private String boardFontColor;
    private String taskListsBgColor;
    private String taskListsFontColor;
    private ColorEntity defaultTaskColor;
    @FXML
    private ColorPicker boardBgColorPicker;
    @FXML
    private ColorPicker boardFontColorPicker;
    @FXML
    private Circle boardColorCircle;
    @FXML
    private Label boardFontColorLabel;
    @FXML
    private ColorPicker taskListsBgColorPicker;
    @FXML
    private ColorPicker taskListsFontColorPicker;
    @FXML
    private Circle taskListsColorCircle;
    @FXML
    private Label taskListsFontColorLabel;
    @FXML
    private ColorPicker taskDefaultBgColorPicker;
    @FXML
    private ColorPicker taskDefaultFontColorPicker;
    @FXML
    private VBox taskPresetsContainer;
    private Board associatedBoard;

    /**
     * Injector constructor
     *
     * @param colorManagementService used for logic handling
     */
    @Inject
    public ColorManagementController(ColorManagementService colorManagementService) {
        this.colorManagementService = colorManagementService;
        colorManagementService.setColorManagementController(this);
    }

    /**
     * Display the color view for the associated board
     *
     * @param board the associated board
     */
    public void displayColorView(Board board){
        if (board == null) return;

        this.associatedBoard = board;

        boardBgColor = board.getBoardBgColor();
        boardFontColor = board.getBoardFontColor();
        taskListsBgColor = board.getTaskListsBgColor();
        taskListsFontColor = board.getTaskListsFontColor();
        defaultTaskColor = board.getTaskDefaultColor();

        boardColorCircle.setFill(Color.web(board.getBoardBgColor()));
        boardFontColorLabel.setTextFill(Color.web(board.getBoardFontColor()));
        boardBgColorPicker.setValue(Color.web(board.getBoardBgColor()));
        boardFontColorPicker.setValue(Color.web(board.getBoardFontColor()));

        taskListsColorCircle.setFill(Color.web(board.getTaskListsBgColor()));
        taskListsFontColorLabel.setTextFill(Color.web(board.getTaskListsFontColor()));
        taskListsBgColorPicker.setValue(Color.web(board.getTaskListsBgColor()));
        taskListsFontColorPicker.setValue(Color.web(board.getTaskListsFontColor()));

        taskDefaultBgColorPicker.setValue(Color.web(defaultTaskColor.getBackGroundColor()));
        taskDefaultFontColorPicker.setValue(Color.web(defaultTaskColor.getFontColor()));

        taskPresetsContainer.getChildren().clear();
        for(ColorEntity colorEntity: board.getTaskColorPresets()){
            var colorPresetView = new ColorPresetView(colorEntity, this);
            taskPresetsContainer.getChildren().add(colorPresetView);
        }
    }

    /**
     * Update the color presets of the associated board
     *
     * @param board the board with new color presets
     */
    public void updateColorPresets(Board board){
        this.associatedBoard.setTaskColorPresets(board.getTaskColorPresets());
        taskPresetsContainer.getChildren().clear();
        for(ColorEntity colorEntity: board.getTaskColorPresets()){
            var colorPresetView = new ColorPresetView(colorEntity, this);
            taskPresetsContainer.getChildren().add(colorPresetView);
        }
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void boardBgColorChanged(){
        boardColorCircle.setFill(boardBgColorPicker.getValue());
        String originColor = boardBgColorPicker.getValue().toString();
        boardBgColor = "#" + originColor.substring(2, 8);
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void boardFontColorChanged(){
        boardFontColorLabel.setTextFill(boardFontColorPicker.getValue());
        String originColor = boardFontColorPicker.getValue().toString();
        boardFontColor = "#" + originColor.substring(2, 8);
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * Color reset to default when 'reset' button is clicked.
     */
    public void resetBoardColorClicked(){
        boardBgColor = "#f2f2f2";
        boardFontColor = "#000000";
        boardColorCircle.setFill(Color.web(boardBgColor));
        boardFontColorLabel.setTextFill(Color.web(boardFontColor));
        boardFontColorPicker.setValue(Color.web(boardFontColor));
        boardBgColorPicker.setValue(Color.web(boardBgColor));
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void taskListsBgColorChanged(){
        taskListsColorCircle.setFill(taskListsBgColorPicker.getValue());
        String originColor = taskListsBgColorPicker.getValue().toString();
        taskListsBgColor = "#" + originColor.substring(2, 8);
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void taskListsFontColorChanged(){
        taskListsFontColorLabel.setTextFill(taskListsFontColorPicker.getValue());
        String originColor = taskListsFontColorPicker.getValue().toString();
        taskListsFontColor =  "#" + originColor.substring(2,8);
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * TaskLists color reset to default when 'reset' button is clicked.
     */
    public void resetTaskListsColorClicked(){
        taskListsBgColor = "#e6e6e6";
        taskListsFontColor = "#000000";
        taskListsColorCircle.setFill(Color.web(taskListsBgColor));
        taskListsFontColorLabel.setTextFill(Color.web(taskListsFontColor));
        taskListsFontColorPicker.setValue(Color.web(taskListsFontColor));
        taskListsBgColorPicker.setValue(Color.web(taskListsBgColor));
        colorManagementService.saveChange(boardBgColor,boardFontColor,
                taskListsBgColor, taskListsFontColor, defaultTaskColor);
    }

    /**
     * A color preset is added when the add icon is clicked.
     */
    public void addColorPresetClicked(){
        colorManagementService.addColorPresetClicked(associatedBoard);
    }

    /**
     * Preset is deleted from a board.
     *
     * @param colorEntity the color preset to delete
     */
    public void deletePresetClicked(ColorEntity colorEntity) {
        colorManagementService.deletePresetClicked(colorEntity, associatedBoard);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     *
     * @param associatedPreset the associated preset
     */
    public void presetColorChanged(ColorEntity associatedPreset) {
        colorManagementService.presetColorChanged(associatedBoard, associatedPreset);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void defaultTaskBgColorChanged(){
        String originColor = taskDefaultBgColorPicker.getValue().toString();
        defaultTaskColor.setBackGroundColor("#" + originColor.substring(2, 8));
    }

    /**
     * Color changed and stored corresponding to the color picker.
     */
    public void defaultTaskFontColorChanged(){
        String originColor = taskDefaultFontColorPicker.getValue().toString();
        defaultTaskColor.setFontColor("#" + originColor.substring(2, 8));
    }

    /**
     * When the "close" button is clicked, new color values are stored
     * in the associated board.
     */
    public void close(){
        ((Stage)boardBgColorPicker.getScene().getWindow()).close();
    }

    /**
     * Return the associated board for the color view
     * @return associated board
     */
    public Board getAssociatedBoard(){
        return associatedBoard;
    }
}
