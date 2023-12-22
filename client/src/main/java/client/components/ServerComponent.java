package client.components;

import client.scenes.ServerConnectController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class ServerComponent extends HBox implements Component<String> {
    private final ServerConnectController serverConnectController;
    private String server;

    /**
     * Constructor
     * @param server server address to be displayed
     * @param serverConnectController controller of the scene
     */

    public ServerComponent(String server,
                           ServerConnectController serverConnectController){
        this.serverConnectController = serverConnectController;
        this.createOverview(server);
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
    public void updateOverview(String updatedObject) {

    }

    /**
     * Creates new layout to display
     *
     * @param newObject object to be displayed
     */
    @Override
    public void createOverview(String newObject) {
        var node = (GridPane)loadNode("/client.components/recentServer.fxml");
        this.setPadding(new Insets(5, 0, 8, 0));
        node.setAlignment(Pos.CENTER);
        this.setAlignment(Pos.CENTER);
        super.getChildren().add(node);
        Label serverAddress = (Label) super.lookup("#serverAddress");
        Button removeRecent = (Button) super.lookup("#removeRecent");
        Button fillServer = (Button) super.lookup("#fillServer");
        serverAddress.setText(newObject);
        fillServer.setOnMouseClicked(event ->
            serverConnectController.setServerAddress(newObject)
        );
        removeRecent.setOnMouseClicked(event ->
            serverConnectController.removeRecentClicked(this)
        );
        server = newObject;
    }

    /**
     * Getter for currently displayed element
     * @return string server address
     */
    public String getServer() {
        return server;
    }
}
