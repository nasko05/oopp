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
import client.service.*;
import client.utils.ServerUtils;
import client.utils.WebSocketClientConfig;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {
    /**
     * Binder that binds classes to a given scope
     *
     * @param binder binder instance
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(OverviewController.class).in(Scopes.SINGLETON);
        binder.bind(TaskViewController.class).in(Scopes.SINGLETON);
        binder.bind(AddSubTaskController.class).in(Scopes.SINGLETON);
        binder.bind(WebSocketClientConfig.class).in(Scopes.SINGLETON);

        binder.bind(AddSubtaskService.class).in(Scopes.SINGLETON);
        binder.bind(OverviewService.class).in(Scopes.SINGLETON);
        binder.bind(RenameTaskListController.class).in(Scopes.SINGLETON);
        binder.bind(ServerConnectService.class).in(Scopes.SINGLETON);
        binder.bind(TaskViewService.class).in(Scopes.SINGLETON);
        binder.bind(EditTagController.class).in(Scopes.SINGLETON);
        binder.bind(TaskColorPresetsController.class).in(Scopes.SINGLETON);
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);
    }
}