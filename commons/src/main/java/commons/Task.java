package commons;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Task extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String description;
    private String backGroundColor;
    private String fontColor;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SubTask> subTasks;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Tag> tags;

    private Long taskListID;
    /**
     * Constructor of task with no parameter
     */
    public Task() {
    }

    /**
     * Clone a task from an existing task
     *
     * @param task the task to clone
     */
    public Task(Task task){
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.backGroundColor = task.backGroundColor;
        this.fontColor = task.fontColor;
        this.subTasks = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.taskListID = task.taskListID;
        for(var tag : task.getTags()){
            tags.add(new Tag(tag));
        }
        for (var subTask : task.getSubTasks()){
            subTasks.add(new SubTask(subTask));
        }
    }

    /**
     * Adds a new (or updates) subtask to this task
     * @param subTask the subtask to add/update
     */
    public void addSubtask(SubTask subTask) {
        for(int i = 0; i < subTasks.size(); i++) {
            if(subTasks.get(i).getId().equals(subTask.getId())) {
                subTasks.set(i, subTask);
                return;
            }
        }
        this.subTasks.add(subTask);
    }

    /**
     * Deletes a given subtask from the task
     * @param subTask the subtask to delete
     */
    public void deleteSubTask(SubTask subTask) {

        for(int i = 0; i < subTasks.size(); i++) {
            if(subTasks.get(i).getId().equals(subTask.getId())) {
                subTasks.remove(i);
                return;
            }
        }
    }

    /**
     * Insert a new SubTask or replace the existing SubTask
     *
     * @param subTask subtask to add or replace
     */
    public void insertOrReplace(SubTask subTask){
        for (int i = 0; i < getSubTasks().size(); ++i) {
            var tmp = getSubTasks().get(i);
            if (tmp.getId().equals(subTask.getId())) {
                subTasks.set(i, subTask);
                return;
            }
        }
        subTasks.add(subTask);
    }

    /**
     * Inserts a Task into a TaskList, before or after another Task.
     *
     * @param newSubTask the SubTask to be added
     * @param subTask the "reference subTask" before/after which the new Task is inserted
     * @param idx 0 if before, 1 if after
     */
    public void insertInSubTaskList(SubTask newSubTask, SubTask subTask, int idx){
        for(int i = 0; i < getSubTasks().size(); i++) {
            var current = getSubTasks().get(i);
            if (current.equals(subTask)) {
                subTasks.add(i + idx, newSubTask);
                return;
            }
        }
    }


    /**
     * Getter for the id
     * @return id
     */
    public Long getId() {
        return id;
    }
}
