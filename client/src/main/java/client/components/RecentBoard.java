package client.components;

import client.scenes.MainCtrl;
import client.scenes.OverviewController;
import commons.Board;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class RecentBoard extends HBox implements Component<Board> {
    private final OverviewController overviewController;
    private Label boardTitle;
    private final MainCtrl mainCtrl;
    private final Long boardId;

    /**
     * Constructor
     * @param overviewController overview Controller
     * @param mainCtrl main control
     * @param boardId displayed board
     */
    public RecentBoard(OverviewController overviewController, MainCtrl mainCtrl, Long boardId) {
        this.overviewController = overviewController;
        this.mainCtrl = mainCtrl;
        this.boardId = boardId;
        this.createOverview(null);
    }

    /**
     * Updates overview
     * Compares old object to new
     * and renders the changes on the existing layout
     * without creating a new one
     *
     * @param updatedObject changed object
     */
    @Override
    public void updateOverview(Board updatedObject) {
        this.boardTitle.setText(updatedObject.getTitle());
    }

    /**
     * Creates new layout to display
     *
     * @param newObject object to be displayed
     */
    @Override
    public void createOverview(Board newObject) {
        var pane = (GridPane) loadNode(
            "/client.components/recentBoard.fxml");
        this.setPadding(new Insets(5, 0, 8, 0));
        pane.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        super.getChildren().add(pane);
        this.boardTitle = (Label) super.lookup("#boardTitle");
        Button openRecent = (Button) super.lookup("#openRecent");
        Button removeRecent = (Button) super.lookup("#removeRecent");

        var board = mainCtrl.getServerUtils().getBoardByID(boardId);
        if(board == null){
            return;
        }
        this.boardTitle.setText(board.getTitle());
        openRecent.setOnMouseClicked(event -> {
            if(overviewController.isInAdmin()){
                return;
            }
            overviewController.setBoardId(board.getUserId());
            overviewController.joinBoardClicked();
        });
        removeRecent.setOnMouseClicked(event ->
            overviewController.removeRecent(getBoardTitle(), boardId)
        );
    }

    /**
     * Gets the title of the board
     * @return title of the board
     */
    public String getBoardTitle() {
        return boardTitle.getText();
    }
}
