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
package client.utils;

import commons.*;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private String server = "http://localhost:8080/";

    /**
     * Getter for the server.
     *
     * @return the server URL as a String
     */
    public String getServer() {
        return this.server;
    }

    /**
     * Changes the server address to user input.
     *
     * @param server the new server address.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Getter
     * @return returns default value for the server
     */
    public String getDefaultServer() {
        return "http://localhost:8080/";
    }

    /**
     * Test server connection with user input.
     *
     * @return true if successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("/connection/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
            return response.getStatus() == 200;
        } catch (ProcessingException e) {
            return false;
        }
    }

    /**
     * Utility method, that check whether the provided admin password is right
     * @param pass input pass
     * @return true or false based on criteria
     */
    public boolean checkAdminPassword(String pass){
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/admin/" + pass)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        return response.getStatus() == 200;
    }

    /**
     * Constructs GET request that retrieves a board by a given id
     *
     * @param id id of the board
     * @return object of type Board
     */
    public Board getBoardByID(long id) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/get/" + id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        if (result.getStatus() == 200) {
            return result.readEntity(new GenericType<>() {
            });
        } else {
            return null;
        }
    }
    /**
     * Constructs GET request that retrieves a board
     * by a given user-friendly id
     *
     * @param id id of the board
     * @return object of type Board
     */
    public Board getBoardByUserID(String id) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/get/userID/" + id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        if (result.getStatus() == 200) {
            return result.readEntity(new GenericType<>() {
            });
        } else {
            return null;
        }
    }

    /**
     * Get all boards for admin view
     * @return list of boards
     */
    public List<Board> getAllBoards(){
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/get")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        if(response.getStatus() == 200){
            return response.readEntity(new GenericType<>(){});
        } else {
            return null;
        }
    }

    /**
     * Constructs POST request to the server
     * that saves a given task to the database
     *
     * @param task task that needs to be saved
     * @return true or false based on HTTP status code
     */
    public Task saveTaskByID(Task task) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/task/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(task));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Saves a board
     *
     * @param board the board to be saved
     * @return the saved board
     */
    public Board saveBoard(Board board) {
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(board));
        return response.readEntity(new GenericType<>() {
        });
    }
    /**
     * Constructs a GET request that retrieves a task from the database
     *
     * @param id id of the task
     * @return object of type Task
     */
    public Task getTaskByID(Long id) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/task/get/" + id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        if (result.getStatus() == 200) {
            return result.readEntity(new GenericType<>() {
            });
        } else {
            return null;
        }
    }


    /**
     * Constructs POST request to the server
     * that saves a given task list to the database
     *
     * @param taskList task list that needs to be saved
     * @return true or false based on HTTP status code
     */
    public TaskList saveTaskList(TaskList taskList) {

        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/tasklist/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(taskList));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Constructs POST request to the server
     * that removes a given task list from the database
     *
     * @param taskList task list that needs to be removed
     * @return true or false based on HTTP status code
     */
    public TaskList removeTaskList(TaskList taskList) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/tasklist/remove")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(taskList));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * DELETE request that removes a task from the database
     * @param taskID task that needs to be removed
     */
    public void removeTask(Long taskID){
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/task/delete/" + taskID)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Adds a new TaskList to the database in the server (assigns it to it's board,
     * which we get in the server by TaskList.boardId).
     *
     * @param taskList the taskList to add
     * @return the added TaskList
     */
    public TaskList addNewTaskList(TaskList taskList) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/tasklist/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(taskList));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Constructs POST request to the server
     * that saves a given tag to the database
     *
     * @param tag tag that needs to be saved
     * @return true or false based on HTTP status code
     */
    public boolean saveTag(Tag tag) {
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/tag/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(tag));
        return response.getStatus() == 200;
    }

    /**
     * Delete all tags with the same desc from board
     *
     * @param tag example tag to be removed
     */
    public void deleteTagFromBoard(Tag tag) {
        try (
                Response result = ClientBuilder.newClient(new ClientConfig())
                        .target(server).path("api/tag/delete")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .post(Entity.json(tag))){
            if(result.getStatus() != 200){
                throw new IllegalStateException("Unsuccessful deletion");
            }
        } catch (IllegalStateException | ProcessingException e){
            e.printStackTrace();
            System.err.println("Error");
        }
    }

    /**
     * Constructs POST request to the server
     * that saves a given subTask to the database
     *
     * @param subTask tag that needs to be saved
     * @return true or false based on HTTP status code
     */
    public SubTask saveSubTask(SubTask subTask) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/subtask/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(subTask));
        return result.readEntity(new GenericType<>() {
        });    }


    /**
     * Adds a new Task to the database in the server
     * @param task the task to add
     * @return the task that was added
     */
    public Task addNewTask(Task task) {
        Response result = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/task/add")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(task));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Adds tag to the DB
     * @param tag to be added
     * @return the tag that was added
     */
    public Tag addNewTag(Tag tag) {
        Response result = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/tag/add")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.json(tag));
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Removes Tag from the DB
     * @param tagID the tag to be removed
     * @param taskID the task from which the tag is removed
     * @return removed tag
     */
    public Tag removeTag(Long tagID, Long taskID){
        Response result = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/tag/delete/"+taskID+"/"+tagID)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        return result.readEntity(new GenericType<>() {
        });
    }

    /**
     * Removes a Board from the DB
     *
     * @param board the Board to be removed
     */
    public void removeBoard(Board board){
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/board/remove/" + board.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Gets all tags from a board
     *
     * @param board the board
     * @return list of tags
     */
    public List<Tag> getAllTagsFromBoard(Board board) {
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/tag/get/" + board.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if(response.getStatus() == 200){
            return response.readEntity(new GenericType<>(){});
        } else {
            return null;
        }
    }

    /**
     * Utility method for updating tags
     * from overview
     * @param oldTag old tag
     * @param newTag new tag
     * @return success of the operation
     */
    public boolean updateTags(Tag oldTag, Tag newTag){
        Map<String, Tag> postObj = new HashMap<>();
        postObj.put("old", oldTag);
        postObj.put("new", newTag);
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/tag/update")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .post(Entity.json(postObj));
        return response.getStatus() == 200;
    }

    /**
     * GET request that checks whether the password for
     * a given board is correct
     * @param boardID provided board
     * @param pass to be checked
     * @return null if wrong or board object
     */
    public Board checkBoardPassword(Long boardID, String pass){
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/board/check/" + boardID)
                .queryParam("password", pass)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if(response.getStatus() != 200)
            return null;
        return response.readEntity(new GenericType<>(){});
    }

    /**
     * GET request that checks whether the password for
     * a given board is correct and also removes it
     * @param boardID provided board
     * @param pass to be checked
     * @return null if wrong or board object
     */
    public Board removePassword(Long boardID, String pass){
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/board/remove/pass/" + boardID)
                .queryParam("password", pass)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if(response.getStatus() != 200)
            return null;
        return response.readEntity(new GenericType<>(){});
    }

    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final ExecutorService boardExec = Executors.newCachedThreadPool();

    /**
     * Uses long polling to check whether a task has been deleted from the database.
     * @param thisID id of the task
     * @param taskID consumer for closing TaskView
     */
    public void poll(Long thisID, Consumer<Long> taskID){
        exec.execute(() -> {
            while(!Thread.interrupted()){
                Response result = ClientBuilder.newClient(new ClientConfig())
                        .target(server).path("api/task/" + thisID + "/status")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if(result.getStatus() == 304){
                    continue;
                }
                var id = result.readEntity(Long.class);
                taskID.accept(id);
                stopLongPolling();
            }
        });
    }

    /**
     * Utility method for admin to remove password for a board
     * @param id target board
     * @return updated board
     */
    public Board removePassword(Long id){
        Response response = ClientBuilder.newClient(new ClientConfig())
            .target(server).path("api/board/remove/pass/admin/" + id)
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .get();
        if(response.getStatus() != 200)
            return null;
        return response.readEntity(new GenericType<>(){});
    }
    /**
     * Uses long polling to check whether a board has been deleted from the database.
     * @param boardID consumer for closing BoardView
     */
    public void pollBoard(Consumer<Long> boardID){
        boardExec.execute(() -> {
            while(!Thread.interrupted()){
                Response result = ClientBuilder.newClient(new ClientConfig())
                        .target(server).path("api/board/status")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if(result.getStatus() == 304){
                    continue;
                }
                var board = result.readEntity(Board.class);
                boardID.accept(board.getId());
            }
        });
    }

    /**
     * Stops polling thread.
     */
    public void stopLongPolling(){
        Thread.currentThread().interrupt();
    }

}