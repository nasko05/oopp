package client.service;

import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import client.utils.ServerUtils;
import commons.*;

import javax.inject.Inject;
import java.util.List;

public class TaskViewService {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private TaskViewController taskViewController;

    /**
     * An injectable constructor
     *
     * @param mainCtrl           the main controller
     * @param serverUtils        the server utilities
     */
    @Inject
    public TaskViewService(MainCtrl mainCtrl,
                           ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * A getter for the controller
     * @return the getTaskView Controller
     */
    public TaskViewController getTaskViewController() {
        return taskViewController;
    }

    /**
     * A setter for the controller
     *
     * @param taskViewController the task view controller
     */
    public void setTaskViewController(TaskViewController taskViewController) {
        this.taskViewController = taskViewController;
    }

    /**
     * Given a task, saves it in the back-end, and refreshes the screen
     *
     * @param title       the new title of the task
     * @param description the new description of the task
     * @param backGroundColor    the new background color of the task
     * @param fontColor   the new font color of the task
     * @param subTasks    the subtasks in this task
     */
    public void saveTask(String title, String description, String backGroundColor, String fontColor,
                         List<SubTask> subTasks) {
        Task currentTask = new Task(taskViewController.getCurrentTask());
        var updated = serverUtils.getTaskByID(currentTask.getId());
        if(updated == null){
            return;
        }
        currentTask.setTaskListID(updated.getTaskListID());
        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setBackGroundColor(backGroundColor);
        currentTask.setFontColor(fontColor);
        currentTask.setSubTasks(subTasks);
        currentTask = serverUtils.saveTaskByID(currentTask);

        var boardView = mainCtrl.getBoardViewFromTask(currentTask);
        Board updatedBoard = new Board(boardView.getAssociatedBoard());
        updatedBoard.insertOrReplace(currentTask);
        boardView.updateOverview(updatedBoard);
        taskViewController.getCurrentTaskView().updateOverview(currentTask);

        mainCtrl.sendToOthers(currentTask);
    }

    /**
     * Deletes a given subtask
     * @param subTask the subtask to be deleted
     */
    public void deleteSubtask(SubTask subTask) {
        Task currentTask = new Task(taskViewController.getCurrentTask());
        currentTask.deleteSubTask(subTask);

        currentTask = serverUtils.saveTaskByID(currentTask);
        taskViewController.getCurrentTaskView().updateOverview(currentTask);

        mainCtrl.sendToOthers(currentTask);
    }

    /**
     * Method that deletes a tag from server
     * @param tag to be deleted
     */
    public void deleteTag(Tag tag){
        Task updatedTask = new Task(taskViewController.getCurrentTaskView().getAssociatedTask());
        updatedTask.getTags().remove(tag);
        updatedTask = serverUtils.saveTaskByID(updatedTask);
        taskViewController.getCurrentTaskView().updateOverview(updatedTask);
    }
    /**
     * saves a tag to the backend
     * @return tag returns the added tag
     */
    public Tag addTagClicked() {
        if(taskViewController != null) {
            Tag tag = new Tag();
            tag.setDescription("New tag");
            tag.setTagColor("#ffffcc");
            tag.setTagFontColor("#000000");
            tag.setBoardId(mainCtrl.getBoardViewFromTask(
                    taskViewController.getCurrentTask())
                .getAssociatedBoard()
                .getId());
            tag = serverUtils.addNewTag(tag);

            return tag;
        }
        return null;
    }

    /**
     * removes a task from the backend
     * @param taskID the task from which the tag is removed
     * @param tag the tag that has been removed
     * @return removed tag
     */
    public Tag removeTagClicked(Long taskID, Tag tag) {
        return serverUtils.removeTag(tag.getId(), taskID);
    }
}
