package client.scenes;

import client.components.BoardView;
import client.components.SelectTaskButton;
import client.components.TaskListView;
import client.components.Workspace;
import client.service.OverviewService;
import commons.Board;
import commons.Task;
import commons.TaskList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OverviewController {
    private final OverviewService overviewService;
    private final Set<Long> openedBoards;
    private final Set<Long> persistedState;
    private Workspace workspace;
    private boolean inAdmin = false;
    private Button adminButton;
    private TextField boardText;
    @FXML
    private TabPane tabPane;
    @FXML
    private PasswordField newPass;
    @FXML
    private PasswordField repeatPass;
    private int numberOfTimesCopied = 0;
    private Task selectedTask;
    protected PasswordField passwordField;


    /**
     * Injector constructor to initialize all instances
     *
     * @param overviewService used to handle logic for this scene
     */
    @Inject
    public OverviewController(OverviewService overviewService) {
        this.openedBoards = new HashSet<>();
        this.overviewService = overviewService;
        this.persistedState = new HashSet<>();
        overviewService.setOverviewController(this);
    }

    /**
     * Returns a set of opened boards
     *
     * @return a hashset of opened boards
     */
    public Set<Long> getOpenedBoards() {
        return openedBoards;
    }

    /**
     * Refresh the current board
     * If there are no tabs create a new one
     *
     * @param board new board
     */
    public void displayBoard(Board board) {
        BoardView boardView = overviewService.createBoardView(board);

        AnchorPane.setTopAnchor(this.tabPane, 30.0);
        AnchorPane.setLeftAnchor(this.tabPane, 0.0);
        AnchorPane.setRightAnchor(this.tabPane, 0.0);
        AnchorPane.setBottomAnchor(this.tabPane, 0.0);

        tabPane.getTabs().add(boardView);
        tabPane.getSelectionModel().select(boardView);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    /**
     * Displays all boards for the admin to see
     */
    protected void displayAllBoards(){
        this.persistedState.clear();
        this.persistedState.addAll(openedBoards);
        this.openedBoards.clear();
        overviewService.clearMaps();
        var boards = overviewService.getAllBoards();
        tabPane.getTabs().clear();
        addWorkspace();
        for(var board : boards){
            displayBoard(board);
        }
    }

    /**
     * The method that handles entering admin mode
     */
    public void checkAdminPassword(){
        //persist previous state of the application before entering admin
        //and return to it whenever
        if(!inAdmin) {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("Authentication");
            textInputDialog.setHeaderText("Please enter the password for the administrator: ");
            textInputDialog.getDialogPane().setGraphic(null);

            PasswordField passwordField = new PasswordField();
            passwordField.setStyle("-fx-min-width: 50; -fx-min-height: 20");
            textInputDialog.getDialogPane().setContent(passwordField);

            var res = textInputDialog.showAndWait();
            if(passwordField.getText().equals("")) {
                return;
            }
            if(res.isEmpty()){
                return;
            }
            var pass = passwordField.getText();
            var isCorrect = overviewService.checkAdminPassword(pass);
            if (!isCorrect) {
                showError("Wrong admin password");
            } else {
                inAdmin = true;
                displayAllBoards();
                adminButton.setText("Leave admin");
                adminButton.setStyle("-fx-text-fill: black;");
            }
        } else {
            tabPane.getTabs().clear();
            inAdmin = false;
            addWorkspace();
            adminButton.setText("Request access");
            adminButton.setStyle("-fx-text-fill: black;");
            for(Long id : persistedState){
                overviewService.joinBoard(id + "");
            }
        }
    }
    /**
     * Close the current tab. There probably is no need to move this to the service layer
     * @param boardView the board view that is to be closed
     */
    public void closeTabClicked(BoardView boardView) {
        Long boardID = boardView.getAssociatedBoard().getId();

        openedBoards.remove(boardID);
        overviewService.removeBoardView(boardID);

        var ids = boardView.getAssociatedBoard().getTaskLists().stream().map(TaskList::getId)
                .collect(Collectors.toList());
        for(Long taskListID : ids){
            overviewService.removeTaskListView(taskListID);
        }
        tabPane.getTabs().remove(boardView);
    }

    /**
     * Handles editing of the board title.
     */
    public void titleClicked(){
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        var boardView = (BoardView) tabPane.getSelectionModel().getSelectedItem();

        if(boardView == null){
            return;
        }
        var titleLabel = (Label) boardView.getTitleLabel();
        var editTitle = (TextField) boardView.getEditTitle();

        editTitle.setText(titleLabel.getText());
        titleLabel.setVisible(false);
        editTitle.setVisible(true);
        editTitle.requestFocus();
        editTitle.selectAll();

        /*
          When focus property changes (user clicks away from the TextField),
          this updates the Board with the new title in the DB
          and updates the BoardView with the new title.
         */
        editTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                changeTitle(editTitle, titleLabel, boardView);
            }
        });

        editTitle.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                changeTitle(editTitle, titleLabel, boardView);
                workspace.updateOverview(null);
            }
        });
    }

    /**
     * Changes the title of a Board (both frontend and backend)
     * @param textField field with the new title
     * @param label label of the title (updated here)
     * @param boardView respective BoardView (+associated Board)
     */
    public void changeTitle(TextField textField, Label label, BoardView boardView){
        String newTitle = textField.getText();
        if(newTitle == null || newTitle.equals("")){
            newTitle = "Untitled";
        }
        label.setText(newTitle);
        textField.setVisible(false);
        label.setVisible(true);
        overviewService.updateTitle(newTitle, boardView);
    }

    /**
     * Method that shows error Alert box
     * @param text Error message
     */
    public void showError(String text) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setContentText(text);
        Platform.runLater(error::showAndWait);
    }

    /**
     * Method that handles joining a new board
     * It retrieves it using serverUtils get request
     * Then displays it using refreshBoard method
     */
    public void joinBoardClicked() {
        if(inAdmin) return ;
        overviewService.joinBoard(boardText.getText());
    }

    /**
     * Disconnects from the current server.
     */
    @FXML
    public void disconnectClicked() {
        overviewService.disconnectClicked();
        this.openedBoards.clear();
        this.tabPane.getTabs().clear();
    }

    /**
     * Sets the TextField with the board id
     * @param boardId board id to be inputted
     */
    public void setBoardId(String boardId){
        var textField = (TextField) workspace.lookup("#boardText");
        textField.setText(boardId);
    }

    /**
     * Removes board frm recent
     * @param title title of the board
     * @param id target board
     */
    public void removeRecent(String title, Long id){
        workspace.removeRecentBoard(title, id);
        overviewService.saveStorage();
    }
    /**
     * This method is called when a delete button is clicked, by a task
     * @param task the task to be deleted
     */
    public void removeTaskClicked(Task task){
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        overviewService.removeTaskClicked(task);
    }
    /**
     * Adds a new taskList to the board and the database
     */
    public void addNewTaskListClicked() {
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        // Check if any board is currently open
        var currentTab = (BoardView) tabPane.getSelectionModel().getSelectedItem();
        if (currentTab == null) {
            return;
        }
        overviewService.addNewTaskList(currentTab);
    }

    /**
     * Remove a taskList from the current board
     *
     * @param taskListView the taskListView of task list to remove
     */
    public void removeTaskListClicked(TaskListView taskListView) {
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        BoardView tab = (BoardView) tabPane.getSelectionModel().getSelectedItem();
        overviewService.removeTaskList(taskListView, tab);
    }

    /**
     * Adds a task and assigns the task to the current taskList.
     *
     * @param taskListView current taskList of which the task is a part of
     */
    public void addTaskClicked(TaskListView taskListView) {
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        overviewService.addNewTask(taskListView);
    }

    /**
     * Show color management view when "Color" button is clicked
     */
    public void colorClicked(){
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        BoardView boardView = (BoardView) tabPane.getSelectionModel().getSelectedItem();
        if (boardView == null) return;
        overviewService.showColorManagement(boardView);
    }

    /**
     * Closes the BoardView
     * and removed the associated Board from the DB.
     * @param boardView boardView to be closed
     */
    public void deleteBoardClicked(BoardView boardView){
        if(boardView == null){
            return;
        }
        Board board = boardView.getAssociatedBoard();
        closeTabClicked(boardView);
        overviewService.deleteRecentBoard(board);
    }

    /**
     * Creates new Board
     */
    public void createNewBoard(){
        Board newBoard = overviewService.createNewBoard();
        if(!inAdmin) {
            this.openedBoards.add(newBoard.getId());
            workspace.addRecentBoard(newBoard.getId());
        }
        var newTab = overviewService.createBoardView(newBoard);
        tabPane.getTabs().add(newTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.getSelectionModel().select(newTab);
    }
    /**
     * Shows an error indicating that a board is already opened
     */
    public void showBoardAlreadyOpenError() {
        Alert box = new Alert(Alert.AlertType.INFORMATION);
        box.setTitle("Board already opened");
        box.setContentText("You cannot open the same board twice");
        box.showAndWait();
    }

    /**
     * Method that unlocks a board
     * granting write access to the user
     */
    public void unlockBoard(){
        overviewService.unlockBoard();
    }

    /**
     * Utility method that displays dialog to remove
     * board password
     */
    public void removePassword(){
        overviewService.removePassword();
    }
    /**
     * Method that locks board
     * still granting write access to current user
     */
    public void lockBoard(){
        overviewService.lockBoard();
    }

    /**
     * Method that changes the password of the board
     * @param event event that triggered the change
     */
    public void changePassword(MouseEvent event){
        var newPassword = newPass.getText();
        var repeatPassword = repeatPass.getText();
        if(!newPassword.equals(repeatPassword)){
            showError("Passwords are not matching!");
            return;
        }
        overviewService.updatePassword(newPassword);
        ((Stage)((Button)event.getSource())
            .getScene().getWindow()).close();
    }

    /**
     * Getter for password field
     * @return password field
     */
    public PasswordField getPasswordField(){
        return passwordField;
    }

    /**
     * Setter for password field
     * @param passwordField new password field
     */
    public void setPasswordField(PasswordField passwordField){
        this.passwordField = passwordField;
    }

    /**
     * Shows confirmation screen for deleting a board
     */
    public void showDeleteConfirmation(){
        if(!getSelectedBoardView().isWriteAccess() && !inAdmin){
            showError("You do not have the right write access");
            return;
        }
        BoardView boardView = getSelectedBoardView();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Board");
        confirm.setContentText("Are you sure? This will delete the board from the server.");
        ButtonType yes = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        confirm.getButtonTypes().setAll(ButtonType.CANCEL, yes);
        Optional<ButtonType> result = confirm.showAndWait();
        if(result.isPresent() && result.get() == yes) {
            deleteBoardClicked(boardView);
            workspace.updateOverview(null);
        }
        else if(result.isPresent()) {
            confirm.close();
        }
    }

    /**
     * Get the corresponding BoardView from the selected tab
     * @return BoardView or null if no selected
     */
    public BoardView getSelectedBoardView() {
        return (BoardView) tabPane.getSelectionModel().getSelectedItem();
    }

    /**
     * Adds the workspace as the first tab
     */
    public void addWorkspace() {
        this.workspace = overviewService.createWorkspace();
        Tab tab = new Tab("Workspace", workspace);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        adminButton = (Button) workspace.lookup("#adminButton");
        boardText = (TextField) workspace.lookup("#boardText");
    }

    /**
     * Is executed when a copy button for an ID is clicked
     * @param currentBoard the board, whose ID was is to be copied
     */
    public void copyIDClicked(Board currentBoard) {
        overviewService.copyID(currentBoard);
    }

    /**
     * Shows on the screen that the ID of the board was copied
     */
    public void showIDCopiedMessage() {
        BoardView boardView = getSelectedBoardView();
        Label label = boardView.getCopiedLabel();
        label.setVisible(true);
        Button button = boardView.getCopyButton();
        button.setStyle("-fx-background-color: green;-fx-cursor: hand;");
    }

    /**
     * Hides from the screen the "Copied" message
     */
    public void hideIDCopiedMessage() {
        BoardView boardView = getSelectedBoardView();
        Label label = boardView.getCopiedLabel();
        label.setVisible(false);
        Button button = boardView.getCopyButton();
        button.setStyle("-fx-background-color: transparent;-fx-border-width:0;" +
                "-fx-cursor: hand;");
    }

    /**
     * Returns the number of times that the copy method was clicked, in the
     * last 2-second period
     * @return the number of clicks
     */
    public int getNumberOfTimesCopied() {
        return this.numberOfTimesCopied;
    }

    /**
     * Sets a new value for the number of clicks
     * @param numberOfTimesCopied the new value
     */
    public void setNumberOfTimesCopied(int numberOfTimesCopied) {
        this.numberOfTimesCopied = numberOfTimesCopied;
    }

    /**
     * Resets the join board text field
     */
    public void resetJoinBoardText () {
        boardText.setText("");
    }
    /**
     * Getter
     * @return tabPane instance height
     */
    public Double getTabPaneHeight() {
        return tabPane.getPrefHeight();
    }
    /**
     * Getter
     * @return tabPane instance
     */
    public Double getTabPaneWidth() {
        return tabPane.getPrefWidth();
    }


    /**
     * Getter
     * @return selected task (for the hover method)
     */
    public Task getSelectedTask() {
        return selectedTask;
    }

    /**
     * Setter
     * @param selectedTask the task that has been selected (by the hover method)
     */
    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }

    /**
     * Checks if ENTER shortcut can be executed
     */
    public void enableEnterShortcut(){
        if(selectedTask != null){
            overviewService.enterShortCut(selectedTask);
        }
    }

    /**
     * Checks if DEL / BACKSPACE shortcut can be executed
     */
    public void enableDelBackspaceShortcut(){
        if(selectedTask != null){
            overviewService.delBackspaceShortcut(selectedTask);
            selectedTask = null;
        }
    }

    /**
     * Checks if E shortcut can be executed
     */
    public void enableTitleShortcut(){
        if(selectedTask != null){
            overviewService.editTitleShortcut(selectedTask);
        }
    }

    /**
     * Changes title of a Task
     * @param titleLabel label that shows the title
     * @param editTitle text field for editing the title
     * @param stb SelectTaskButton associated with the Task
     */
    public void changeTaskTitle(Label titleLabel, TextField editTitle, SelectTaskButton stb){
        String newTitle = editTitle.getText();
        if(newTitle == null || newTitle.equals("")){
            newTitle = "Untitled";
        }
        titleLabel.setText(newTitle);
        editTitle.setVisible(false);
        titleLabel.setVisible(true);
        overviewService.changeTaskTitle(newTitle, stb);
        stb.requestFocus();
    }

    /**
     * Is called when shift+down is clicked
     */
    void enableShiftDownShortcut() {
        if(selectedTask == null) return ;
        overviewService.moveTask(selectedTask, 1);
    }

    /**
     * Is activated when shift+up is clicked
     */
    void enableShiftUpShortcut() {
        if(selectedTask == null) return ;
        overviewService.moveTask(selectedTask, -1);
    }

    /**
     * Is activated, when T is clicked
     */
    public void enableTagsShortcut() {
        if(selectedTask == null) return ;
        overviewService.showAddTag(selectedTask);
    }

    /**
     * Checks if UP shortcut can be executed
     */
    public void enableUpShortcut(){
        if(selectedTask != null){
            overviewService.upShortcut(selectedTask);
        }
    }

    /**
     * Checks if DOWN shortcut can be executed
     */
    public void enableDownShortcut(){
        if(selectedTask != null){
            overviewService.downShortcut(selectedTask);
        }
    }

    /**
     * Checks if LEFT shortcut can be executed
     */
    public void enableLeftShortcut(){
        if(selectedTask != null){
            overviewService.leftShortcut(selectedTask);
        }
    }

    /**
     * Checks if RIGHT shortcut can be executed
     */
    public void enableRightShortcut(){
        if(selectedTask != null){
            overviewService.rightShortcut(selectedTask);
        }
    }

    /**
     * Changes the selected task in a given task list view
     * @param taskListView the task list view that is to be updated
     * @param task  the task that is to be selected
     */
    public void changeSelectedTask(TaskListView taskListView, Task task) {
        taskListView.changeSelected(task);
    }

    /**
     *  Check if the selected board view
     *  is password protected
     * @return true or false, based on password protection
     */
    public boolean isCurrentPasswordProtected(){
        var selectedBoardView = getSelectedBoardView();
        if(selectedBoardView == null){
            return false;
        }
        return selectedBoardView.isWriteAccess();
    }

    /**
     * Getter
     * @return instance of overview service
     */
    public OverviewService getOverviewService() {
        return overviewService;
    }

    /**
     * Getter
     * @return whether an admin is using the app
     */
    public boolean isInAdmin() {
        return inAdmin;
    }

    /**
     * Adds recent board to workspace
     * @param id id of the new board
     */
    public void addRecentBoard(Long id){
        workspace.addRecentBoard(id);
    }

    /**
     * Clears the fields in the change password scene
     */
    public void setEmpty(){
        this.repeatPass.setText("");
        this.newPass.setText("");
    }
}
