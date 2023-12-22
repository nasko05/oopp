package server.database;

import commons.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagDao extends JpaRepository<Tag, Long> {
    /**
     * Gets all distinct tags from a board
     *
     * @param boardID board
     * @return list of tags
     */
    @Query(value =
        "SELECT DISTINCT on(DESCRIPTION, TAG_COLOR, TAG_FONT_COLOR) * " +
                "FROM TAG " +
                "WHERE BOARD_ID = ?1",
        nativeQuery = true)
    List<Tag> getAllDistinct(Long boardID);

    /**
     * Removes all tags with same desc from a board
     *
     * @param boardID the board
     * @param desc the description
     * @param tagColor tag color
     * @param tagFontColor tag font color
     * @return value
     */
    @Modifying
    @Query(value = "DELETE " +
            "FROM TASK_TAGS " +
            "WHERE EXISTS " +
            "( SELECT t.* " +
            "FROM TAG t " +
            "WHERE t.ID = TAGS_ID " +
            "AND t.DESCRIPTION = ?2 " +
            "AND t.TAG_COLOR = ?3 " +
            "AND t.TAG_FONT_COLOR = ?4); " +
            "DELETE " +
            "FROM tag " +
            "WHERE BOARD_ID = ?1 " +
            "AND DESCRIPTION = ?2 " +
            "AND TAG_COLOR = ?3 " +
            "AND TAG_FONT_COLOR = ?4", nativeQuery = true)
    int deleteTagFromBoard(Long boardID, String desc, String tagColor, String tagFontColor);

    /**
     * Custom query that updates tags in DB
     * @param newDesc new description
     * @param newColor new background color
     * @param newFont new font color
     * @param boardID current board
     * @param oldDesc old description
     * @param oldColor old background color
     * @param oldFont old font color
     * @return number of records updated
     */
    @Modifying
    @Query(value = "UPDATE " +
            "TAG SET DESCRIPTION = ?1 " +
            ", TAG_COLOR = ?2 " +
            ", TAG_FONT_COLOR = ?3 " +
            "WHERE BOARD_ID = ?4 " +
            "AND DESCRIPTION = ?5 " +
            "AND TAG_COLOR = ?6 " +
            "AND TAG_FONT_COLOR = ?7 " +
            ";", nativeQuery = true)
    int updateTags(String newDesc,
                   String newColor,
                   String newFont,
                   Long boardID,
                   String oldDesc,
                   String oldColor,
                   String oldFont);
}