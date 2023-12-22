package server.sockets;


import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Constructor
     * @param simpMessagingTemplate object
     */
    @Autowired
    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    /**
     * Controller for /app/tag
     * @param tag received tag
     * @param headers headers
     * */
    @MessageMapping("/tag")
    public void tagBroker(@Payload Tag tag, @Headers Map<String, Object> headers){
        if((tag == null) || (tag.getDescription() == null)){
            return;
        }
        simpMessagingTemplate.convertAndSend("/update/tag", tag, headers);
    }

    /**
     * Controller for /app/subtask
     * @param subTask received subTask
     * @param headers headers
     */
    @MessageMapping("/subtask")
    public void subTaskBroker(@Payload SubTask subTask, @Headers Map<String, Object> headers){
        if((subTask == null) || (subTask.getDescription() == null)){
            return;
        }
        simpMessagingTemplate.convertAndSend("/update/subtask", subTask, headers);
    }

    /**
     * Controller for /app/task
     * @param task received Task
     * @param headers headers
     */
    @MessageMapping("/task")
    public void taskBroker(@Payload Task task, @Headers Map<String, Object> headers){
        if(task == null || task.getDescription() == null ||
                task.getTitle() == null || task.getTags() == null
                || task.getSubTasks() == null){
            return;
        }
        simpMessagingTemplate.convertAndSend("/update/task", task, headers);
    }

    /**
     * Controller for /app/taskList
     * @param taskList received taskList
     * @param headers headers
     */
    @MessageMapping("/taskList")
    public void taskListBroker(@Payload TaskList taskList, @Headers Map<String, Object> headers){
        if(taskList == null || taskList.getTasks() == null){
            return;
        }
        simpMessagingTemplate.convertAndSend("/update/taskList", taskList, headers);
    }

    /**
     * Controller for /app/board
     * @param board received board
     * @param headers headers
     */
    @MessageMapping("/board")
    public void boardBroker(@Payload Board board, @Headers Map<String, Object> headers){
        if(board == null || board.getTaskLists() == null){
            return;
        }
        simpMessagingTemplate.convertAndSend("/update/board", board, headers);
    }

}
