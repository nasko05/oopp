package client.scenes;

import client.components.ServerComponent;
import client.service.ServerConnectService;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class ServerConnectController {

    private final ServerConnectService serverConnectService;
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField serverAddress;
    @FXML
    private VBox serverContainer;

    /**
     * ServerConnectController injection constructor.
     *
     * @param serverConnectService used for logic handling
     * @param serverUtils the server utilities
     * @param mainCtrl the main controller
     */
    @Inject
    public ServerConnectController(ServerConnectService serverConnectService,
                                   ServerUtils serverUtils, MainCtrl mainCtrl) {
        this.serverConnectService = serverConnectService;
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
        serverConnectService.setServerConnectController(this);
    }

    /**
     * Shows the default server address to the user.
     * Facilitates connecting to the local server. Gets called on button click
     */
    @FXML
    public void defaultServerClicked() {
        this.serverAddress.setText(serverUtils.getDefaultServer());
    }

    /**
     * Display all saved servers
     */
    public void loadRecentServer(){
        serverContainer.getChildren().clear();
        var servers = mainCtrl.getAllServers();
        for(String server : servers){
            serverContainer.getChildren().add(new ServerComponent(server, this));
        }
    }

    /**
     * Connects to the client's requested server. Gets called on button click
     */
    @FXML
    public void joinServerClicked() {
        boolean connected = serverConnectService.joinServer(this.serverAddress.getText());
        for(var item : serverContainer.getChildren()){
            if(((ServerComponent)item).getServer().equals(this.serverAddress.getText()))
                return;
        }
        if(!connected) return ;
        serverContainer.getChildren().add(new ServerComponent(this.serverAddress.getText(), this));
    }

    /**
     * Setter for the label
     * @param address to be displayed
     */
    public void setServerAddress(String address){
        serverAddress.setText(address);
        joinServerClicked();
    }

    /**
     * Remove server address from recent
     * @param serverComponent to be removed
     */
    public void removeRecentClicked(ServerComponent serverComponent){
        this.serverContainer.getChildren().remove(serverComponent);
        mainCtrl.removeServer(serverComponent.getServer());
    }
    /**
     * Displays an error box on the screen
     */
    public void showConnectionError() {
        Alert alertBox = new Alert(Alert.AlertType.ERROR);
        alertBox.setTitle("Connection error");
        alertBox.setContentText("Failed to connect to the provided server address. " +
                "Please try again");
        alertBox.showAndWait();
    }
}
