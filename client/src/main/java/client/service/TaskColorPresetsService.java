package client.service;

import client.scenes.MainCtrl;
import client.scenes.TaskColorPresetsController;
import client.utils.ServerUtils;
import commons.Board;
import commons.ColorEntity;
import commons.Task;
import commons.TaskList;

import javax.inject.Inject;

public class TaskColorPresetsService {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private TaskColorPresetsController taskColorPresetsController;

    /**
     * Injectable constructor
     * @param serverUtils the server utils
     * @param mainCtrl the main controller
     */
    @Inject
    public TaskColorPresetsService(ServerUtils serverUtils,
                                   MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setter for the controller
     * @param taskColorPresetsController the new controller
     */
    public void setTaskColorPresetsController(
            TaskColorPresetsController taskColorPresetsController) {
        this.taskColorPresetsController = taskColorPresetsController;
    }

    /**
     * Pops up the task color presets window and injects data to it
     * @param task the task that the presets are for
     */
    public void showWindow(Task task) {
        TaskList taskList = mainCtrl.getTaskListViewById(task.getTaskListID())
                .getAssociatedTaskList();
        Board board = mainCtrl.getBoardViewById(taskList.getBoardId())
                .getAssociatedBoard();
        taskColorPresetsController.insertPresets(board);
        mainCtrl.showTaskColorPresets();
    }

    /**
     * Called when some color preset has been selected. Updates the task,
     * saves it ands sends it to others
     * @param task the task in which the preset was selected
     * @param color the new color for the task
     */
    public void presetSelected(Task task, ColorEntity color) {
        Task newTask = new Task(task);
        newTask.setBackGroundColor(color.getBackGroundColor());
        newTask.setFontColor(color.getFontColor());

        TaskList taskList = mainCtrl.getTaskListViewById(task.getTaskListID())
                .getAssociatedTaskList();
        Board board = mainCtrl.getBoardViewById(taskList.getBoardId())
                .getAssociatedBoard();
        Board newBoard = new Board(board);
        newBoard.insertOrReplace(newTask);
        mainCtrl.getBoardViewById(board.getId()).updateOverview(newBoard);
        mainCtrl.sendToOthers(newTask);
        serverUtils.saveTaskByID(newTask);
    }

}
