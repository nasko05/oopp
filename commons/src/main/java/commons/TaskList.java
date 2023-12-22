package commons;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@EqualsAndHashCode(callSuper=false)
public class TaskList extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks;
    @OneToMany(cascade = CascadeType.ALL)
    private List<TaskList> tasksLists;
    private Long boardId;

    /**
     * Clone a taskList from an existing taskList
     *
     * @param taskList the taskList to clone
     */
    public TaskList(TaskList taskList){
        this.id = taskList.id;
        this.title = taskList.title;
        this.tasks = new ArrayList<>();
        for(var task : taskList.getTasks()){
            tasks.add(new Task(task));
        }
        this.boardId = taskList.boardId;
    }

    /**
     * Constructor of taskList with no parameter
     */
    public TaskList() {
    }

    /**
     * Iterates through the tasks List and replaces the task with the matching id
     *
     * @param task the task that has just been created and will be added
     */
    public void addTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                tasks.set(i, task);
                return ;
            }
        }
        tasks.add(task);
    }

    /**
     * Getter for the Tasks
     * @return Tasks contained in this TaskList
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Getter
     * @return id
     */
    public Long getId() {
        return id;
    }
}
