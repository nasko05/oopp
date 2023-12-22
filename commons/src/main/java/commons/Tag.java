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
public class Tag extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String description;
    private Long boardId;
    private String tagColor;
    private String tagFontColor;

    /**
     * Constructor of tag with no parameter
     */
    public Tag(){

    }

    /**
     * Constructor used for cloning object
     *
     * @param other object of type Tag
     */
    public Tag(Tag other){
        this.id = other.id;
        this.description = other.description;
        this.tagColor = other.tagColor;
        this.tagFontColor = other.tagFontColor;
        this.boardId = other.boardId;
    }

    /**
     * Getter
     * @return id
     */
    public Long getId() {
        return id;
    }
}
