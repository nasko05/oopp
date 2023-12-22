package server.api;

import commons.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import server.service.SubtaskService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubtaskControllerTest {

    @InjectMocks
    private SubtaskController subtaskController;

    @MockBean
    private final SubtaskService subTaskService = Mockito.mock(SubtaskService.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewSubTask() {
        SubTask subTask = new SubTask();
        when(subTaskService.addNewSubTask(any(SubTask.class))).thenReturn(new SubTask());
        ResponseEntity<SubTask> added = subtaskController.addNewSubTask(subTask);
        assertThat(added).isEqualTo(ResponseEntity.ok(subTask));
    }

    @Test
    void addNewSubTaskNULL() {
        ResponseEntity<SubTask> added = subtaskController.addNewSubTask(null);
        assertThat(added).isEqualTo(ResponseEntity.badRequest().build());
    }
}