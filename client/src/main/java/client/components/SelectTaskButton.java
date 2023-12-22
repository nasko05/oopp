package client.components;

import client.scenes.OverviewController;
import commons.Board;
import client.scenes.MainCtrl;
import commons.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public class SelectTaskButton extends VBox implements Component<Task>{
    private Task associatedTask;
    private final MainCtrl mainCtrl;
    private final OverviewController overviewController;
    private Label title;
    private TextField editTitle;
    private ImageView descriptionIcon;
    private GridPane grid;
    private TagBar tagBar;
    private SubtaskBar subtaskBar;

    /**
     * Creates a new button, which, when pressed, selects a certain task
     *
     * @param mainCtrl           the main controller
     * @param task               associated Task
     * @param overviewController overview Controller
     */
    public SelectTaskButton(MainCtrl mainCtrl, Task task, OverviewController overviewController ) {
        this.mainCtrl = mainCtrl;
        this.overviewController = overviewController;
        this.createOverview(task);
        this.makeDragAndDroppable();
        this.addHoverSelection();
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(2))));

    }

    /**
     * Getter
     * @return associated Task
     */
    public Task getAssociatedTask() {
        return associatedTask;
    }

    /**
     * Creates layout for the current component
     * by loading its content dynamically from a file
     * @param newTask object to be displayed
     */
    public void createOverview(Task newTask){
        // Load the grid from fxml file
        this.grid = loadNode("/client.components/selectTaskButton.fxml");
        this.getChildren().add(grid);
        descriptionIcon = (ImageView) this.lookup("#descriptionIcon");
        descriptionIcon.setVisible(
                newTask.getDescription() != null && !newTask.getDescription().equals(""));

        GridPane.setMargin(this, new Insets(5, 5, 5, 5));
        grid.setPadding(new Insets(5, 5, 5, 5));
        getStyleClass().add("SelectTaskButton"); // for future css support

        // Set the title for the task
        Label titleLabel = new Label(newTask.getTitle());
        titleLabel.getStyleClass().add("TaskTitle");
        this.title = titleLabel;
        this.title.setStyle("-fx-text-fill: " + newTask.getFontColor());
        this.title.setVisible(true);
        TextField editTitleField = new TextField();
        this.editTitle = editTitleField;
        this.editTitle.setVisible(false);
        StackPane titleContainer = new StackPane();
        titleContainer.setAlignment(Pos.TOP_CENTER);
        titleContainer.getChildren().add(titleLabel);
        titleContainer.getChildren().add(editTitleField);
        grid.add(titleContainer, 0, 1, 2, 1);

        displayTagsAndSubTasks(newTask);
        addEvents(newTask);
        this.setStyle("-fx-background-color: " + newTask.getBackGroundColor());
        this.associatedTask = newTask;
    }

    /**
     * Method that enable drag and drop
     */
    public void makeDragAndDroppable(){
        this.setOnDragDetected(event -> {
            if (!overviewController.getSelectedBoardView().isWriteAccess()
                && !overviewController.isInAdmin()) {
                overviewController.showError("You do not have the right write access");
                return;
            }
            Dragboard db = startDragAndDrop(TransferMode.ANY);
            db.setDragView(this.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            content.putString(Long.toString(getAssociatedTask().getId()));
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
                if (event.getGestureTarget() instanceof SelectTaskButton
                    && event.getGestureSource() != this) {
                    TaskListView taskListView =
                        mainCtrl.getTaskListViewById(this.associatedTask.getTaskListID());
                    BoardView boardView = mainCtrl.getBoardViewById(
                        taskListView.getAssociatedTaskList().getBoardId());
                    Board updatedBoard = new Board(boardView.getAssociatedBoard());
                    var src = ((SelectTaskButton) event.getGestureSource()).getAssociatedTask();
                    updatedBoard.removeTask(src);
                    updatedBoard = mainCtrl.getServerUtils().saveBoard(updatedBoard);
                    src.setTaskListID(this.getAssociatedTask().getTaskListID());
                    double y = event.getY();
                    double centerY = super.getBoundsInLocal().getMinY() + super.getHeight()/2;
                    if (y < centerY) {
                        updatedBoard.insertInList(src, this.getAssociatedTask(), 0);
                    } else {
                        updatedBoard.insertInList(src, this.getAssociatedTask(), 1);
                    }
                    updatedBoard = mainCtrl.getServerUtils().saveBoard(updatedBoard);
                    boardView.updateOverview(updatedBoard);
                    mainCtrl.sendToOthers(updatedBoard);
                    event.setDropCompleted(true);
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
     * updates the layout of the current element
     * without removing it and creating it again
     * @param updatedTask changed object
     */
    public void updateOverview(Task updatedTask){

        this.title.setText(updatedTask.getTitle());
        // TODO: This directly overrides the style property
        // Since now the only style property is background color
        // It is fine, but in the future this needs to be improved
        this.title.setStyle("-fx-text-fill: " + updatedTask.getFontColor());
        super.setStyle("-fx-background-color: " + updatedTask.getBackGroundColor());
        displayTagsAndSubTasks(updatedTask);
        // Set some properties for the select task button
        addEvents(updatedTask);
        descriptionIcon.setVisible(
                updatedTask.getDescription() != null && !updatedTask.getDescription().equals(""));
        this.associatedTask = updatedTask;
    }

    /**
     * Add the ability to hover and select tasks.
     */
    public void addHoverSelection() {
        this.setOnMouseEntered(mouseEvent -> {
            overviewController.setSelectedTask(getAssociatedTask());
            highlightTask();
            mouseEvent.consume();
        });
        this.setOnMouseExited(mouseEvent -> {
            overviewController.setSelectedTask(null);
            unHighlightTask();
            mouseEvent.consume();
        });
    }

    /**
     * Highlights task when mouse is hovering over it.
     */
    public void highlightTask() {
        this.requestFocus();
        this.setFocused(true);
        this.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(2))));
    }

    /**
     * Reverts task to normal after mouse is no longer hovering over it.
     */
    public void unHighlightTask() {
        this.setFocused(false);
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(2))));
    }

    /**
     * Getter
     * @return the title of select task button
     */
    public Label getTitle() {
        return title;
    }

    /**
     * Utility method that displays tags and subtasks
     * @param updatedTask updated task with updated subtasks
     *                    and tags
     */
    public void displayTagsAndSubTasks(Task updatedTask){
        final double barSizes = 0.009 * grid.getPrefHeight() *
                grid.getRowConstraints().get(3).getPercentHeight();

        grid.getChildren().remove(subtaskBar);
        subtaskBar = new SubtaskBar(updatedTask.getSubTasks(), barSizes);
        grid.add(subtaskBar, 0, 3, 2, 1);

        // Add the tag bar
        grid.getChildren().remove(tagBar);
        tagBar = new TagBar(updatedTask.getTags(), (int) (barSizes * 0.5));
        grid.add(tagBar, 0, 2, 2, 1);
    }

    /**
     * Utility method that adds event for current component
     * @param newTask new task
     */
    public void addEvents(Task newTask) {
        // Set some properties for the select task button
        grid.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() >= 2) {
                mainCtrl.showEditTask(newTask);
            }
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 1) {
                highlightTask();
            }
        });
        // Add the close button
        Button closeButton = new TaskListButton("Delete",
                (event -> overviewController.removeTaskClicked(newTask)));
        grid.add(closeButton, 1, 0);
    }

    /**
     * Edits the title of a task
     */
    public void editTitle(){
        editTitle.setText(title.getText());
        title.setVisible(false);
        editTitle.setVisible(true);
        editTitle.selectAll();

        editTitle.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                overviewController.changeTaskTitle(title, editTitle, this);
                event.consume();
            }
        });
    }

}
