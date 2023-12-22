package client.service;

import client.scenes.MainCtrl;
import client.scenes.ServerConnectController;
import client.utils.ServerUtils;

import javax.inject.Inject;

public class ServerConnectService {
    private final ServerUtils serverUtils;
    private final MainCtrl mainCtrl;
    private ServerConnectController serverConnectController;

    /**
     * An injectable constructor for the service
     *
     * @param serverUtils server utils to inject
     * @param mainCtrl    the main controller
     */
    @Inject
    public ServerConnectService(ServerUtils serverUtils,
                                MainCtrl mainCtrl) {
        this.serverUtils = serverUtils;
        this.mainCtrl = mainCtrl;
    }

    /**
     * A setter for the controller. This is required, to avoid circular dependency injection.
     *
     * @param serverConnectController the controller to which this service belongs
     */
    public void setServerConnectController(ServerConnectController serverConnectController) {
        this.serverConnectController = serverConnectController;
    }

    /**
     * A getter for the controller. This is required for testing.
     * @return the serverConnect controller
     */
    public ServerConnectController getServerConnectController() {
        return serverConnectController;
    }

    /**
     * Joins a server with a given address (and possibly tells a controller to
     * change scenes).
     *
     * @param serverAddress the URL of the server to join
     * @return returns true, iff connection to server was successful
     */
    public boolean joinServer(String serverAddress) {
        if (serverAddress != null && !serverAddress.equals("")) {
            if(serverAddress.charAt(serverAddress.length() - 1) != '/') {
                serverAddress += '/';
            }
            serverUtils.setServer(serverAddress);
            if (serverUtils.testConnection()) {
                mainCtrl.showOverview();
                mainCtrl.rememberServer(serverAddress);
                String url = serverAddress.replace("http", "ws") + "ws";
                mainCtrl.testConnectionToWebSocket(url);
                return true;
            } else {
                serverConnectController.showConnectionError();
                return false;
            }
        } else {
            serverConnectController.showConnectionError();
            return false;
        }
    }
}
