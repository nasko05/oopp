package client.components;

import client.scenes.MainCtrl;
import client.scenes.TaskViewController;
import commons.Board;
import commons.SubTask;
import commons.Tag;
import commons.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class TaskView extends AnchorPane implements Component<Task> {
    private final MainCtrl mainCtrl;
    private final TaskViewController taskViewController;
    private Task associatedTask;
    private Board associatedBoard;
    private ScrollPane subTaskContainer;
    private ScrollPane tagContainer;
    private TextField title;
    private TextArea description;
    private VBox subTasks;
    private FlowPane tags;
    private Label readOnly;
    private ComboBox<Label> colorComboBox;

    /**
     * Constructor
     *
     * @param mainCtrl           reference of mainCtrl
     * @param associatedTask     associated Task
     * @param taskViewController associated taskViewController
     */
    public TaskView(MainCtrl mainCtrl, Task associatedTask, TaskViewController taskViewController) {
        super();

        this.associatedTask = associatedTask;
        this.mainCtrl = mainCtrl;
        this.taskViewController = taskViewController;

        super.setMaxHeight(-1);
        super.setMaxWidth(-1);
        super.setPrefHeight(-1.0);
        super.setPrefWidth(-1.0);
        VBox.setVgrow(this, Priority.ALWAYS);
        this.createOverview(associatedTask);
    }

    /**
     * Refreshes current taskView
     *
     * @param task task to be associated with the taskView
     */
    public void createOverview(Task task) {
        this.associatedTask = new Task(task);
        Parent content = loadNode("/client.components/taskComponent.fxml");
        this.getChildren().clear();
        this.getChildren().add(content);
        this.setWidth(200);
        this.setHeight(600);
        findNodes();
        if(!taskViewController.isWriteAccess()){
            title.setDisable(true);
            description.setDisable(true);
            readOnly.setVisible(true);
        } else {
            title.setDisable(false);
            description.setDisable(false);
            readOnly.setVisible(false);
        }
        title.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(5, 5, 5, 5, true),
                BorderWidths.DEFAULT)));
        description.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(5, 5, 5, 5, true),
                BorderWidths.DEFAULT)));
        subTaskContainer.setFitToWidth(true);
        this.subTasks = (VBox) subTaskContainer.getContent();
        this.tags = (FlowPane) tagContainer.getContent();
        this.title.setText(this.associatedTask.getTitle());
        this.description.setText(this.associatedTask.getDescription());

        displayTagsAndSubTasks(associatedTask);

        BoardView boardView = mainCtrl.getBoardViewFromTask(task);
        associatedBoard = boardView.getAssociatedBoard();

        colorComboBox.getItems().clear();
        for (int i = 0; i < associatedBoard.getTaskColorPresets().size(); i++) {
            Label newLabel = new Label(String.valueOf(i + 1));
            newLabel.setPrefWidth(100);
            newLabel.setAlignment(Pos.CENTER);
            newLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            newLabel.setStyle("-fx-background-color: " +
                    associatedBoard.getTaskColorPresets().get(i).getBackGroundColor()
                    + ";-fx-text-fill:" +
                    associatedBoard.getTaskColorPresets().get(i).getFontColor()
            );
            colorComboBox.getItems().add(newLabel);
        }
        Label newLabel = new Label("current");
        newLabel.setPrefWidth(100);
        newLabel.setAlignment(Pos.CENTER);
        newLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        newLabel.setStyle("-fx-background-color: " + task.getBackGroundColor()
                + ";-fx-text-fill:" + task.getFontColor());
        colorComboBox.setValue(newLabel);
        addEventsForAllButtons();
        makeDragAndDroppable();

    }

    /**
     * This method is a helper method for createOverview(). It goes over the clickable buttons
     * and assigns onClick methods for them
     */
    void addEventsForAllButtons() {
        Button closeButton = (Button) this.lookup("#saveButton");
        Button addSubtaskButton = (Button) this.lookup("#addSubtaskButton");
        Button addTagButton = (Button) this.lookup("#addTagButton");

        closeButton.setOnMouseClicked(event -> ((Stage) super.getScene().getWindow()).close());
        addSubtaskButton.setOnMouseClicked(event ->
                taskViewController.addSubTaskClicked()
        );
        addTagButton.setOnMouseClicked(event ->
                taskViewController.addTagClicked(this)
        );
        this.title.setOnKeyReleased(this::saveChange);
        this.description.setOnKeyReleased(this::saveChange);
        colorComboBox.setOnAction(event ->
                taskViewController.changeColorPreset(associatedBoard.getTaskColorPresets().
                        get(Integer.parseInt(colorComboBox.getValue().getText()) - 1))
        );
        colorComboBox.setCellFactory(param -> new ListCell<>() {
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
                    Label label = new Label(item.getText());
                    label.setPrefWidth(100);
                    label.setAlignment(Pos.CENTER);
                    label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    label.setTextFill(Color.web(associatedBoard.getTaskColorPresets().
                            get(Integer.parseInt(item.getText()) - 1).getFontColor()));
                    label.setStyle("-fx-background-color: " + associatedBoard.getTaskColorPresets().
                            get(Integer.parseInt(item.getText()) - 1).getBackGroundColor());
                    setGraphic(label);
                }
            }
        });
    }

    /**
     * Update the current overview
     *
     * @param newTask the new task to be displayed
     */
    public void updateOverview(Task newTask) {
        var titleCursor = title.getCaretPosition();
        var descriptionCursor = description.getCaretPosition();

        this.title.setText(newTask.getTitle());
        this.description.setText(newTask.getDescription());

        title.positionCaret(titleCursor);
        description.positionCaret(descriptionCursor);

        this.tags.getChildren().clear();
        this.subTasks.getChildren().clear();

        displayTagsAndSubTasks(newTask);

        this.associatedTask = newTask;
    }

    /**
     * Get the associated task of taskView
     *
     * @return task in the taskView
     */
    public Task getAssociatedTask() {
        return associatedTask;
    }

    /**
     * Adds Drag and Drop functionality to a TaskView.
     * More specifically, adds functionality for dragging over a TaskListView
     * and dropping an item in a TaskListView.
     */
    private void makeDragAndDroppable() {

        subTasks.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

        subTasks.setOnDragDropped((DragEvent event) -> {
            Dragboard dragBoard = event.getDragboard();
            var targetNode = (Node) event.getGestureTarget();
            if (targetNode instanceof VBox) {
                if (dragBoard.hasString()) {
                    var src = ((SubTaskView) event.getGestureSource()).getAssociatedSubTask();
                    TaskView taskView = taskViewController.getCurrentTaskView();
                    Task updatedTask = new Task(taskView.getAssociatedTask());
                    updatedTask.deleteSubTask(src);
                    updatedTask = mainCtrl.getServerUtils().saveTaskByID(updatedTask);
                    src.setTaskId(this.getAssociatedTask().getId());
                    updatedTask.insertOrReplace(src);
                    updatedTask = mainCtrl.getServerUtils().saveTaskByID(updatedTask);

                    taskView.updateOverview(updatedTask);
                    mainCtrl.sendToOthers(updatedTask);
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
     * Utility method that finds
     * all nodes that are needed
     */
    @SuppressWarnings("unchecked")
    private void findNodes() {
        this.title = (TextField) this.lookup("#titleField");
        this.description = (TextArea) this.lookup("#descriptionField");
        this.subTaskContainer = (ScrollPane) this.lookup("#subTaskContainer");
        this.tagContainer = (ScrollPane) this.lookup("#tagContainer");
        this.colorComboBox = (ComboBox<Label>) this.lookup("#colorComboBox");
        this.readOnly = (Label) this.lookup("#readOnly");
    }

    /**
     * Utility method that displays all tags
     * and subtasks of given task
     *
     * @param newTask provided task, whose tags
     *                and subtasks will be displayed
     */
    public void displayTagsAndSubTasks(Task newTask) {
        for (SubTask subTask : newTask.getSubTasks()) {
            //Create Sub Task View
            var subTaskView = new SubTaskView(taskViewController, newTask,
                    subTask, mainCtrl);
            subTasks.getChildren().add(subTaskView);
        }
        for (Tag tag : newTask.getTags()) {
            //Create Tag View
            var tagView = new TagView(taskViewController, tag, mainCtrl);
            tags.getChildren().add(tagView);
        }
    }

    /**
     * Utility method that saves changes
     * @param event event that triggered the method
     */
    private void saveChange(KeyEvent event){
        if (event.getCode() != KeyCode.BACK_SPACE
                &&
                (event.isShortcutDown()
                        || event.isShiftDown())) {
            return;
        }
        taskViewController.updateOnChange(title.getText(), description.getText());
    }
}
