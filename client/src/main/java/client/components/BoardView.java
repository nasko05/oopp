package client.components;

import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import commons.Board;
import commons.Tag;
import commons.TaskList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;


public class BoardView extends Tab implements Component<Board>{
    private final MainCtrl mainCtrl;
    private final OverviewController overviewController;
    private Board associatedBoard;
    private HBox taskListViewContainers;
    private GridPane grid;
    private Label titleLabel;
    private Label userID;
    private Button copyUserID;
    private Button addListButton;
    private Button colorButton;
    private Button deleteButton;
    private ScrollPane outerCont;
    private TextField editTitle;
    private Label copiedLabel;
    private Button lockedStatus;
    private boolean writeAccess;
    private Button addRemovePass;
    private Button editPass;
    @FXML
    private ScrollPane tagList;

    /**
     * Given a board, creates a view (which is a ScrollPane) for it
     *
     * @param board              the board that needs to be viewed
     * @param mainCtrl           the main controller
     * @param overviewController the overview controller
     */
    public BoardView(Board board, MainCtrl mainCtrl, OverviewController overviewController) {
        super();
        this.mainCtrl = mainCtrl;
        this.overviewController = overviewController;
        createOverview(board);
    }

    /**
     * Adds a new taskList to the view of the board
     *
     * @param newTaskList the taskList that will be added to the view
     */
    public void addNewTaskList(TaskList newTaskList) {
        var newTaskListView = new TaskListView(newTaskList, mainCtrl,
            overviewController);
        newTaskListView.setMinWidth(150);
        taskListViewContainers.getChildren().add(newTaskListView);
        mainCtrl.addTaskListView(newTaskList.getId(), newTaskListView);
    }

    /**
     * Helper method that finds a taskListView and removes it
     *
     * @param taskList element to be removed
     */
    public void deleteTaskList(TaskList taskList){
        for (int i = 0; i < taskListViewContainers.getChildren().size(); i++) {
            TaskListView curr = (TaskListView) taskListViewContainers.getChildren().get(i);
            if(taskList.getId().equals(curr.getAssociatedTaskList().getId())){
                taskListViewContainers.getChildren().remove(curr);
                mainCtrl.deleteTaskListView(taskList.getId());
                return;
            }
        }
    }

    /**
     * Returns the current Board object
     * connected with the BoardView object
     *
     * @return current Board
     */
    public Board getAssociatedBoard() {
        return this.associatedBoard;
    }

    /**
     * Designated refreshMethod for the BoardView class
     *
     * @param newBoard new Board to be rendered
     */
    public void updateOverview(Board newBoard) {
        if (associatedBoard == null || super.getContent() == null) {
            createOverview(newBoard);
            return;
        }
        String newStyle = "";
        String pastStyle = taskListViewContainers.getStyle();
        // Calculate the new style for color changes
        newStyle += "-fx-background-color: " + newBoard.getBoardBgColor() + ";";
        newStyle += "-fx-text-base-color:" + newBoard.getBoardFontColor();
        this.titleLabel.setText(newBoard.getTitle());
        if(!newStyle.equals(pastStyle)) {
            setStyle(newBoard);
            super.setStyle("-fx-text-fill: " + newBoard.getBoardFontColor());
        }
        displayPasswordProtection(newBoard);
        var result = getDelta(
            associatedBoard.getTaskLists(),
            newBoard.getTaskLists());

        var allTags = mainCtrl.getAllTagsFromBoard(newBoard);
        if(allTags != null) {
            VBox tagsVBox = new VBox();
            tagsVBox.setSpacing(10);
            displayTags(tagsVBox, allTags);
            tagList.setContent(tagsVBox);
        }
        // It is important to put line this here, since otherwise, refreshing the taskViews
        // will no longer work due to colors.
        this.associatedBoard = newBoard;

        for(var item : result.get("r")){
            deleteTaskList(item);
        }
        for(var item : result.get("a")){
            addNewTaskList(item);
        }
        var modified = result.get("m");
        for(var item : modified){
            var correspondingTaskListView = mainCtrl.getTaskListViewById(item.getId());
            correspondingTaskListView.updateOverview(item);
        }
    }

