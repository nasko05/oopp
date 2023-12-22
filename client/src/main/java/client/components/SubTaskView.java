package client.components;

import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import commons.SubTask;
import commons.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;

public class SubTaskView extends HBox implements Component<SubTask> {
    private final Task associatedTask;
    private final TaskViewController taskViewController;
    private final MainCtrl mainCtrl;
    private SubTask associatedSubTask;
    private CheckBox checkBox;

    /**
     * Constructor
     *
     * @param taskViewController associated taskViewController
     * @param associatedTask     the associated task
     * @param associatedSubTask  associated subtask
     * @param mainCtrl           the main controller
     */
    public SubTaskView(TaskViewController taskViewController, Task associatedTask,
                       SubTask associatedSubTask, MainCtrl mainCtrl) {
        super();
        this.taskViewController = taskViewController;
        this.associatedSubTask = associatedSubTask;
        this.associatedTask = associatedTask;
        this.mainCtrl = mainCtrl;
        this.createOverview(associatedSubTask);
    }

    /**
     * Create an overview of subtasks
     *
     * @param subTask subtask
     */
    public void createOverview(SubTask subTask) {
        this.associatedSubTask = subTask;
        Parent content = loadNode("/client.components/subTaskComponent.fxml");
        this.getChildren().clear();
        this.getChildren().add(content);
        setMargin(content, new Insets(10, 2, 10, 2));
        this.checkBox = (CheckBox) this.lookup("#subTaskText");
        this.checkBox.setText(subTask.getDescription());
        this.checkBox.setSelected(subTask.isChecked());

        addEventsForAllButtons();
        this.makeDragAndDroppable();
    }

    /**
     * Adds the events for all buttons in this scene, including the checkbox button
     */
    void addEventsForAllButtons() {
        Button editButton = (Button) this.lookup("#editSubtaskButton");
        Button deleteButton = (Button) this.lookup("#deleteSubtaskButton");
        CheckBox checkBox = (CheckBox) this.lookup("#subTaskText");

        editButton.setOnMouseClicked(event -> {
                if (!taskViewController.isWriteAccess()) {
                    taskViewController.showError("You do not have the right write access");
                } else {
                    mainCtrl.showEditSubtaskScene(associatedSubTask,
                            taskViewController.getCurrentTaskView());
                }
            }
        );

        deleteButton.setOnMouseClicked(event -> {
            taskViewController.deleteSubTaskClicked(associatedSubTask);
            taskViewController.updateOnChange();
        });

        checkBox.setOnAction(event -> {
            SubTask updatedSubTask = new SubTask(associatedSubTask);
            Task updatedTask = new Task(associatedTask);
            updatedSubTask.setChecked(checkBox.isSelected());
            updatedTask.addSubtask(updatedSubTask);
            taskViewController.getCurrentTaskView().updateOverview(updatedTask);
            taskViewController.updateOnChange();
        });
    }

    /**
     * Update the current overview
     *
     * @param subTask new task to be displayed with update
     */
    public void updateOverview(SubTask subTask) {
        this.checkBox.setText(subTask.getDescription());
        this.checkBox.setSelected(subTask.isChecked());
        this.associatedSubTask = subTask;
    }

    /**
     * Getter
     *
     * @return id
     */
    public SubTask getAssociatedSubTask() {
        return associatedSubTask;
    }

    /**
     * Getter
     *
     * @return id of the associated task
     */
    public Task getAssociatedTask() {
        return associatedTask;
    }


    /**
     * Enables drag & drop for reordering SubTasks.
     */
    public void makeDragAndDroppable() {
        this.setOnDragDetected(event -> {
            if (!taskViewController.isWriteAccess()) {
                taskViewController.showError("You do not have the right write access");
                return;
            }
            Dragboard db = startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString(Long.toString(getAssociatedSubTask().getId()));
            db.setContent(content);
            event.setDragDetect(true);

            event.consume();
        });

        this.setOnDragOver(new EventHandler<>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });

        this.setOnDragDropped(event -> {
            Dragboard dragBoard = event.getDragboard();
            if (dragBoard.hasString()) {
                if (event.getGestureTarget() instanceof SubTaskView
                        && event.getGestureSource() != this) {
                    TaskView taskView = taskViewController.getCurrentTaskView();
                    Task updatedTask = new Task(taskView.getAssociatedTask());
                    var src = ((SubTaskView) event.getGestureSource()).getAssociatedSubTask();
                    updatedTask.deleteSubTask(src);
                    updatedTask = mainCtrl.getServerUtils().saveTaskByID(updatedTask);
                    src.setTaskId(this.getAssociatedTask().getId());
                    double y = event.getY();
                    double centerY = super.getBoundsInLocal().getMinY() + super.getHeight() / 2;
                    if (y < centerY) {
                        updatedTask.insertInSubTaskList(src, this.getAssociatedSubTask(), 0);
                    } else {
                        updatedTask.insertInSubTaskList(src, this.getAssociatedSubTask(), 1);
                    }
                    updatedTask = mainCtrl.getServerUtils().saveTaskByID(updatedTask);

                    taskView.updateOverview(updatedTask);
                    taskViewController.updateOnChange();
                    mainCtrl.sendToOthers(updatedTask);
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
            }
            event.consume();
        });
    }
}
