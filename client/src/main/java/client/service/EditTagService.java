package client.service;

import client.components.TaskView;
import client.scenes.EditTagController;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.Board;
import commons.Tag;
import commons.Task;

import javax.inject.Inject;

public class EditTagService {

    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private EditTagController editTagController;

    /**
     * An injectable constructor
     *
     * @param mainCtrl           the main controller
     * @param serverUtils        the server utils
     */
    @Inject
    public EditTagService(MainCtrl mainCtrl,
                         ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param editTagController the controller to which this service belongs
     */
    public void setEditTagController(EditTagController editTagController) {
        this.editTagController = editTagController;
    }

    /**
     * getter for the controller. This is required for testing.
     * @return the editTag controller
     */
    public EditTagController getEditTagController(){
        return editTagController;
    }

    /**
     * Saves a tag, sends it to the server and refreshes the screen
     *
     * @param taskView the corresponding task view
     * @param tag current tag
     * @param tagDescription the description of the tag
     * @param tagColor the background color of the tag
     * @param tagFontColor the font color of the tag
     * @param associatedTask the task associated to this tag. Might be null, if
     *                       taskView is provided. This is mostly used for shortcuts
     */
    public void save(TaskView taskView, Tag tag, String tagDescription,
                     String tagColor, String tagFontColor, Task associatedTask) {
        Tag newTag = new Tag(tag);
        newTag.setDescription(tagDescription);
        newTag.setTagColor(tagColor);
        newTag.setTagFontColor(tagFontColor);
        if(taskView != null || associatedTask != null) {
            if (!serverUtils.saveTag(newTag)) {
                editTagController.displayFailedToSaveTag();
                return;
            }
            if(associatedTask == null) associatedTask = taskView.getAssociatedTask();
            Task task = new Task(associatedTask);
            for (int i = 0; i < task.getTags().size(); ++i) {
                var curr = task.getTags().get(i);
                if (curr.getId().equals(tag.getId())) {
                    task.getTags().set(i, newTag);
                    break;
                }
            }
            if(taskView != null) taskView.updateOverview(task);
            var boardView = mainCtrl.getBoardViewFromTask(task);
            var updatedBoard = new Board(boardView.getAssociatedBoard());
            updatedBoard.insertOrReplace(task);
            boardView.updateOverview(updatedBoard);
            mainCtrl.sendToOthers(updatedBoard);
            if(taskView == null) serverUtils.saveTaskByID(task);
        } else {
            if (!serverUtils.updateTags(tag, newTag)) {
                editTagController.displayFailedToSaveTag();
                return;
            }
            var boardView = mainCtrl.getBoardViewById(newTag.getBoardId());
            var updatedBoard = serverUtils.getBoardByID(newTag.getBoardId());
            boardView.updateOverview(updatedBoard);
            mainCtrl.sendToOthers(updatedBoard);
        }

    }
}
