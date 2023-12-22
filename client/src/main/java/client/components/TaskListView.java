package client.components;

import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import commons.Board;
import commons.Task;
import commons.TaskList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class TaskListView extends VBox implements Component<TaskList> {
    private TaskList associatedTaskList;
    private final OverviewController overviewController;
    private Label title;
    private final MainCtrl mainCtrl;
    private VBox buttonContainer;
    private GridPane grid;
    private final String gridBorderStyle = "-fx-border-width: 1px; -fx-border-color: black;";

    /**
     * Creates a VBox that stores the tasks that are provided in the TaskList.
     *
     * @param taskList           the tasks to store in this view
     * @param mainCtrl           the main controller, needed to know what to do on button clicks
     * @param overviewController overviewController
     */
    public TaskListView(TaskList taskList,
                        MainCtrl mainCtrl,
                        OverviewController overviewController) {
        super();
        this.overviewController = overviewController;
        this.mainCtrl = mainCtrl;
        createOverview(taskList);
    }

    /**
     * Updates the colors
     */
    void updateColors (TaskList newTaskList) {
        Board associatedBoard = mainCtrl.getBoardViewById(newTaskList.getBoardId())
                .getAssociatedBoard();
        String oldStyle = buttonContainer.getStyle();
        String newStyle = "";
        newStyle += "-fx-text-base-color: " + associatedBoard.getTaskListsFontColor() + ";";
        newStyle += "-fx-background-color: " + associatedBoard.getTaskListsBgColor();
        if(!newStyle.equals(oldStyle)) {
            buttonContainer.setStyle(newStyle);
            title.setStyle("-fx-text-fill: " + associatedBoard.getTaskListsFontColor());
            grid.setStyle(gridBorderStyle + newStyle);
        }
    }
    /**
     * Helper method that updates the current Overview
     * @param newTaskList new TaskList to be displayed
     */
    public void updateOverview(TaskList newTaskList){
        if(associatedTaskList == null || super.getChildren() == null){
            createOverview(newTaskList);
            return;
        }
        this.title.setText(newTaskList.getTitle());
        var result = getDelta(
            associatedTaskList.getTasks(),
            newTaskList.getTasks());
        for(var item : result.get("r")){
            removeTask(item);
        }
        updateTaskListButtons(newTaskList);

        updateColors(newTaskList);

        this.associatedTaskList = newTaskList;
    }

    /**
     * Helper method that adds new SelectTaskButton
     * @param task task to be added
     */

    public void addTask(Task task){
        if(super.getChildren() == null)
            return;
        SelectTaskButton stb = new SelectTaskButton(mainCtrl, task, overviewController);
        this.associatedTaskList.addTask(task);
        buttonContainer.getChildren().add(stb);
    }

    /**
     * Helper method that finds and removes SelectTaskButton
     * @param task task to be removed
     */
    public void removeTask(Task task){
        if(super.getChildren() == null || super.getChildren().size() < 1)
            return;
        for(var item : buttonContainer.getChildren()){
            if(((SelectTaskButton) item).getAssociatedTask().getId().equals(task.getId())) {
                this.associatedTaskList.getTasks()
                    .remove(task);
                buttonContainer.getChildren().remove(item);
                return;
            }
        }
    }

    /**
     * Helper method that directly creates new TaskListView layout
     * It is called in case the initial layout is not created
     * Every other time the updateOverview method should be called
     * since it updates the values of the field and does not
     * replace them with new elements
     * @param taskList initial taskList
     */
    public void createOverview(TaskList taskList){
        this.associatedTaskList = taskList;
        this.getStyleClass().add("TaskListView");

        this.setAlignment(Pos.CENTER);

        this.grid = loadNode("/client.components/taskList.fxml");

        this.getChildren().add(grid);

        // Add the title of the task list
        Label title = new Label (taskList.getTitle());
        title.getStyleClass().add("TaskListTitle");
        GridPane.setMargin(title, new Insets(0,0,0,5));
        this.title = title;
        grid.add(title, 0, 0);

        // Add the list of tasks
        ScrollPane taskListsScrollPane = new ScrollPane();
        buttonContainer = new VBox();
        buttonContainer.setMinHeight(476);
        buttonContainer.setPadding(new Insets(10, 10, 10, 10));
        buttonContainer.setAlignment(Pos.TOP_CENTER);
        taskListsScrollPane.setFitToWidth(true);
        for (Task task : taskList.getTasks()) {
            SelectTaskButton stb = new SelectTaskButton(mainCtrl, task, overviewController);
            stb.getTitle().setStyle("-fx-text-fill: " + task.getFontColor());
            stb.setStyle("-fx-background-color: " + task.getBackGroundColor());
            stb.applyCss();
            buttonContainer.getChildren().add(stb);
        }
        buttonContainer.setAlignment(Pos.TOP_CENTER);
        buttonContainer.getStyleClass().add("TaskListVBox");
        buttonContainer.setSpacing(8);
        taskListsScrollPane.setContent(buttonContainer);
        grid.add(taskListsScrollPane, 0, 1, 3, 1);

        addButtons(grid);

        mainCtrl.addTaskListView(associatedTaskList.getId(), this);
        updateColors(taskList);
        this.makeDragAndDroppable();
    }

    /**
     * Adds the 'delete', 'rename' and 'add' buttons to the task list view
     * @param grid the gridPane to which to add the buttons
     */
    void addButtons(GridPane grid) {
        // Add the functionality buttons in their respective places
        TaskListButton addBtn = new TaskListButton("Add",
                (event -> overviewController.addTaskClicked(this)));

        TaskListButton renameBtn = new TaskListButton("Rename",
                (event -> mainCtrl.renameTaskListClicked(this)));

        TaskListButton deleteBtn = new TaskListButton("Delete",
                (event -> overviewController.removeTaskListClicked(this)));

        HBox deleteButtonContainer, renameButtonContainer, addButtonContainer;
        addButtonContainer = new HBox();
        deleteButtonContainer = new HBox();
        renameButtonContainer = new HBox();

        addButtonContainer.getChildren().add(addBtn);
        renameButtonContainer.getChildren().add(renameBtn);
        deleteButtonContainer.getChildren().add(deleteBtn);

        renameButtonContainer.setAlignment(Pos.CENTER);
        deleteButtonContainer.setAlignment(Pos.CENTER);
        addButtonContainer.setAlignment(Pos.CENTER);

        grid.add(addButtonContainer, 2, 2);
        grid.add(renameButtonContainer, 1, 0);
        grid.add(deleteButtonContainer, 2, 0);
    }

    /**
     * Getter
     * @return associated TaskList
     */
    public TaskList getAssociatedTaskList() {
        return associatedTaskList;
    }

    /**
     * Adds Drag and Drop functionality to a TaskListView.
     * More specifically, adds functionality for dragging over a TaskListView
     * and dropping an item in a TaskListView.
     */
    public void makeDragAndDroppable(){

        this.setOnDragOver(event -> {
            if(event.getDragboard().hasString()){
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

        this.setOnDragDropped((DragEvent event) -> {
            Dragboard dragBoard = event.getDragboard();
            var targetNode = (Node) event.getGestureTarget();
            if(targetNode instanceof TaskListView) {
                if (dragBoard.hasString()) {
                    var src = ((SelectTaskButton) event.getGestureSource()).getAssociatedTask();
                    //long id = this.getAssociatedTaskList().getId();
                    BoardView boardView = mainCtrl.getBoardViewById(
                        this.getAssociatedTaskList().getBoardId());
                    Board updatedBoard = new Board(boardView.getAssociatedBoard());
                    updatedBoard.removeTask(src);
                    updatedBoard = mainCtrl.getServerUtils().saveBoard(updatedBoard);
                    src.setTaskListID(this.getAssociatedTaskList().getId());
                    updatedBoard.insertOrReplace(src);
                    updatedBoard = mainCtrl.getServerUtils().saveBoard(updatedBoard);
                    event.setDropCompleted(true);
                    boardView.updateOverview(updatedBoard);
                    mainCtrl.sendToOthers(updatedBoard);
                } else {
                    event.setDropCompleted(false);
                }
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    /**
     * Utility method that updates
     * the order of the tasks and also checks
     * for added and removed tasks
     * @param newTaskList new taskList
     */
    private void updateTaskListButtons(TaskList newTaskList){
        int old = 0;
        int updated = 0;
        while(old < this.associatedTaskList.getTasks().size()
                && updated < newTaskList.getTasks().size()){
            var currOld = this.associatedTaskList.getTasks().get(old);
            var currNew = newTaskList.getTasks().get(updated);
            if(currOld.equals(currNew)){
                ++old;
                ++updated;
                continue;
            } else if(currOld.getId().equals(currNew.getId())){
                for(var item : buttonContainer.getChildren()){
                    if(((SelectTaskButton)item).getAssociatedTask()
                            .getId().equals(currNew.getId())){
                        ((SelectTaskButton)item).updateOverview(currNew);
                    }
                }
            } else if(!currOld.equals(currNew)){
                boolean swapped = false;
                for(int i = old; i < this.associatedTaskList.getTasks().size(); ++i){
                    var target = this.associatedTaskList.getTasks().get(i);
                    if(target.getId().equals(currNew.getId())){
                        var newSTB = new SelectTaskButton(mainCtrl, currNew, overviewController);
                        this.buttonContainer.getChildren().set(old, newSTB);
                        this.associatedTaskList.getTasks().set(old, currNew);
                        this.associatedTaskList.getTasks().remove(i);
                        this.buttonContainer.getChildren().remove(i);
                        swapped = true;
                        break;
                    }
                } if(!swapped){
                    var taskListButton = new SelectTaskButton(mainCtrl, currNew,
                            overviewController);
                    taskListButton.setStyle("-fx-background-color: " +
                            currNew.getBackGroundColor());
                    taskListButton.getTitle().setStyle("-fx-text-fill: " + currNew.getFontColor());
                    this.buttonContainer.getChildren().add(old, taskListButton);
                    this.associatedTaskList.getTasks().add(old, currNew);
                }
            }
            ++old;
            ++updated;
        }
        while(updated < newTaskList.getTasks().size()){
            addTask(newTaskList.getTasks().get(updated++));
        }
    }

    /**
     * Returns SelectTaskButton associated with a Task
     * @param taskID id of the Task
     * @return SelectTaskButton associated with ID
     */
    public SelectTaskButton getSelectedSTB(Long taskID){
        for(Node child: buttonContainer.getChildren()){
            if(child instanceof SelectTaskButton){
                SelectTaskButton stb = (SelectTaskButton) child;
                if(stb.getAssociatedTask().getId().equals(taskID)){
                    return stb;
                }
            }
        }
        return null;
    }

    /**
     * Changes the selected task to the provided
     * @param task the task to select
     */
    public void changeSelected(Task task) {
        // TODO: inspect why this does not actually change the overview
        SelectTaskButton stb = getSelectedSTB(task.getId());
        SelectTaskButton old = (overviewController.getSelectedTask() != null ?
                getSelectedSTB(overviewController.getSelectedTask().getId()) : null);
        if(old != null) old.unHighlightTask();
        stb.highlightTask();
    }
}
