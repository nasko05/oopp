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
package client;

import client.scenes.*;
import client.utils.ServerUtils;
import client.utils.WebSocketClientConfig;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;


import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Launches the application
     *
     * @param args entry arguments
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Initialize our application
     * Create instances of all used scenes and controllers as pairs
     * and send them to MainCtrl
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        var serverConnect =
            FXML.load(ServerConnectController.class, "client", "scenes", "serverConnect.fxml");
        var overview =
            FXML.load(OverviewController.class, "client", "scenes", "overview.fxml");
        var editTask =
            FXML.load(TaskViewController.class, "client", "scenes", "taskView.fxml");
        var addSubTask =
            FXML.load(AddSubTaskController.class, "client", "scenes", "addSubTask.fxml");
        var renameTaskList =
            FXML.load(RenameTaskListController.class, "client", "scenes", "renameTaskList.fxml");
        var colorManagement =
            FXML.load(ColorManagementController.class, "client","scenes","colorManagement.fxml");
        var editTag =
                FXML.load(EditTagController.class, "client", "scenes", "editTag.fxml");
        var editSubtask =
                FXML.load(EditSubtaskController.class, "client", "scenes", "editSubtask.fxml");
        var changePass =
            FXML.load(OverviewController.class, "client", "scenes", "changePassword.fxml");
        var taskColorPresets =
                FXML.load(TaskColorPresetsController.class, "client", "scenes",
                        "taskColorPresets.fxml");


        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        var webSocketConfig = INJECTOR.getInstance(WebSocketClientConfig.class);
        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        mainCtrl.initialize(primaryStage, serverConnect, overview,
                editTask, addSubTask, renameTaskList, colorManagement,
                editTag, editSubtask, changePass, taskColorPresets, webSocketConfig, serverUtils);
    }
}