package server.service;

import commons.Tag;
import commons.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.TagDao;
import server.database.TaskDao;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagDao tagDao;
    private final TaskDao taskDao;

    /**
     * The constructor for the service
     *
     * @param tagDao  the tag data access object (injected)
     * @param taskDao the task DAO
     */
    @Autowired
    public TagService(TagDao tagDao, TaskDao taskDao) {
        this.tagDao = tagDao;
        this.taskDao = taskDao;
    }

    /**
     * Adds a new tag
     * @param tag the tag to add
     * @return the newly added tag
     */
    public Tag addNewTag (Tag tag) {
        if(tag == null) return null;
        return tagDao.save(tag);
    }

    /**
     * Disconnects the tag from the task
     * @param tagID the ID of the tag being removed
     * @param taskID the ID of the task in which the tag is removed from
     * @return the removed tag
     */
    public Tag removeTag (Long tagID, Long taskID) {
        if(tagID == null) return null;
        Optional<Tag> tagOptional = tagDao.findById(tagID);
        Optional<Task> taskOptional = taskDao.findById(taskID);
        if (tagOptional.isEmpty() || taskOptional.isEmpty()) {
            return null;
        }
        Tag tag = tagOptional.get();
        Task task = taskOptional.get();
        task.getTags().remove(tag);
        taskDao.save(task);
        tagDao.deleteById(tag.getId());
        return tag;
    }

    /**
     * Deletes all tags with same desc from board
     *
     * @param tag tag to be removed
     * @return res
     */
    public Integer deleteTagFromBoard (Tag tag) {
        if(tag == null ) {
            return null;
        }
        return tagDao.deleteTagFromBoard(
                tag.getBoardId(),
                tag.getDescription(),
                tag.getTagColor(),
                tag.getTagFontColor());
    }

    /**
     * Utility method that updates tags in DB
     * @param oldTag old tag
     * @param newTag new tag with updated value
     * @return number of rows changed
     */
    public Integer updateTags(Tag oldTag, Tag newTag){
        if(oldTag == null || newTag == null) {
            return null;
        }
        return tagDao.updateTags(
                newTag.getDescription(),
                newTag.getTagColor(),
                newTag.getTagFontColor(),
                newTag.getBoardId(),
                oldTag.getDescription(),
                oldTag.getTagColor(),
                oldTag.getTagFontColor());
    }

    /**
     * Get all tags
     * @param boardID the board id
     * @return list of distinct boards
     */
    public List<Tag> getAllDistinct(Long boardID) {
        return tagDao.getAllDistinct(boardID);
    }
}
