package commons;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class SubTask extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String description;
    private boolean checked;
    private Long taskId;
    /**
     * Constructor of subtask with no parameter
     */
    public SubTask(){
    }

    /**
     * Constructor used for cloning a subtask
     *
     * @param subTask subtask to be cloned
     */
    public SubTask(SubTask subTask){
        this.id = subTask.id;
        this.description = subTask.description;
        this.checked = subTask.checked;
        this.taskId = subTask.taskId;
    }

    /**
     * Getter
     * @return id
     */
    public Long getId() {
        return id;
    }
}
