package server.service;

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
import server.database.TagDao;
import server.database.TaskDao;
import java.util.ArrayList;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @MockBean
    private final TagDao tagDao = Mockito.mock(TagDao.class);
    @MockBean
    private final TaskDao taskDao = Mockito.mock(TaskDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTag() {
        Tag tag = new Tag();
        when(tagDao.save(any(Tag.class))).thenReturn(tag);
        Tag added = tagService.addNewTag(tag);
        assertThat(added).isEqualTo(tag);
    }

    @Test
    void addTagNULL() {
        Tag added = tagService.addNewTag(null);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void removeTag () {
        Task task = new Task();
        Tag tag = new Tag();
        ArrayList<Tag> lst = new ArrayList<>();
        lst.add(tag);
        task.setTags(lst);

        Mockito.doReturn(true).when(tagDao).existsById(any());
        Mockito.doReturn(true).when(taskDao).existsById(any());

        Mockito.doReturn(Optional.of(task)).when(taskDao).findById(any());
        Mockito.doReturn(Optional.of(tag)).when(tagDao).findById(any());

        Mockito.doReturn(Optional.of(tag)).when(tagDao).findById(1l);
        Mockito.doReturn(Optional.of(task)).when(taskDao).findById(1l);

        var ret = tagService.removeTag(1L, 1L);
        assertThat(ret).isEqualTo(tag);
    }
    @Test
    void removeTagNonExistentNULL() {

    }
    @Test
    void removeTagNULL() {
        when(tagDao.existsById(any(Long.class))).thenReturn(false);
        var added = tagService.removeTag(new Tag().getId(), new Task().getId());
        assertThat(added).isEqualTo(null);
    }
}