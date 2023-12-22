/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.components.BoardView;
import client.components.TaskListView;
import client.components.TaskView;
import client.exceptions.WebSocketConnectionException;
import client.utils.ServerUtils;
import client.utils.Storage;
import client.utils.WebSocketClientConfig;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class MainCtrl {
    private Stage primaryStage;
    private OverviewController overviewCtrl;
    private TaskViewController taskViewController;
    private WebSocketClientConfig webSocketClientConfig;
    private ServerConnectController serverConnectController;
    private RenameTaskListController renameTaskListController;
    private ColorManagementController colorManagementController;
    private EditTagController editTagController;
    private EditSubtaskController editSubtaskController;
    private TaskColorPresetsController taskColorPresetsController;
    private Scene editTask;
    private Scene renameTaskList;
    private Scene overview;
    private Scene addSubTask;
    private Scene serverConnect;
    private Scene colorManagement;
    private Scene editTag;
    private Scene editSubtask;
    private Scene changePass;
    private Scene taskColorPresets;
    private ServerUtils serverUtils;
    private TextInputDialog textInputDialog;
    private Map<Long, BoardView> boardViewMap;
    private Map<Long, TaskListView> taskListViewMap;
    private Storage storage;

    /**
     * Callback for handling a payload of type Board
     * received from the WebSocket connection
     */
    private final Consumer<Board> boardConsumer =
        payload -> {
            if(boardViewMap.containsKey(payload.getId())){
                var curr = boardViewMap.get(payload.getId());
                if(!curr.getAssociatedBoard().getPassword().equals(payload.getPassword())){
                    if(payload.getPassword().equals("")){
                        curr.setWriteAccess(true);
                        taskViewController.setWriteAccess(true);
                    } else {
                        curr.setWriteAccess(false);
                        taskViewController.setWriteAccess(false);
                    }
                    this.removeBoardPass(payload.getId());
                    this.saveStorage();
                    if(taskViewController != null
                            && taskViewController.getCurrentTaskView() != null
                            && taskViewController.getCurrentTask() != null) {
                        Platform.runLater(() ->
                                taskViewController.displayTask(
                                        taskViewController.getCurrentTask()));
                    }
                }
                Platform.runLater(() -> curr.updateOverview(payload));
            }
        };

    /**
     * Callback for handling a payload of type TaskList
     * received from the WebSocket connection
     */
    private final Consumer<TaskList> taskListConsumer = payload -> {
        if(taskListViewMap.containsKey(payload.getId())){
            var curr = taskListViewMap.get(payload.getId());
            Platform.runLater(() -> curr.updateOverview(payload));
        }
    };

    /**
     * Callback for handling a payload of type Task
     * received from the WebSocket connection
     */
    private final Consumer<Task> taskConsumer = payload -> Platform.runLater(() -> {
        if(taskListViewMap.containsKey(payload.getTaskListID())){
            var curr = taskListViewMap.get(payload.getTaskListID());
            var boardView = boardViewMap.get(curr.getAssociatedTaskList().getBoardId());
            Board updatedBoard = new Board(boardView.getAssociatedBoard());
            updatedBoard.insertOrReplace(payload);
            Platform.runLater(() -> boardView.updateOverview(updatedBoard));
        }
        if(taskViewController.getCurrentTaskView() != null
            && taskViewController.getCurrentTask().getId().equals(payload.getId())
            && !taskViewController.getCurrentTask().equals(payload)){
            Platform.runLater(() ->
                taskViewController.getCurrentTaskView().updateOverview(payload));
        }
    });
    /**
     * Not an actual implementation
     */
    private final Consumer<Tag> tagConsumer = payload -> {
        //Not actual implementation
        //taskViewController.refreshTask(payload);
    };
    /**
     * Not an actual implementation
     */
    private final Consumer<SubTask> subTaskConsumer = payload -> {
        //Not actual implementation
        //taskViewController.refreshTask(payload);
    };

    /**
     * Initializes the main controller, injecting parameters automatically
     *
     * @param primaryStage          Primary stage object used to display the current scene
     * @param serverConnection      Pair of the starting scene and its controller
     * @param overview              Pair of the overview scene and its controller
     * @param editTask              Pair of the edit task scene and its controller
     * @param addSubTask            Pair of the add subTask popup scene and its controller
     * @param renameTaskList        Pair of the renameTaskList scene and its controller
     * @param colorManagement       Pair of the add colorManagement scene and its controller
     * @param editTag               Pair of the editTag scene and its controller
     * @param editSubtask           Pair of the editSubtask scene and its controller
     * @param changePass            Pair of the change password scene and its controller
     * @param taskColorPresets      Pair of the taskColorPreset scene and its controller
     * @param webSocketClientConfig webSocketClientConfig
     * @param serverUtils           serverUtils
     */
    public void initialize(Stage primaryStage,
                           Pair<ServerConnectController, Parent> serverConnection,
                           Pair<OverviewController, Parent> overview,
                           Pair<TaskViewController, Parent> editTask,
                           Pair<AddSubTaskController, Parent> addSubTask,
                           Pair<RenameTaskListController, Parent> renameTaskList,
                           Pair<ColorManagementController, Parent> colorManagement,
                           Pair<EditTagController, Parent> editTag,
                           Pair<EditSubtaskController, Parent> editSubtask,
                           Pair<OverviewController, Parent> changePass,
                           Pair<TaskColorPresetsController, Parent> taskColorPresets,
                           WebSocketClientConfig webSocketClientConfig,
                           ServerUtils serverUtils) {
        this.primaryStage = primaryStage;

        this.serverConnectController = serverConnection.getKey();
        this.serverConnect = new Scene(serverConnection.getValue());
        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addSubTask = new Scene(addSubTask.getValue());
        this.editTask = new Scene(editTask.getValue());
        this.taskViewController = editTask.getKey();
        this.renameTaskListController = renameTaskList.getKey();
        this.renameTaskList = new Scene(renameTaskList.getValue());
        this.colorManagementController = colorManagement.getKey();
        this.colorManagement = new Scene(colorManagement.getValue());
        this.editTag = new Scene(editTag.getValue());
        this.editTagController = editTag.getKey();
        this.editSubtask = new Scene(editSubtask.getValue());
        this.editSubtaskController = editSubtask.getKey();
        this.changePass = new Scene(changePass.getValue());

        this.taskColorPresets = new Scene(taskColorPresets.getValue());
        this.taskColorPresetsController = taskColorPresets.getKey();
        this.serverUtils = serverUtils;
        this.webSocketClientConfig = webSocketClientConfig;

        this.boardViewMap = new HashMap<>();
        this.taskListViewMap = new HashMap<>();

        linkCSSFiles();

        showServerConnect();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            if(primaryStage.getScene().equals(this.overview)){
                serverUtils.stopLongPolling();
            }
        });
        primaryStage.show();

        this.loadServers();
    }

    /**
     * Links all scenes to their respective CSS files
     */
    private void linkCSSFiles() {
        try {
            serverConnect.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("css/main.css")).toExternalForm());
            overview.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("css/main.css")).toExternalForm());
            editTask.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("css/main.css")).toExternalForm());
            addSubTask.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("css/main.css")).toExternalForm());
            renameTaskList.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("css/main.css")).toExternalForm());
        } catch (Exception e){
            System.err.println("Error loading css");
        }
    }

    /**
     * Get the boardView by the id of a board
     *
     * @param id id of a board
     * @return the boardView
     */
    public BoardView getBoardViewById(Long id){
        return this.boardViewMap.get(id);
    }
    /**
     * Get the taskListView by the id of a taskList
     *
     * @param id id of a taskList
     * @return the TaskListView
     */
    public TaskListView getTaskListViewById(Long id){
        return this.taskListViewMap.get(id);
    }
    /**
     * Clear the boardView map
     */
    public void clearBoardViewMap(){
        this.boardViewMap.clear();
    }

    /**
     * Clear the taskListView map
     */
    public void clearTaskListViewMap(){
        this.taskListViewMap.clear();
    }
    /**
     * Add a boardView to the map of boardId and boardView
     *
     * @param id id of the board
     * @param boardView boardView of the board
     */
    public void addBoardView(Long id, BoardView boardView) {
        this.boardViewMap.put(id, boardView);
    }

    /**
     * Delete a boardView from the map of boardId and boardView
     *
     * @param id id of the board
     */
    public void deleteBoardView(Long id) {
        this.boardViewMap.remove(id);
    }

    /**
     * Add a taskListView to the map of taskListId and taskListView
     *
     * @param id id of the taskList
     * @param taskListView taskListView of the taskList
     */
    public void addTaskListView(Long id, TaskListView taskListView) {
        taskListViewMap.put(id, taskListView);
    }

    /**
     * Delete a taskListView from the map of taskListId and taskListView
     *
     * @param id id of the taskList
     */
    public void deleteTaskListView(Long id) {
        this.taskListViewMap.remove(id);
    }

    /**
     * Get boardView that a task belongs to
     *
     * @param task a task in a boardView
     * @return the boardView that the given task belongs to
     */
    public BoardView getBoardViewFromTask(Task task){
        var taskListView = taskListViewMap.get(task.getTaskListID());
        if(taskListView == null)
            return null;
        return boardViewMap.get(taskListView.getAssociatedTaskList().getBoardId());
    }

    /**
     * Displays the overview over the primary stage
     */
    public void showOverview() {
        Parent root = overview.getRoot();
        for (var tmp : root.lookupAll("*")) {
            if (tmp instanceof TabPane) {
                ((TabPane) tmp).prefWidthProperty().bind(overview.widthProperty());
            } else if (tmp instanceof ToolBar) {
                ((ToolBar) tmp).prefWidthProperty().bind(overview.widthProperty());
            }
        }
        enableHelpShortcut(overview);
        enableShortcuts();
        primaryStage.setScene(overview);
        overviewCtrl.addWorkspace();
        serverUtils.pollBoard(boardId ->
            Platform.runLater(() -> {
                BoardView boardView = getBoardViewById(boardId);
                if(boardViewMap.containsKey(boardId)){
                    overviewCtrl.closeTabClicked(boardView);
                    closeBoardPopups();
                    showBoardRemoved();
                }
                if(getAllBoardsForCurrent().contains(boardId)){
                    removeRecentBoard(boardId);
                    overviewCtrl.removeRecent(boardView.getAssociatedBoard().getTitle(), boardId);
                }
            })
        );
    }

    /**
     * Shows server connection screen.
     */
    public void showServerConnect() {
        primaryStage.setTitle("Talio");
        primaryStage.setScene(serverConnect);
    }

    /**
     * Display
     */
    public void loadServers(){
        serverConnectController.loadRecentServer();
    }

    /**
     * Displays the editTag scene
     * @param tag the tag to be edited
     * @param taskViewController the taskViewController in which the tag exists.
     * @param associatedTask the task associated with this tag
     */
    public void showEditTagScene(Tag tag, TaskViewController taskViewController,
                                 Task associatedTask) {
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.initOwner(editTask.getWindow());
        popUpWindow.setTitle("Edit Tag");
        popUpWindow.setScene(editTag);
        editTagController.displayTag(tag);
        editTagController.setAssociatedTask(associatedTask);
        if(taskViewController == null) {
            editTagController.setTaskView(null);
        } else {
            editTagController.setTaskView(taskViewController.getCurrentTaskView());
        }
        editTagController.setStage(popUpWindow);
        popUpWindow.showAndWait();
    }

    /**
     * Displays the edit subtask scene
     * @param subtask the subtask to be edited
     * @param taskView the taskView in which the subtask exists.
     */
    public void showEditSubtaskScene(SubTask subtask, TaskView taskView) {
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.initOwner(editTask.getWindow());
        popUpWindow.setTitle("Edit subtask");
        popUpWindow.setScene(editSubtask);
        editSubtaskController.displaySubtask(subtask);
        editSubtaskController.setTaskView(taskView);
        editSubtaskController.setStage(popUpWindow);
        popUpWindow.showAndWait();
    }
    /**
     * Displays the change password scene
     */
    public void showChangePassword() {
        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        popUpWindow.setTitle("Change password");
        popUpWindow.setScene(changePass);
        overviewCtrl.setEmpty();
        popUpWindow.showAndWait();
    }

    /**
     * Displays the edit task scene to the primary stage
     * It automatically loads the task parameters using the refreshTask method
     *
     * @param task the current task
     */
    public void showEditTask(Task task) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Edit task");
        popup.setScene(editTask);
        if(overviewCtrl.isInAdmin()){
            taskViewController.setWriteAccess(true);
        } else {
            taskViewController.setWriteAccess(overviewCtrl.isCurrentPasswordProtected());
        }
        taskViewController.displayTask(task);
        serverUtils.poll(task.getId(), id -> {
            Platform.runLater(taskViewController::closeAddSubtaskWindow);
            Platform.runLater(() -> {
                if(popup.isShowing()){
                    this.showTaskRemoved();
                    popup.close();
                }
            });
        });
        popup.setOnCloseRequest(event -> serverUtils.stopLongPolling());
        editTask.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ESCAPE){
                popup.close();
            }
        });
        popup.showAndWait();
    }

    /**
     * Lets the user know a Task was removed.
     */
    public void showTaskRemoved(){
        Alert inform = new Alert(Alert.AlertType.INFORMATION);
        inform.setTitle("Task Deleted");
        inform.setContentText("This task was deleted by someone else.");
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        inform.getButtonTypes().setAll(ok);
        inform.show();
    }

    /**
     * Informs the user that a board they had open was removed.
     */
    public void showBoardRemoved(){
        Alert inform = new Alert(Alert.AlertType.INFORMATION);
        inform.setTitle("Board Deleted");
        inform.setContentText("This board was deleted by someone else.");
        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        inform.getButtonTypes().setAll(ok);
        inform.show();
    }

    /**
     * Displays the color management scene to the primary stage
     *
     * @param board associated board
     */
    public void showColorManagement(Board board){
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Color management");
        popup.setScene(colorManagement);
        colorManagementController.displayColorView(board);
        popup.showAndWait();
    }

    /**
     * Returns the add tag scene
     * The scene is used for the popup dialog
     *
     * @return the scene
     */
    public Scene getAddSubTask() {
        return addSubTask;
    }

    /**
     * Helper method that sends message to the WebSocket Server Configuration
     *
     * @param payload message
     * @param <T>     the type of the message
     */
    public <T> void sendToOthers(T payload) {
        webSocketClientConfig.sendToOthers(payload);
    }

    /**
     * Test connection to WebSocket server
     *
     * @param url given url with WS protocol
     */
    public void testConnectionToWebSocket(String url) {
        try {
            webSocketClientConfig.connect(url);
        } catch (WebSocketConnectionException e) {
            System.err.println(e.getMessage());
        }
        subscribeToBrokers();
    }

    /**
     * Subscribes to all message brokers
     */
    private void subscribeToBrokers() {
        try {
            if (!webSocketClientConfig.isConnected()) {
                throw new WebSocketConnectionException("There is no open connection");
            }
            webSocketClientConfig.subscribe("/update/tag", Tag.class, tagConsumer);
            webSocketClientConfig.subscribe("/update/subtask", SubTask.class, subTaskConsumer);
            webSocketClientConfig.subscribe("/update/task", Task.class, taskConsumer);
            webSocketClientConfig.subscribe("/update/taskList", TaskList.class, taskListConsumer);
            webSocketClientConfig.subscribe("/update/board", Board.class, boardConsumer);
        } catch (WebSocketConnectionException e) {
            System.err.println(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to connect to WebSocket");
                alert.setContentText("Realtime communication with others is not enabled!");
                alert.showAndWait();
            });
        }
    }

    /**
     * Show a pop-up (load a scene) to rename a TaskList
     *
     * @param taskListView the taskListView of the task list to rename
     */
    public void renameTaskListClicked(TaskListView taskListView) {
        if(!overviewCtrl.getSelectedBoardView().isWriteAccess()
            && !overviewCtrl.isInAdmin()){
            overviewCtrl.showError("You do not have the right write access");
            return;
        }
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Rename Task List");
        popup.setScene(renameTaskList);
        renameTaskListController.refresh(taskListView, popup);
        popup.showAndWait();
    }

    /**
     * Getter for ServerUtils
     * @return ServerUtils
     */
    public ServerUtils getServerUtils() {
        return serverUtils;
    }

    /**
     * This method is called when a delete button is clicked, by a task
     * @param task the task to be deleted
     */
    public void deleteTaskClicked(Task task) {
        var boardView = this.getBoardViewFromTask(task);
        var updatedBoard = new Board(boardView.getAssociatedBoard());
        updatedBoard.removeTask(task);
        serverUtils.removeTask(task.getId());
        updatedBoard = serverUtils.saveBoard(updatedBoard);
        boardView.updateOverview(updatedBoard);

        this.sendToOthers(updatedBoard);
    }

    /**
     * Remember board as recent for current server
     * @param boardID board to be remembered
     */
    public void rememberBoard(Long boardID){
        storage.addBoard(serverUtils.getServer(), boardID);
    }

    /**
     * Get all servers that have been remembered
     * @return set of all remembered servers
     */
    public Set<String> getAllServers(){
        if(storage == null){
            storage = new Storage();
        }
        return storage.getAllServers();
    }

    /**
     * Saves hashmap to file in the local system
     */
    public void saveStorage(){
        storage.serialize();
    }

    /**
     * Add server to hashmap
     * @param server to be remembered
     */
    public void rememberServer(String server){
        storage.rememberServer(server);
    }
    /**
     * Get all recent boards for current server
     * @return list of board ids
     */
    public List<Long> getAllBoardsForCurrent(){
        return storage.getAllBoardsForServer(serverUtils.getServer());
    }

    /**
     * Remove server from hashmap
     * @param server to be removed
     */
    public void removeServer(String server){
        this.storage.removeServer(server);
    }

    /**
     * Removes board from current server
     * @param id target board
     */
    public void removeRecentBoard(Long id){
        this.storage.removeRecentBoard(serverUtils.getServer(), id);
    }
    /**
     * Sets the clipboard content to the given string
     * @param str the string for the clipboard
     */

    public void setClipboardContent(String str) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(str);
        clipboard.setContent(content);
    }

    /**
     * Utility method that saves password for a board
     * @param id board id
     * @param password new password
     */
    public void addBoardPass(Long id, String password){
        this.storage.addBoardPass(id, password);
    }

    /**
     * Utility method that removes saved password
     * for a given board
     * @param id board id
     */
    public void removeBoardPass(Long id){
        this.storage.removeBoardPass(id);
    }

    /**
     * Utility method that retrieves saved password
     * from storage
     * @param id board id
     * @return saved password or null if no such pass is found
     */
    public String getPasswordBoard(Long id){
        return this.storage.getPasswordBoard(id);
    }
    /**
     * Gets all tags from a board
     *
     * @param board the board to get tags from
     * @return a list of tags
     */
    public List<Tag> getAllTagsFromBoard(Board board){
        return getServerUtils().getAllTagsFromBoard(board);
    }

    /**
     * Enables help screen in a scene
     * @param scene the respective scene
     */
    public void enableHelpShortcut(Scene scene){
        KeyCombination help = new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHIFT_DOWN);
        Runnable helpEvent = () -> {
            try {
                showHelpScreen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        scene.getAccelerators().put(help, helpEvent);
    }

    /**
     * Utility method that shows popup for
     * password operations
     * @param title of the popup
     * @param content of the popup
     * @return result
     */
    public Optional<String> showPasswordPopUp(String title, String content){
        textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(content);
        textInputDialog.getDialogPane().setGraphic(null);

        overviewCtrl.setPasswordField(new PasswordField());
        overviewCtrl.getPasswordField().setStyle("-fx-max-width: 50; -fx-max-height: 20");
        textInputDialog.getDialogPane().setContent(overviewCtrl.getPasswordField());
        return textInputDialog.showAndWait();
    }

    /**
     * Shows help screen
     * @throws IOException if file is not found (it will always be found)
     */
    public void showHelpScreen() throws IOException {
        Stage help = new Stage();
        help.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/client/scenes/help.fxml"));
        Parent root = helpLoader.load();
        Scene helpScene = new Scene(root);
        help.setScene(helpScene);
        help.showAndWait();
    }

    private boolean editingTitle = false;

    /**
     * Displays the popup which allows to select a color preset for
     * task
     */
    public void showTaskColorPresets() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Color preset selection");
        popup.setScene(taskColorPresets);
        popup.showAndWait();
    }

    /**
     * Enables shortcuts for the overview:
     * ENTER
     * Enables shortcuts for the overview
     */
    public void enableShortcuts(){
        addEventFilters();
        overview.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.C) {
                taskColorPresetsController.enableColorsShortcut(overviewCtrl.getSelectedTask());
                event.consume();
            }
            if(event.getCode() == KeyCode.ENTER && !editingTitle){
                overviewCtrl.enableEnterShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE){
                overviewCtrl.enableDelBackspaceShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.E){
                overviewCtrl.enableTitleShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.T){
                overviewCtrl.enableTagsShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.UP){
                if(event.isShiftDown()){
                    overviewCtrl.enableShiftUpShortcut();
                    event.consume();
                }
            }
            if(event.getCode() == KeyCode.DOWN){
                if(event.isShiftDown()){
                    overviewCtrl.enableShiftDownShortcut();
                    event.consume();
                }
            }
        });
    }

    /**
     * Adds event filters for keyboard shortcuts.
     */
    public void addEventFilters(){
        overview.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(!event.isShiftDown()) {
                if(event.getCode() == KeyCode.UP) {
                    overviewCtrl.enableUpShortcut();
                    event.consume();
                }
                if(event.getCode() == KeyCode.DOWN) {
                    overviewCtrl.enableDownShortcut();
                    event.consume();
                }
            }
            if(event.getCode() == KeyCode.LEFT){
                overviewCtrl.enableLeftShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.RIGHT){
                overviewCtrl.enableRightShortcut();
                event.consume();
            }
            if(event.getCode() == KeyCode.E){
                editingTitle = true;
            }
        });
        overview.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.E){
                editingTitle = false;
            }
        });

    }

    /**
     * Shows error
     * @param message message
     */
    public void showError(String message){
        overviewCtrl.showError(message);
    }

    /**
     * Getter
     * @return if the user is admin
     */
    public boolean isInAdmin(){
        return overviewCtrl.isInAdmin();
    }

    /**
     * Closes board popups, if they are showing
     */
    public void closeBoardPopups(){
        if(renameTaskList.getWindow() != null && renameTaskList.getWindow().isShowing()){
            renameTaskList.getWindow().hide();
        }
        if(editTask.getWindow() != null && editTask.getWindow().isShowing()){
            editTask.getWindow().hide();
        }
        if(colorManagement.getWindow() != null && colorManagement.getWindow().isShowing()){
            colorManagement.getWindow().hide();
        }
        if(taskColorPresets.getWindow() != null && taskColorPresets.getWindow().isShowing()){
            taskColorPresets.getWindow().hide();
        }
        if(editTag.getWindow() != null && editTag.getWindow().isShowing()){
            editTag.getWindow().hide();
        }
        if(changePass.getWindow() != null && changePass.getWindow().isShowing()){
            changePass.getWindow().hide();
        }
        if(textInputDialog != null && textInputDialog.isShowing()){
            textInputDialog.close();
        }
    }
}