package server.api;

import commons.Tag;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import server.database.TagDao;
import server.service.TagService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @InjectMocks
    private TagController tagController;

    @MockBean
    private final TagService tagService = Mockito.mock(TagService.class);
    @MockBean
    private TagDao tagDao = Mockito.mock(TagDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewTag() {
        Tag tag = new Tag();
        when(tagService.addNewTag(any(Tag.class))).thenReturn(new Tag());
        ResponseEntity<Tag> added = tagController.addNewTag(tag);
        assertThat(added).isEqualTo(ResponseEntity.ok(tag));
    }

    @Test
    void addNewTagNULL() {
        ResponseEntity<Tag> added = tagController.addNewTag(null);
        assertThat(added).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removeTag() {
        Tag tag = new Tag();
        Task task = new Task();
        when(tagService.removeTag(any(Long.class), any(Long.class))).thenReturn(tag);
        ResponseEntity<Tag> removed = tagController.removeTag(tag.getId(), task.getId());
        assertThat(removed).isEqualTo(ResponseEntity.ok(tag));
    }

    @Test
    void removeTagNULL() {
        when(tagService.removeTag(null, null)).thenReturn(null);
        ResponseEntity<Tag> removed = tagController.removeTag(null, null);
        assertThat(removed).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void getTag(){
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag());
        when(tagService.getAllDistinct(1L)).thenReturn(tags);
        ResponseEntity<List<Tag>> response = tagController.getTag(1L);
        assertThat(response).isEqualTo(ResponseEntity.ok(tags));
    }

    @Test
    void getTagNull(){
        when(tagService.getAllDistinct(-1L)).thenReturn(null);
        ResponseEntity<List<Tag>> response = tagController.getTag(-1L);
        assertThat(response).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void deleteTagFromBoard(){
        Tag tag = new Tag();
        when(tagService.deleteTagFromBoard(tag)).thenReturn(1);
        ResponseEntity<Void> response = tagController.deleteTagFromBoard(tag);
        assertThat(response).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    void deleteTagFromBoardNull(){
        when(tagService.deleteTagFromBoard(null)).thenReturn(null);
        ResponseEntity<Void> response = tagController.deleteTagFromBoard(null);
        assertThat(response).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void updateTags(){
        Map<String, Tag> oldNewTag = new HashMap<>();
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        oldNewTag.put("old", tag1);
        oldNewTag.put("new", tag2);
        when(tagService.updateTags(tag1, tag2)).thenReturn(1);
        ResponseEntity<Void> response = tagController.updateTags(oldNewTag);
        assertThat(response).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    void updateTagsNull(){
        Map<String, Tag> oldNewTag = new HashMap<>();
        oldNewTag.put("old", null);
        oldNewTag.put("new", null);
        when(tagService.updateTags(null, null)).thenReturn(null);
        ResponseEntity<Void> response = tagController.updateTags(oldNewTag);
        assertThat(response).isEqualTo(ResponseEntity.notFound().build());
    }
}