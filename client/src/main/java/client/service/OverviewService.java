package client.service;

import client.components.BoardView;
import client.components.SelectTaskButton;
import client.components.TaskListView;
import client.components.Workspace;
import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import client.utils.ServerUtils;
import commons.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class OverviewService {
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private OverviewController overviewController;
    /**
     * An injectable constructor for this class
     *
     * @param mainCtrl    the main controller
     * @param serverUtils the server utilities
     */
    @Inject
    public OverviewService(MainCtrl mainCtrl,
                           ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
    }

    /**
     * A setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param overviewController the controller to which this service belongs
     */
    public void setOverviewController(OverviewController overviewController) {
        this.overviewController = overviewController;
    }

    /**
     * A getter for the controller. This is required for the testing.
     * @return the getOverview controller
     */
    public OverviewController getOverviewController() {
        return overviewController;
    }

    /**
     * Performs actions needed to join a given board
     *
     * @param idString the id of the board we want to join
     */
    public void joinBoard(String idString) {
        Board current;
        try{
            var id = Long.parseLong(idString);
            current = serverUtils.getBoardByID(id);
        } catch (NumberFormatException e) {
            current = serverUtils.getBoardByUserID(idString);
        }
        var openedBoards = overviewController.getOpenedBoards();
        if (current == null) {
            overviewController.showError("No board with the given ID was found. Please try again.");
            return;
        }
        if (openedBoards.contains((current.getId()))) {
            overviewController.showBoardAlreadyOpenError();
            return;
        }
        overviewController.resetJoinBoardText();
        openedBoards.add(current.getId());
        if(!mainCtrl.getAllBoardsForCurrent().contains(current.getId())) {
            mainCtrl.rememberBoard(current.getId());
            mainCtrl.saveStorage();
            overviewController.addRecentBoard(current.getId());
        }
        overviewController.displayBoard(current);
    }

    /**
     * Utility method that checks if the password is correct
     * by sending request to the server
     * @param password to check
     * @return success of the operation
     */
    public boolean checkBoardPassword(String password){
        var currentBoardView = overviewController.getSelectedBoardView();
        var hash = currentBoardView.getAssociatedBoard().hashPassword(password);
        var result = serverUtils.checkBoardPassword(
                currentBoardView.getAssociatedBoard().getId(), hash);
        if(result != null){
            currentBoardView.setWriteAccess(true);
            currentBoardView.updateOverview(result);
            mainCtrl.addBoardPass(result.getId(), hash);
            mainCtrl.saveStorage();
            mainCtrl.sendToOthers(result);
        }
        return result != null;
    }

    /**
     * Utility method that checks if the password is correct
     * by sending request to the server
     * @param password to check
     * @param board current board
     * @return success of the operation
     */
    public boolean checkBoardPassword(Board board, String password){
        var result = serverUtils.checkBoardPassword(
                board.getId(), password);
        if(result != null){
            mainCtrl.sendToOthers(result);
        }
        return result != null;
    }
    /**
     * Utility method that updates the password
     * for the current board by sending request to
     * the server
     * @param password new password
     */
    public void updatePassword(String password){
        var boardView = overviewController.getSelectedBoardView();
        var updatedBoard = new Board(boardView.getAssociatedBoard());
        //automatic hashing using sha-256
        var hash = updatedBoard.hashPassword(password);
        updatedBoard.setPassword(hash);
        updatedBoard = serverUtils.saveBoard(updatedBoard);
        boardView.setWriteAccess(true);
        boardView.updateOverview(updatedBoard);
        if(!overviewController.isInAdmin()) {
            mainCtrl.addBoardPass(updatedBoard.getId(), hash);
            mainCtrl.saveStorage();
        }
        mainCtrl.sendToOthers(updatedBoard);
    }

    /**
     * Method that deletes task from overview
     * @param task to be deleted
     */
    public void removeTaskClicked(Task task) {
        var boardView = overviewController.getSelectedBoardView();
        var updatedBoard = serverUtils.getBoardByID(
                overviewController.getSelectedBoardView()
                        .getAssociatedBoard().getId());
        updatedBoard.removeTask(task);
        serverUtils.removeTask(task.getId());
        updatedBoard = serverUtils.saveBoard(updatedBoard);
        boardView.updateOverview(updatedBoard);
        mainCtrl.sendToOthers(updatedBoard);
    }
    /**
     * Method that sends request to server to remove password
     * @param password to be checked
     * @return success of the operation
     */
    public boolean removePassword(String password){
        var boardView = overviewController.getSelectedBoardView();
        var updatedBoard = new Board(boardView.getAssociatedBoard());
        //automatic hashing using sha-256
        var hash = updatedBoard.hashPassword(password);
        updatedBoard = serverUtils.removePassword(updatedBoard.getId(), hash);
        if(updatedBoard == null)
            return false;
        boardView.setWriteAccess(true);
        boardView.updateOverview(updatedBoard);
        mainCtrl.removeBoardPass(updatedBoard.getId());
        mainCtrl.saveStorage();
        mainCtrl.sendToOthers(updatedBoard);
        return true;
    }
    /**
     * Method that sends request to server to remove password
     */
    public void removePasswordAdmin(){
        var boardView = overviewController.getSelectedBoardView();
        var updatedBoard = new Board(boardView.getAssociatedBoard());
        updatedBoard = serverUtils.removePassword(updatedBoard.getId());
        if(updatedBoard == null)
            return;
        boardView.setWriteAccess(true);
        boardView.updateOverview(updatedBoard);
        mainCtrl.removeBoardPass(updatedBoard.getId());
        mainCtrl.saveStorage();
        mainCtrl.sendToOthers(updatedBoard);
    }
    /**
     * Method that creates new board
     * and saves it to the server
     * @return the created board
     */
    public Board createNewBoard(){
        Board board = new Board();
        board.setTitle("Untitled board");
        board.setBoardBgColor("#f2f2f2");
        board.setTaskColorPresets(new ArrayList<>());
        board.setBoardFontColor("#000000");
        board.setTaskListsBgColor("#e6e6e6");
        board.setTaskListsFontColor("#00000");
        board.setTaskListsFontColor("#000000");
        board.setTaskLists(new ArrayList<>());
        board.setUserId("");
        board.setPassword("");
        ColorEntity colorEntity = new ColorEntity();
        colorEntity.setBackGroundColor("#cccccc");
        colorEntity.setFontColor("#000000");
        board.setTaskDefaultColor(colorEntity);
        board = serverUtils.saveBoard(board);
        mainCtrl.rememberBoard(board.getId());
        mainCtrl.saveStorage();
        return board;
    }
    /**
     * Method that gets all boards for admin mode
     * @return list of all boards in the server
     */
    public List<Board> getAllBoards(){
        var boards = serverUtils.getAllBoards();
        if(boards == null){
            throw new IllegalArgumentException("Retrieved boards from server are null");
        } else {
            return boards;
        }
    }

    /**
     * Check the authenticity of the provided admin password
     * @param password given password
     * @return boolean
     */
    public boolean checkAdminPassword(String password){
        return serverUtils.checkAdminPassword(password);
    }
    /**
     * Adds a new taskList to the board and sends it to the server
     * @param boardView current displayed board
     */
    public void addNewTaskList(BoardView boardView) {
        TaskList newTaskList = new TaskList();
        newTaskList.setTitle("Title");
        newTaskList.setBoardId(boardView.getAssociatedBoard().getId());
        newTaskList.setTasks(new ArrayList<>());

        TaskList addedTaskList = serverUtils.addNewTaskList(newTaskList);

        // If failed to add a TaskList to the server
        if (addedTaskList == null) {
            overviewController.showError("Please enter correct board_id");
            return;
        }
        Board updatedBoard = new Board(boardView.getAssociatedBoard());
        updatedBoard.addNewTaskList(addedTaskList);
        boardView.updateOverview(updatedBoard);
        mainCtrl.sendToOthers(updatedBoard);
    }

    /**
     * Removes a given taskList from the board and sends it to the server
     * @param boardView current displayed board
     * @param taskListView the taskListView of the taskList to be removed
     */
    public void removeTaskList(TaskListView taskListView, BoardView boardView) {
        TaskList removedTaskList = serverUtils.removeTaskList(taskListView.getAssociatedTaskList());
        if (removedTaskList == null) {
            overviewController.showError("Please enter correct board_id");
            return;
        }
        Board updatedBoard = new Board(boardView.getAssociatedBoard());
        for(Task t: removedTaskList.getTasks()){
            serverUtils.removeTask(t.getId());
        }
        updatedBoard.removeTaskList(removedTaskList);
        boardView.updateOverview(updatedBoard);

        mainCtrl.sendToOthers(updatedBoard);
    }

    /**
     * Adds a new task to the given taskList (and sends it to the server)
     * @param taskListView the taskList to which to add a new task
     */
    public void addNewTask(TaskListView taskListView) {
        Board updatedBoard = new Board(
                overviewController.getSelectedBoardView().getAssociatedBoard());

        Task task = new Task();
        task.setTitle("new task");
        task.setDescription("");
        task.setBackGroundColor(updatedBoard.getTaskDefaultColor().getBackGroundColor());
        task.setFontColor(updatedBoard.getTaskDefaultColor().getFontColor());
        task.setSubTasks(new ArrayList<>());
        task.setTags(new ArrayList<>());
        task.setTaskListID(taskListView.getAssociatedTaskList().getId());
        task = serverUtils.addNewTask(task);

        updatedBoard.insertOrReplace(task);
        overviewController.getSelectedBoardView().updateOverview(updatedBoard);

        mainCtrl.sendToOthers(updatedBoard);
    }

    /**
     * Utility method that updates the title of a board
     * @param newTitle given board, whose title will be updated
     * @param boardView the board view in which the title should be updated
     */
    public void updateTitle(String newTitle, BoardView boardView){
        Board newBoard = boardView.getAssociatedBoard();
        newBoard.setTitle(newTitle);
        newBoard = serverUtils.saveBoard(newBoard);
        boardView.updateOverview(newBoard);
        mainCtrl.sendToOthers(newBoard);
    }

    /**
     * Deletes board from server
     * and form recent boards
     * @param board to be deleted
     */
    public void deleteRecentBoard(Board board) {
        serverUtils.removeBoard(board);
        mainCtrl.removeRecentBoard(board.getId());
        mainCtrl.saveStorage();
    }

    /**
     * Utility method that saves the current state
     * of the storage in a file using serialization
     */
    public void saveStorage(){
        mainCtrl.saveStorage();
    }

    /**
     * Utility method that handles the clicking of the
     * disconnect button
     */
    public void disconnectClicked(){
        serverUtils.stopLongPolling();
        mainCtrl.showServerConnect();
        mainCtrl.loadServers();
    }

    /**
     * Utility method that clears the maps
     * which is invoked when user leaves admin mode
     */
    public void clearMaps(){
        mainCtrl.clearBoardViewMap();
        mainCtrl.clearTaskListViewMap();
    }

    /**
     * Utility method that removes boardView from map
     * in mainCtrl
     * @param id of the board to be removed
     */
    public void removeBoardView(Long id){
        mainCtrl.deleteBoardView(id);
    }

    /**
     * Utility method that removes taskListView from map
     * in mainCtrl
     * @param id of the taskList to be removed
     */
    public void removeTaskListView(Long id){
        mainCtrl.deleteTaskListView(id);
    }

    /**
     * Utility method that opens the color management
     * panel with the current color presets for a board
     * @param boardView currently opened boardView
     */
    public void showColorManagement(BoardView boardView){
        mainCtrl.showColorManagement(boardView.getAssociatedBoard());
    }

    /**
     * Utility method that creates new BoardView
     * @param board new board to create board view from
     * @return crated board view
     */
    public BoardView createBoardView(Board board){
        return new BoardView(board, mainCtrl, overviewController);
    }

    /**
     * Utility method that creates new Workspace instance
     * @return created Workspace instance
     */
    public Workspace createWorkspace(){
        return new Workspace(mainCtrl, overviewController,
                (int) (overviewController.getTabPaneHeight() * 0.9),
                (int) (overviewController.getTabPaneWidth() * 0.95),
                this.getServerURL());
    }
    /**
     * Returns the URL of the server
     * @return the url of the server
     */
    public String getServerURL() {
        return serverUtils.getServer();
    }

    /**
     * Copies the id of a given board to the clipboard and shows
     * @param board the board whose ID was copied
     */
    public void copyID(Board board) {

        mainCtrl.setClipboardContent(board.getUserId());

        overviewController.showIDCopiedMessage();

        overviewController.setNumberOfTimesCopied(
                overviewController.getNumberOfTimesCopied() + 1
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        overviewController.setNumberOfTimesCopied(
                                overviewController.getNumberOfTimesCopied() - 1
                        );
                        if(overviewController.getNumberOfTimesCopied() == 0) {
                            overviewController.hideIDCopiedMessage();
                        }
                    }
                },
                500
        );
    }

    /**
     * Executes the ENTER shortcut for the selected Task
     * @param task selected Task
     */
    public void enterShortCut(Task task){
        mainCtrl.showEditTask(task);
    }

    /**
     * Executes DEL / BACKSPACE shortcut for the selected Task
     * @param task selected Task
     */
    public void delBackspaceShortcut(Task task){
        mainCtrl.deleteTaskClicked(task);
    }

    /**
     * Executes E shortcut
     * @param task selected Task
     */
    public void editTitleShortcut(Task task){
        var taskListId = (Long) task.getTaskListID();
        var associatedTaskListView = (TaskListView) mainCtrl.getTaskListViewById(taskListId);
        var associatedSTB = (SelectTaskButton) associatedTaskListView.getSelectedSTB(task.getId());
        if(associatedSTB != null){
            associatedSTB.editTitle();
        }
    }

    /**
     * Changes title of Task in the DB
     * @param newTitle new title
     * @param stb SelectTaskButton associated with the Task
     */
    public void changeTaskTitle(String newTitle, SelectTaskButton stb){
        Task newTask = stb.getAssociatedTask();
        newTask.setTitle(newTitle);
        newTask = serverUtils.saveTaskByID(newTask);
        stb.updateOverview(newTask);
        mainCtrl.sendToOthers(newTask);
    }

    /**
     * Swaps to elements in a list
     * @param list the list
     * @param i1 index of the first element
     * @param i2 index of the second element
     * @param <T> the type of the items in the list
     */
    private <T> void swap(List<T> list, int i1, int i2) {
        T first = list.get(i1);
        list.set(i1, list.get(i2));
        list.set(i2, first);
    }
    /**
     * Moves a task in its taskList by position delta
     * @param task the task to move
     * @param delta the change in position
     */
    public void moveTask(Task task, int delta) {
        TaskList taskList = new TaskList(mainCtrl.getTaskListViewById(
                task.getTaskListID()).getAssociatedTaskList());
        int curPos = taskList.getTasks().indexOf(task);
        if(curPos == -1) return ;
        int newPos = curPos + delta;
        if(newPos < 0 || newPos >= taskList.getTasks().size()) return ;
        swap(taskList.getTasks(), curPos, newPos);
        serverUtils.saveTaskList(taskList);
        mainCtrl.sendToOthers(taskList);
        mainCtrl.getTaskListViewById(taskList.getId()).updateOverview(taskList);
        TaskListView taskListView = mainCtrl.getTaskListViewById(taskList.getId());
        overviewController.changeSelectedTask(taskListView, task);
    }

    /**
     * Displays the adding tag window, when T key is clicked
     * @param task the task to which a tag is added
     */
    public void showAddTag(Task task) {
        Task newTask = new Task(task);
        Tag newTag = addNewTag(newTask);
        newTask.getTags().add(newTag);
        mainCtrl.showEditTagScene(newTag, null, newTask);
    }

    /**
     * Creates a new tag and stores it in the server
     * @param task the task of that tag
     * @return the created tag
     */
    public Tag addNewTag(Task task) {
        Tag tag = new Tag();
        tag.setDescription("New tag");
        tag.setTagColor("#ffffcc");
        tag.setTagFontColor("#000000");
        tag.setBoardId(mainCtrl.getBoardViewFromTask(task)
                .getAssociatedBoard()
                .getId());
        tag = serverUtils.addNewTag(tag);

        return tag;
    }
    /**
     * Executes UP shortcut
     * @param task selected Task
     */
    public void upShortcut(Task task){
        TaskListView taskListView = mainCtrl.getTaskListViewById(task.getTaskListID());
        TaskList taskList = taskListView.getAssociatedTaskList();
        SelectTaskButton stb = taskListView.getSelectedSTB(task.getId());
        int currentIndex = taskList.getTasks().indexOf(task);
        int nextIndex;
        if(currentIndex == 0){
            nextIndex = taskList.getTasks().size() - 1;
        }
        else {
            nextIndex = currentIndex - 1;
        }
        Long nextID = taskList.getTasks().get(nextIndex).getId();
        SelectTaskButton nextSTB = taskListView.getSelectedSTB(nextID);
        overviewController.setSelectedTask(taskList.getTasks().get(nextIndex));
        stb.unHighlightTask();
        nextSTB.highlightTask();
    }

    /**
     * Executes DOWN shortcut
     * @param task selected Task
     */
    public void downShortcut(Task task){
        TaskListView taskListView = mainCtrl.getTaskListViewById(task.getTaskListID());
        TaskList taskList = taskListView.getAssociatedTaskList();
        SelectTaskButton stb = taskListView.getSelectedSTB(task.getId());
        int currentIndex = taskList.getTasks().indexOf(task);
        int nextIndex;
        if(currentIndex == taskList.getTasks().size() - 1){
            nextIndex = 0;
        }
        else{
            nextIndex = currentIndex + 1;
        }
        Long nextID = taskList.getTasks().get(nextIndex).getId();
        SelectTaskButton nextSTB = taskListView.getSelectedSTB(nextID);
        overviewController.setSelectedTask(taskList.getTasks().get(nextIndex));
        stb.unHighlightTask();
        nextSTB.highlightTask();
    }

    /**
     * Executes LEFT shortcut
     * @param task selected Task
     */
    public void leftShortcut(Task task){
        Task leftTask = findLeftRight(task, -1);
        if(leftTask.equals(task)){
            return;
        }
        TaskListView currentTaskListView = mainCtrl.getTaskListViewById(task.getTaskListID());
        TaskListView leftTaskListView = mainCtrl.getTaskListViewById(leftTask.getTaskListID());
        SelectTaskButton currentSTB = currentTaskListView.getSelectedSTB(task.getId());
        SelectTaskButton leftSTB = leftTaskListView.getSelectedSTB(leftTask.getId());
        overviewController.setSelectedTask(leftTask);
        currentSTB.unHighlightTask();
        leftSTB.highlightTask();
    }

    /**
     * Executes RIGHT shortcut
     * @param task selected Task
     */
    public void rightShortcut(Task task){
        Task rightTask = findLeftRight(task, 1);
        if(rightTask.equals(task)){
            return;
        }
        TaskListView currentTaskListView = mainCtrl.getTaskListViewById(task.getTaskListID());
        TaskListView rightTaskListView = mainCtrl.getTaskListViewById(rightTask.getTaskListID());
        SelectTaskButton currentSTB = currentTaskListView.getSelectedSTB(task.getId());
        SelectTaskButton rightSTB = rightTaskListView.getSelectedSTB(rightTask.getId());
        overviewController.setSelectedTask(rightTask);
        currentSTB.unHighlightTask();
        rightSTB.highlightTask();
    }

    /**
     * Finds a taskList which contains a given task
     * @param list the list of task lists
     * @param task the task
     * @return the index of the tasklist with the given task
     */
    private int findIndex(List<TaskList> list, Task task) {
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getId().equals(task.getTaskListID())) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Finds next Task in left / right direction
     * @param task selected Task
     * @param delta -1 if left, 1 if right
     * @return next Task in the specified direction
     */
    public Task findLeftRight(Task task, int delta){
        Board board = mainCtrl.getBoardViewFromTask(task).getAssociatedBoard();
        Long currentTaskListID = task.getTaskListID();
        TaskList currentTaskList = mainCtrl.getTaskListViewById(
                currentTaskListID).getAssociatedTaskList();
        int taskListsInBoard = board.getTaskLists().size();
        if(taskListsInBoard == 1){
            return task;
        }
        int currentTaskListIndex = findIndex(board.getTaskLists(), task);

        int nextTaskListIndex = currentTaskListIndex + delta;
        if(nextTaskListIndex < 0){
            nextTaskListIndex = 0;
        }
        if(nextTaskListIndex == taskListsInBoard){
            nextTaskListIndex = taskListsInBoard-1;
        }
        int currentTaskIndex = currentTaskList.getTasks().indexOf(task);
        while(board.getTaskLists().get(nextTaskListIndex).getTasks().isEmpty()){
            nextTaskListIndex += delta;
            if(nextTaskListIndex < 0){
                nextTaskListIndex = taskListsInBoard - 1;
            }
            if(nextTaskListIndex == taskListsInBoard){
                nextTaskListIndex = 0;
            }
        }
        if(currentTaskListIndex == nextTaskListIndex){
            return task;
        }
        TaskList nextTaskList = board.getTaskLists().get(nextTaskListIndex);
        if(currentTaskIndex < nextTaskList.getTasks().size()){
            return nextTaskList.getTasks().get(currentTaskIndex);
        }
        return nextTaskList.getTasks().get(nextTaskList.getTasks().size() - 1);
    }

    /**
     * Method that locks board
     * still granting write access to current user
     */
    public void lockBoard(){
        var res = mainCtrl.showPasswordPopUp("Adding password to a board",
                "Please enter desired password: ");
        if(overviewController.getPasswordField().getText().equals("")) {
            return;
        }
        if(res.isEmpty()){
            return;
        }
        var pass = overviewController.getPasswordField().getText();
        updatePassword(pass);
    }

    /**
     * Utility method that displays dialog to remove
     * board password
     */
    public void removePassword(){
        if(overviewController.isInAdmin()){
            removePasswordAdmin();
            return;
        }
        var res = mainCtrl.showPasswordPopUp("Removing password from board",
                "Please enter password for verification:");
        if(overviewController.getPasswordField().getText().equals("")) {
            return;
        }
        if(res.isEmpty()){
            return;
        }
        var pass = overviewController.getPasswordField().getText();
        if(!removePassword(pass)){
            overviewController.showError("Wrong board password");
        }
    }

    /**
     * Method that unlocks a board
     * granting write access to the user
     */
    public void unlockBoard(){
        if(overviewController.isInAdmin()){
            overviewController.showError("You are in admin mode! You already have write access!");
            return;
        }
        var res = mainCtrl.showPasswordPopUp("Unlocking a board",
                "Please enter password to gain write access: ");
        if(overviewController.getPasswordField().getText().equals("")) {
            return;
        }
        if(res.isEmpty()){
            return;
        }
        var pass = overviewController.getPasswordField().getText();
        if(!checkBoardPassword(pass)){
            overviewController.showError("Wrong board password");
        }
    }
}
