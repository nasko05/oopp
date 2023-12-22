package client.components;

import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import commons.Model;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class Workspace extends VBox implements Component<Model> {
    private final MainCtrl mainCtrl;
    private GridPane grid;
    private ScrollPane scrollPane;
    private final OverviewController overviewController;
    private VBox recentBoardContainer;
    private final int height, width;
    private Button joinButton;
    private Button disconnectButton;
    private Button adminButton;
    private Button createButton;
    private Label serverURLLabel;
    private final String serverURL;

    /**
     * A constructor for the overview
     * @param mainCtrl the main controller
     * @param overviewController the overview controller
     * @param height the pref height of the workspace
     * @param width the pref width of the workspace
     * @param serverURL the url of the server
     */
    public Workspace(MainCtrl mainCtrl, OverviewController overviewController, int height,
                     int width, String serverURL) {
        this.overviewController = overviewController;
        this.mainCtrl = mainCtrl;
        this.height = height;
        this.width = width;
        this.serverURL = serverURL;
        createOverview(null);
    }

    @Override
    public void updateOverview(Model updatedObject) {
        this.recentBoardContainer.getChildren().clear();
        for(Long id : mainCtrl.getAllBoardsForCurrent()) {
            addRecentBoard(id);
        }
    }
    
    @Override
    public void createOverview(Model newObject) {
        this.setAlignment(Pos.TOP_CENTER);
        grid = loadNode("/client/scenes/workspace.fxml");
        this.findNodes();
        scrollPane.setFitToWidth(true);
        this.recentBoardContainer =
            (VBox) scrollPane.getContent();
        this.recentBoardContainer.getChildren().clear();
        for(Long id : mainCtrl.getAllBoardsForCurrent()) {
            addRecentBoard(id);
        }
        addEvents();

        grid.setPrefHeight(height);
        grid.setMaxHeight(height);
        grid.setPrefWidth(width);
        grid.setMaxWidth(width);

        serverURLLabel.setText(serverURL);

        this.getChildren().add(grid);
    }

    /**
     * Helper method
     * that adds new recent board to the FlowPane
     * @param id of the board to be added
     */
    public void addRecentBoard(Long id){
        recentBoardContainer.getChildren().add(
            new RecentBoard(overviewController, mainCtrl, id));
    }
    /**
     * Removes board from recent boards
     * @param title current server
     * @param id board to be removed
     */
    public void removeRecentBoard(String title, Long id){
        for (int i = 0; i < recentBoardContainer.getChildren().size(); i++) {
            var curr = (RecentBoard)recentBoardContainer.getChildren().get(i);
            if(curr.getBoardTitle().equals(title)){
                recentBoardContainer.getChildren().remove(curr);
                mainCtrl.removeRecentBoard(id);
                mainCtrl.removeBoardPass(id);
                return;
            }
        }
    }

    /**
     * Utility method that finds used nodes
     */
    public void findNodes(){
        this.scrollPane = (ScrollPane) grid.lookup("#recentBoard");
        this.joinButton = (Button) grid.lookup("#joinButton");
        this.disconnectButton = (Button) grid.lookup("#disconnectButton");
        this.adminButton = (Button) grid.lookup("#adminButton");
        this.createButton = (Button) grid.lookup("#createButton");
        this.serverURLLabel = (Label) grid.lookup("#serverURL");
    }

    /**
     * Utility method that adds events for
     * used components
     */
    public void addEvents(){
        joinButton.setOnMouseClicked(event ->
                overviewController.joinBoardClicked()
        );
        disconnectButton.setOnMouseClicked(event ->
                overviewController.disconnectClicked()
        );
        adminButton.setOnMouseClicked(event ->
                overviewController.checkAdminPassword()
        );
        createButton.setOnMouseClicked(event ->
                overviewController.createNewBoard()
        );
    }
}
