package server.api;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.service.BoardService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;
    private final AdminComponent adminComponent;

    /**
     * Constructor for the board controller
     *
     * @param boardService   the board service (injected)
     * @param adminComponent the admin component
     */
    @Autowired
    public BoardController(BoardService boardService, AdminComponent adminComponent) {
        this.boardService = boardService;
        this.adminComponent = adminComponent;
    }

    /**
     * Handles a get request that gets a board by its ID.
     * @param id the ID of the board
     * @return the board with the given ID.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable("id") long id) {
        Board board = boardService.getBoardById(id);
        if(board == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(board);
        }
    }
    /**
     * Handles a get request that gets a board by its ID.
     * @param id the ID of the board
     * @return the board with the given ID.
     */
    @GetMapping("/get/userID/{id}")
    public ResponseEntity<Board> getBoardByUserId(@PathVariable("id") String id) {
        Board board = boardService.getBoardByUserId(id);
        if(board == null) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(board);
        }
    }

    /**
     * Handles a GET request that gets all boards
     * @return a list of all boards
     */
    @GetMapping("/get")
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        if(boards == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(boards);
        }
    }

    /**
     * Adds a new board if it does not exist (or updates if it does)
     * @param board the board to add (or update)
     * @return the newly added/updated board
     */
    @PostMapping("/add")
    public ResponseEntity<Board> addBoard(@RequestBody Board board) {
        Board addedBoard =  boardService.addBoard(board);
        if(addedBoard == null) {
            return ResponseEntity.badRequest().build();
        }else {
            return ResponseEntity.ok(board);
        }
    }

    /**
     * Simple GET mapping to check if admin password is right
     * @param password received password
     * @return response entity with OK if the password is correct, otherwise error code 404
     */
    @GetMapping("/admin/{password}")
    public ResponseEntity<String> checkAdmin(@PathVariable("password") String password){
        if(password.equals(adminComponent.getPassword()))
            return ResponseEntity.ok("");
        return ResponseEntity.notFound().build();
    }

    private final Map<Object, Consumer<Board>> listeners = new HashMap<>();

    /**
     * GET mapping to check if a Board is still in the database.
     * @return the removed Board if it was removed, NOT_MODIFIED otherwise
     */
    @GetMapping("/status")
    DeferredResult<ResponseEntity<Board>> checkStatus(){
        var timeoutValue = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        var res = new DeferredResult<ResponseEntity<Board>>(5000L, timeoutValue);
        var key = new Object();
        listeners.put(key, b ->
            res.setResult(ResponseEntity.ok(b))
        );
        res.onCompletion(() ->
            listeners.remove(key)
        );
        return res;
    }

    /**
     * Removes a Board from the database
     * @param id id of the board to be removed
     * @return the removed Board
     */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Board> removeBoard(@PathVariable("id") Long id) {
        Board removedBoard = boardService.removeBoard(id);
        if(removedBoard == null) {
            return ResponseEntity.badRequest().build();
        }else {
            listeners.forEach((k, v) ->
                v.accept(removedBoard)
            );
            return ResponseEntity.ok(removedBoard);
        }
    }

    /**
     * GET mapping that checks if password
     * is correct
     * @param boardID id of the board
     * @param password password to check
     * @return board if correct pass, null otherwise
     */
    @GetMapping("/check/{boardID}")
    public ResponseEntity<Board> checkPassword(@PathVariable("boardID") Long boardID,
                                               @RequestParam("password") String password){
        var res = boardService.checkPassword(boardID, password);
        if(res == null){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(res);
        }
    }

    /**
     * GET mapping that checks if password
     * is correct
     * @param boardID id of the board
     * @param password password to check
     * @return board if correct pass, null otherwise
     */
    @GetMapping("/remove/pass/{boardID}")
    public ResponseEntity<Board> removePassword(@PathVariable("boardID") Long boardID,
                                               @RequestParam("password") String password){
        var res = boardService.removePass(boardID, password);
        if(res == null){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(res);
        }
    }
    /**
     * GET mapping that checks if password
     * is correct
     * @param boardID id of the board
     * @return board if correct pass, null otherwise
     */
    @GetMapping("/remove/pass/admin/{boardID}")
    public ResponseEntity<Board> removePasswordAdmin(@PathVariable("boardID") Long boardID){
        var res = boardService.removePass(boardID);
        if(res == null){
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(res);
        }
    }
}