    /**
     * Utility method that displays whether the user has correct
     * write privileges
     */
    private void displayPasswordProtection(Board board) {
        var msg = "";
        if(board.getPassword().equals("")) {
            msg = "Create password";
        } else {
            msg = "Remove password";
        }
        this.addRemovePass.setText(msg);
        this.addRemovePass.setVisible(true);
        this.editPass.setVisible(!board.getPassword().equals(""));
        this.lockedStatus.setVisible(true);
        var imageView = new ImageView();
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        if(writeAccess || overviewController.isInAdmin() || board.getPassword().equals("")){
            imageView.setImage(new Image("/icons/unlocked.png"));
            lockedStatus.setStyle("-fx-border-color: transparent; " +
                    "-fx-background-color: transparent");
        } else {
            lockedStatus.setStyle("-fx-border-color: black; " +
                    "-fx-background-color: white");
            imageView.setImage(new Image("/icons/locked.png"));
        }
        this.lockedStatus.setGraphic(imageView);
    }


    /**
     * Helper method that directly creates new BoardView layout
     * It is called in case the initial layout is not created
     * Every other time the updateOverview method should be called
     * since it updates the values of the field and does not
     * replace them with new elements
     *
     * @param board initial Board
     */
    public void createOverview(Board board){
        this.associatedBoard = board;
        mainCtrl.addBoardView(associatedBoard.getId(), this);
        this.grid = loadNode("/client.components/boardView.fxml");
        findNodes();
        var saved = mainCtrl.getPasswordBoard(board.getId());
        if(saved != null){
            writeAccess = overviewController.getOverviewService().checkBoardPassword(board, saved);
        } else {
            writeAccess = board.getPassword().equals("");
        }
        if(!writeAccess){
            addRemovePass.setVisible(false);
        }
        displayPasswordProtection(board);
        HBox hBox = new HBox();
        this.getStyleClass().add("rootNode");
        grid.getStyleClass().add("rootNode");
        VBox tagsVBox = new VBox();
        HBox.setHgrow(hBox, Priority.ALWAYS);
        hBox.setMinWidth(1036);
        this.titleLabel.setText(associatedBoard.getTitle());
        this.userID.setText(board.getUserId());

        // Add the taskLists:
        var taskListViews = new TaskListView[board.getTaskLists().size()];
        for (int i = 0; i < taskListViews.length; ++i) {
            TaskList currentTaskList = board.getTaskLists().get(i);
            var currentTaskListView = new TaskListView(currentTaskList, mainCtrl,
                overviewController);
            taskListViews[i] = currentTaskListView;
        }
        for (TaskListView taskListView : taskListViews) {
            hBox.getChildren().add(taskListView);
        }
        hBox.setSpacing(10);
        outerCont.setContent(hBox);
        outerCont.setFitToHeight(true);
        outerCont.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        this.copiedLabel = (Label) grid.lookup("#textCopiedLabel");
        this.copiedLabel.setVisible(false);
        super.setContent(grid);
        super.setText(associatedBoard.getUserId());
        this.taskListViewContainers = hBox;
        addCloseButton();
        addEvents(board);
        setStyle(board);
        var allTags = mainCtrl.getAllTagsFromBoard(board);
        if(allTags != null) {
            displayTags(tagsVBox, allTags);
            tagsVBox.setSpacing(10);
            tagList.setContent(tagsVBox);
        }
    }

    /**
     * Utility method that displays tags
     * @param tagsVBox container for the tags
     * @param allTags tags that will be displayed
     */
    private void displayTags(VBox tagsVBox, List<Tag> allTags) {
        var tagViews = new TagView[allTags.size()];
        for (int i = 0; i < allTags.size(); i++) {
            Tag currentTag = allTags.get(i);
            var currentTagView = new TagView(currentTag, mainCtrl);
            tagViews[i] = currentTagView;
        }
        tagList.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        for (int i = 0; i < allTags.size(); ++i) {
            tagsVBox.getChildren().add(tagViews[i]);
        }
    }

