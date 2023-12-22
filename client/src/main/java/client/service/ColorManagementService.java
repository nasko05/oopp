package client.service;

import client.components.BoardView;
import client.scenes.ColorManagementController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.Board;
import commons.ColorEntity;

import javax.inject.Inject;

public class ColorManagementService {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private ColorManagementController colorManagementController;

    /**
     * An injectable constructor
     *
     * @param serverUtils        server utilities
     * @param mainCtrl           the main controller
     */
    @Inject
    public ColorManagementService(ServerUtils serverUtils,
                                 MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param colorManagementController the color management controller
     */
    public void setColorManagementController(ColorManagementController colorManagementController){
        this.colorManagementController = colorManagementController;
    }

    /**
     * Getter for the controller. This is required for testing.
     *
     * @return the color management controller
     */
    public ColorManagementController getColorManagementController() {
        return colorManagementController;
    }

    /**
     * A new color preset is created
     *
     * @param associatedBoard the associated board
     */
    public void addColorPresetClicked(Board associatedBoard){
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setFontColor("#000000");
        colorEntity.setBackGroundColor("#cccccc");
        Board board = new Board(associatedBoard);
        board.getTaskColorPresets().add(colorEntity);
        serverUtils.saveBoard(board);
        colorManagementController.updateColorPresets(board);

        mainCtrl.sendToOthers(board);
    }

    /**
     * The color preset is removed
     *
     * @param colorEntity the color preset to remove
     * @param associatedBoard the associated board
     */
    public void deletePresetClicked(ColorEntity colorEntity, Board associatedBoard) {
        Board board = new Board(associatedBoard);
        board.getTaskColorPresets().remove(colorEntity);
        serverUtils.saveBoard(board);
        colorManagementController.updateColorPresets(board);

        mainCtrl.sendToOthers(board);
    }

    /**
     * Color changed and stored corresponding to the color picker.
     *
     * @param associatedBoard the associated board
     * @param associatedPreset the associated preset
     */
    public void presetColorChanged(Board associatedBoard, ColorEntity associatedPreset) {
        Board board = new Board(associatedBoard);
        for(int i = 0; i < associatedBoard.getTaskColorPresets().size(); i++){
            if (associatedBoard.getTaskColorPresets().get(i).getId() == associatedPreset.getId()){
                associatedBoard.getTaskColorPresets().set(i,associatedPreset);
            }
        }
        serverUtils.saveBoard(board);
        colorManagementController.updateColorPresets(board);

        mainCtrl.sendToOthers(board);
    }

    /**
     * Save the change of the changed colors to the corresponding board.
     *
     * @param boardBgColor background color of board
     * @param boardFontColor font color of board
     * @param taskListsBgColor background color of task lists
     * @param taskListsFontColor font color of task lists
     * @param defaultTaskColor default task color of a board
     */
    public void saveChange(String boardBgColor, String boardFontColor,
                           String taskListsBgColor, String taskListsFontColor,
                           ColorEntity defaultTaskColor){
        Board currentBoard = serverUtils.getBoardByID(colorManagementController
                .getAssociatedBoard().getId());
        currentBoard.setBoardBgColor(boardBgColor);
        currentBoard.setBoardFontColor(boardFontColor);
        currentBoard.setTaskListsBgColor(taskListsBgColor);
        currentBoard.setTaskListsFontColor(taskListsFontColor);
        currentBoard.setTaskDefaultColor(defaultTaskColor);
        currentBoard = serverUtils.saveBoard(currentBoard);

        BoardView boardView = mainCtrl.getBoardViewById(currentBoard.getId());
        boardView.updateOverview(currentBoard);

        mainCtrl.sendToOthers(currentBoard);
    }


}
