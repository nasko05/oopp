package server.api;

import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.TagService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final TagService tagService;
    /**
     * The constructor for the tag controller
     *
     * @param tagService the tag service (injected)
     */
    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Handles the POST request that adds a new tag (or just updates it)
     *
     * @param tag the tag to add
     * @return the newly added tag
     */
    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    ResponseEntity<Tag> addNewTag(@RequestBody Tag tag) {
        Tag addedTag = tagService.addNewTag(tag);
        if (addedTag == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(addedTag);
        }
    }

    /**
     * Handles the DELETE request to remove a tag
     *
     * @param taskID The ID of the task from which the tag should be deleted
     * @param tagID  The ID of the tag which will be deleted
     * @return removed tag
     */
    @DeleteMapping("/delete/{taskID}/{tagID}")
    ResponseEntity<Tag> removeTag(@PathVariable("taskID") Long taskID,
                                  @PathVariable("tagID") Long tagID) {
        Tag result = tagService.removeTag(tagID, taskID);
        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    /**
     * gets tags from board
     *
     * @param boardID board
     * @return list of tags
     */
    @GetMapping("/get/{boardID}")
    ResponseEntity<List<Tag>> getTag(@PathVariable("boardID") Long boardID) {
        List<Tag> tagList = tagService.getAllDistinct(boardID);
        if (tagList == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(tagList);
        }
    }

    /**
     * Deletes all tags with same desc from board
     *
     * @param tag example tag to be deleted
     * @return entity
     */
    @PostMapping("/delete")
    @Transactional
    public ResponseEntity<Void> deleteTagFromBoard(@RequestBody Tag tag) {
        var result = tagService.deleteTagFromBoard(tag);
        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Mapping for updating tags
     * @param oldNewTag pair with the old and new tag
     * @return success of the operation
     */
    @PostMapping("/update")
    @Transactional
    public ResponseEntity<Void> updateTags(@RequestBody Map<String, Tag> oldNewTag){
        var result = tagService.updateTags(oldNewTag.get("old"), oldNewTag.get("new"));
        if (result == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}
