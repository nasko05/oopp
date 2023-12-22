package commons;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ColorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String backGroundColor;
    private String fontColor;

    /**
     * Constructor
     */
    public ColorEntity(){

    }

    /**
     * Constructor that clones an object
     * @param colorEntity color Entity to be cloned
     */
    public ColorEntity(ColorEntity colorEntity){
        this.id = colorEntity.getId();
        this.backGroundColor = colorEntity.backGroundColor;
        this.fontColor = colorEntity.fontColor;
    }
}
