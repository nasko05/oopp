package commons;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@SuppressWarnings("contructor")
public class Board extends Model{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String boardBgColor;
    private String boardFontColor;
    private String taskListsBgColor;
    private String taskListsFontColor;
    private String userId;
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    private ColorEntity taskDefaultColor;
    @OneToMany(cascade = CascadeType.ALL)
    private List<TaskList> taskLists;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ColorEntity> taskColorPresets;

    /**
     * Clone a board from an existing board
     *
     * @param board the board to clone
     */
    public Board(Board board){
        this.id = board.id;
        this.title = board.title;
        this.taskLists = new ArrayList<>();
        for(var taskList : board.getTaskLists()){
            taskLists.add(new TaskList(taskList));
        }
        this.taskColorPresets = new ArrayList<>();
        this.taskColorPresets.addAll(board.getTaskColorPresets());
        this.userId = board.userId;
        this.boardBgColor = board.boardBgColor;
        this.boardFontColor = board.boardFontColor;
        this.taskListsBgColor = board.taskListsBgColor;
        this.taskListsFontColor = board.taskListsFontColor;
        this.taskDefaultColor = new ColorEntity(board.taskDefaultColor);
        this.password = board.password;
    }

    /**
     * Constructor of board with no parameter
     */
    public Board() {
    }

    /**
     * Add a new TaskList to the board.
     *
     * @param taskList the TaskList to be added
     * @return The TaskList that was added
     */
    public TaskList addNewTaskList(TaskList taskList) {
        for(int i = 0; i < taskLists.size(); i++) {
            if(taskLists.get(i).getId().equals(taskList.getId())) {
                taskLists.set(i, taskList);
                return taskList;
            }
        }
        taskLists.add(taskList);
        return taskList;
    }

    /**
     * Removes a TaskList
     *
     * @param taskList TaskList to be removed
     * @return The removed TaskList
     */
    public TaskList removeTaskList(TaskList taskList) {
        for(int i = 0; i < taskLists.size(); i++) {
            if(taskLists.get(i).getId().equals(taskList.getId())) {
                taskLists.remove(i);
                return taskList;
            }
        }
        return null;
    }

    /**
     * Insert a new task or replace the existing task
     *
     * @param task task to add or replace
     */
    public void insertOrReplace(Task task){
        var taskListTarget = (TaskList) null;
        for (TaskList taskList : taskLists) {
            if (task.getTaskListID().equals(taskList.getId())) {
                taskListTarget = taskList;
                break;
            }
        }
        if(taskListTarget == null) return;
        for (int i = 0; i < taskListTarget.getTasks().size(); ++i) {
            var tmp = taskListTarget.getTasks().get(i);
            if (tmp.getId().equals(task.getId())) {
                taskListTarget.getTasks().set(i, task);
                return;
            }
        }
        taskListTarget.addTask(task);
    }

    /**
     * Get the taskList by id
     *
     * @param id id of the taskList
     * @return the taskList with the given id
     */
    public TaskList getTaskListByID(Long id){
        for (var item : this.taskLists){
            if(item.getId().equals(id))
                return item;
        }
        return null;
    }

    /**
     * Removes a Task from a TaskList
     * @param task the task to be removed
     */
    public void removeTask(Task task){
        TaskList target = null;
        for(var taskList: this.taskLists){
            if(task.getTaskListID().equals(taskList.getId())){
                target = taskList;
                break;
            }
        }
        if(target == null){
            return;
        }
        for(int i = 0; i < target.getTasks().size(); ++i){
            var curr = target.getTasks().get(i);
            if(curr.getId().equals(task.getId())){
                target.getTasks().remove(curr);
                return;
            }
        }
    }

    /**
     * Inserts a Task into a TaskList, before or after another Task.
     * @param newTask the Task to be added
     * @param task the "reference task" before/after which the new Task is inserted
     * @param idx 0 if before, 1 if after
     */
    public void insertInList(Task newTask, Task task, int idx){
        TaskList target = null;
        for(var taskList: this.taskLists){
            if(task.getTaskListID().equals(taskList.getId())
                && newTask.getTaskListID().equals(taskList.getId())){
                target = taskList;
                break;
            }
        }
        if(target == null){
            return;
        }
        for(int i = 0; i < target.getTasks().size(); i++) {
            var current = target.getTasks().get(i);
            if (current.getId().equals(task.getId())) {
                target.getTasks().add(i + idx, newTask);
                return;
            }
        }
    }

    /**
     * Getter
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the title
     * @return title
     */
    public String getTitle(){ return title; }

    /**
     * Updated the board's title
     * @param title the new title
     */
    public void setTitle(String title){
        this.title = title;
    }

    /**
     * Getter
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Utility method used for hashing a password
     * @param password password to be hashed
     * @return hashed password
     */
    public String hashPassword(String password) {
        if(password.equals(""))
            return password;
        String generatedPassword = null;
        //Using deterministic SALT
        String salt = "hy^*%#*9";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt; // Combine password with salt
            byte[] bytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
