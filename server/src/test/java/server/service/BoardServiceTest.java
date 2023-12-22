package server.service;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.database.BoardDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;

    @MockBean
    private final BoardDao boardDao = Mockito.mock(BoardDao.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBoardById() {
        Board board = new Board();
        when(boardDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        Board result = boardService.getBoardById(1);
        assertThat(result).isEqualTo(board);
    }

    @Test
    void getBoardByIdNULL() {
        when(boardDao.existsById(any(long.class))).thenReturn(false);
        Board result = boardService.getBoardById(1);
        assertThat(result).isEqualTo(null);
    }

    @Test
    void getBoardByUserId() {
        Board board = new Board();
        when(boardDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        Board result = boardService.getBoardById(1);
        assertThat(result).isEqualTo(board);
    }

    @Test
    void getBoardByUserIdNULL() {
        when(boardDao.existsById(any(long.class))).thenReturn(false);
        Board result = boardService.getBoardById(1);
        assertThat(result).isEqualTo(null);
    }


    @Test
    void getAllBoards() {
        List<Board> boards = new ArrayList<>();
        boards.add(new Board());
        when(boardDao.findAll()).thenReturn(boards);
        var result = boardService.getAllBoards();
        assertThat(result).isEqualTo(boards);
    }

    @Test
    void getAllBoardsNULL() {
        when(boardDao.findAll()).thenReturn(null);
        var result = boardService.getAllBoards();
        assertThat(result).isEqualTo(null);
    }

    @Test
    void addBoard() {
        Board board = new Board();
        when(boardDao.save(any(Board.class))).thenReturn(board);
        Board added = boardService.addBoard(board);
        assertThat(added).isEqualTo(board);
    }

    @Test
    void addBoardNULL() {
        Board added = boardService.addBoard(null);
        assertThat(added).isEqualTo(null);
    }

    @Test
    void removeBoard() {
        Board board = new Board();
        when(boardDao.existsById(any(Long.class))).thenReturn(true);
        when(boardDao.findById(any(Long.class))).thenReturn(Optional.of(board));
        Board result = boardService.getBoardById(1);
        Board removed = boardService.removeBoard(1L);
        assertThat(removed).isEqualTo(result);
    }

    @Test
    void removeBoardNULL() {
        Board removed = boardService.removeBoard(null);
        assertThat(removed).isEqualTo(null);
    }
}