    /**
     * Creates close button for board tab
     */
    public void addCloseButton(){
        super.setClosable(true);
        super.setOnClosed(event -> overviewController.closeTabClicked(this));
    }
    /**
     * Getter for the title Label
     * (For editing purposes)
     * @return the title Label
     */
    public Label getTitleLabel() {
        return titleLabel;
    }

    /**
     * Getter for the title TextFField
     * (For editing purposes)
     * @return the title Field
     */
    public TextField getEditTitle() {
        return editTitle;
    }

    /**
     * Returns the label that is supposed to show whether the text was copied
     * @return the label object
     */
    public Label getCopiedLabel() {
        return copiedLabel;
    }

    /**
     * Returns the copy button
     * @return the copy button
     */
    public Button getCopyButton() {
        return copyUserID;
    }
    /**
     * Utility method to add events
     * @param board new board
     */
    private void addEvents(Board board) {
        addListButton.setOnMouseClicked(event ->
                overviewController.addNewTaskListClicked());
        colorButton.setOnMouseClicked(event ->
                overviewController.colorClicked());
        deleteButton.setOnMouseClicked(event ->
                overviewController.showDeleteConfirmation());
        this.copyUserID.setOnMouseClicked(event -> overviewController.copyIDClicked(board));

        titleLabel.setOnMouseClicked(event ->
                overviewController.titleClicked());
        editPass.setOnMouseClicked(event -> {
            if(!writeAccess && !overviewController.isInAdmin()){
                overviewController.showError("You do not have right access!");
                return;
            }
            mainCtrl.showChangePassword();
        });
        lockedStatus.setOnMouseClicked(event -> {
            if(!writeAccess){
                overviewController.unlockBoard();
            }
        });
        this.addRemovePass.setOnMouseClicked(event -> {
            if(associatedBoard.getPassword().equals("")){
                overviewController.lockBoard();
            } else {
                overviewController.removePassword();
            }
        });
    }

    /**
     * Utility method that finds all nodes
     * that need to be used
     */
    private void findNodes(){
        this.outerCont = (ScrollPane) grid.lookup("#scrollPane");
        this.tagList = (ScrollPane) grid.lookup("#tagList");
        this.addListButton = (Button) grid.lookup("#addListButton");
        this.colorButton = (Button) grid.lookup("#colorButton");
        this.deleteButton = (Button) grid.lookup("#deleteButton");
        this.titleLabel = (Label) grid.lookup("#boardName");
        this.editTitle = (TextField) grid.lookup("#titleField");
        this.userID = (Label) grid.lookup("#userID");
        this.copyUserID = (Button) grid.lookup("#copyUserID");
        this.lockedStatus = (Button) grid.lookup("#lockedStatus");
        this.addRemovePass = (Button) grid.lookup("#addRemovePass");
        this.editPass = (Button) grid.lookup("#editPass");
    }

    /**
     * Sets the style of current components
     * @param board new board
     */
    private void setStyle(Board board){
        String newStyle = "";
        newStyle += "-fx-background-color: " + board.getBoardBgColor() + ";";
        newStyle += "-fx-text-base-color:" + board.getBoardFontColor();
        this.grid.setStyle(newStyle);
        this.taskListViewContainers.setStyle(newStyle);
        this.userID.setStyle("-fx-text-fill: " + board.getBoardFontColor());
        this.titleLabel.setTextFill(Color.web(board.getBoardFontColor()));
    }

    /**
     * Getter
     * @return whether the user
     * is authorized to edit the board
     */
    public boolean isWriteAccess() {
        return writeAccess;
    }

    /**
     * Setter
     * @param writeAccess update write access rights
     */
    public void setWriteAccess(boolean writeAccess) {
        this.writeAccess = writeAccess;
    }
}
