package server.api;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import server.service.BoardService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    @InjectMocks
    private BoardController boardController;

    @MockBean
    private final BoardService boardService = Mockito.mock(BoardService.class);
    @MockBean
    private final AdminComponent adminComponent = Mockito.mock(AdminComponent.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBoardById() {
        when(boardService.getBoardById(1)).thenReturn(new Board());
        var result = boardController.getBoardById(1);
        assertThat(result).isEqualTo(ResponseEntity.ok(new Board()));
    }

    @Test
    void getBoardByIdNULL() {
        when(boardService.getBoardById(-1)).thenReturn(null);
        var result = boardController.getBoardById(-1);
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void getBoardByUserID(){
        Board board = new Board();
        when(boardService.getBoardByUserId("1")).thenReturn(board);
        var result = boardController.getBoardByUserId("1");
        assertThat(result).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void getBoardByUserIDNull(){
        Board board = new Board();
        when(boardService.getBoardByUserId("1")).thenReturn(null);
        var result = boardController.getBoardByUserId("1");
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void getAllBoards() {
        List<Board> boards = new ArrayList<>();
        boards.add(new Board());
        when(boardService.getAllBoards()).thenReturn(boards);
        ResponseEntity<List<Board>> result = boardController.getAllBoards();
        assertThat(result).isEqualTo(ResponseEntity.ok(boards));
    }

    @Test
    void getAllBoardsNULL() {
        when(boardService.getAllBoards()).thenReturn(null);
        ResponseEntity<List<Board>> result = boardController.getAllBoards();
        assertThat(result).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void addBoard() {
        Board board = new Board();
        when(boardService.addBoard(any(Board.class))).thenReturn(new Board());
        ResponseEntity<Board> added = boardController.addBoard(board);
        assertThat(added).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void addBoardNULL() {
        ResponseEntity<Board> added = boardController.addBoard(null);
        assertThat(added).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removeBoard() {
        Board board = new Board();
        when(boardService.removeBoard(any(Long.class))).thenReturn(board);
        ResponseEntity<Board> removed = boardController.removeBoard(board.getId());
        assertThat(removed).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void removeBoardNULL() {
        when(boardService.removeBoard(null)).thenReturn(null);
        ResponseEntity<Board> removed = boardController.removeBoard(0L);
        assertThat(removed).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void checkAdmin() {
        when(adminComponent.getPassword()).thenReturn("123");
        var ret = boardController.checkAdmin("123");
        assertThat(ret).isEqualTo( ResponseEntity.ok(""));
    }

    @Test
    void checkAdminFalse() {
        when(adminComponent.getPassword()).thenReturn("123");
        var ret = boardController.checkAdmin("1234");
        assertThat(ret).isEqualTo(ResponseEntity.notFound().build());
    }

    @Test
    void checkPassword() {
        Board board = new Board();
        when(boardService.checkPassword(1L,"1")).thenReturn(board);
        var ret = boardController.checkPassword(1L,"1");
        assertThat(ret).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void checkPasswordFalse() {
        when(boardService.checkPassword(-1L,"1")).thenReturn(null);
        var ret = boardController.checkPassword(-1L,"1");
        assertThat(ret).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removePassword() {
        Board board = new Board();
        when(boardService.removePass(1L,"1")).thenReturn(board);
        var ret = boardController.removePassword(1L,"1");
        assertThat(ret).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void removePasswordFailed() {
        when(boardService.removePass(-1L,"1")).thenReturn(null);
        var ret = boardController.removePassword(-1L,"1");
        assertThat(ret).isEqualTo(ResponseEntity.badRequest().build());
    }

    @Test
    void removePasswordAdmin() {
        Board board = new Board();
        when(boardService.removePass(1L)).thenReturn(board);
        var ret = boardController.removePasswordAdmin(1L);
        assertThat(ret).isEqualTo(ResponseEntity.ok(board));
    }

    @Test
    void removePasswordAdminFailed() {
        when(boardService.removePass(-1L)).thenReturn(null);
        var ret = boardController.removePasswordAdmin(-1L);
        assertThat(ret).isEqualTo(ResponseEntity.badRequest().build());
    }

}