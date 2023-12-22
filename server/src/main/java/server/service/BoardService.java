package server.service;

import commons.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.BoardDao;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BoardService {

    private final BoardDao boardDao;

    /**
     * Injector constructor
     * @param boardDao board DB interface
     */
    @Autowired
    public BoardService(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

    /**
     * Finds a board with a given id (returns null if it does not exist)
     * @param id the id of the board
     * @return the found board (or null)
     */
    public Board getBoardById(long id) {
        if(!boardDao.existsById(id)) {
            return null;
        }
        return boardDao.findById(id).orElse(null);
    }

    /**
     * Finds a board by user-friendly ID
     * @param id given user-friendly id
     * @return board or null if not found
     */
    public Board getBoardByUserId(String id){
        if(!boardDao.existsBoardByUserId(id)){
            return null;
        } else {
            return boardDao.findBoardByUserId(id).get();
        }
    }
    /**
     * Returns a list of all boards
     * @return the list of all boards
     */
    public List<Board> getAllBoards() {
        return boardDao.findAll();
    }

    /**
     * Adds a new board to the db. Returns null, if the board is null
     * @param board the board to add. Can't be null
     * @return the newly added board
     */
    public Board addBoard(Board board) {
        if(board == null) {
            return null;
        }
        if(board.getUserId() == null || board.getUserId().equals("")){
            board.setUserId(generateUserFriendlyID());
        }
        return boardDao.save(board);
    }

    /**
     * Removes a Board from the database
     * @param id id of the Board to be removed
     * @return the deleted Board
     */
    public Board removeBoard(Long id){
        Optional<Board> optional = boardDao.findById(id);
        if(optional.isEmpty()) return null;
        Board result = optional.get();
        boardDao.delete(result);
        return result;
    }

    /**
     * Generates user-friendly id
     * @return the generated id
     */
    private String generateUserFriendlyID(){
        Random random = new Random();
        String randomKey = "";
        do {
            char letter1 = (char) (random.nextInt(26) + 'A');
            int digit1 = random.nextInt(10);
            char letter2 = (char) (random.nextInt(26) + 'A');
            int digit2 = random.nextInt(10);
            char letter3 = (char) (random.nextInt(26) + 'A');
            int digit3 = random.nextInt(10);
            randomKey = "" + letter1 + digit1 + letter2 + digit2 + letter3 + digit3;
        } while (boardDao.existsBoardByUserId(randomKey));
        return randomKey;
    }
    /**
     * Method that checks if password
     * is correct
     * @param boardID id of the board
     * @param password password to check
     * @return board if correct pass, null otherwise
     */
    public Board checkPassword(Long boardID, String password){
        if(!boardDao.existsById(boardID)){
            return null;
        }
        var board = boardDao.findById(boardID).get();
        if(board.getPassword().equals(password)){
            return board;
        } else {
            return null;
        }
    }

    /**
     * Method that removes password
     *
     * @param boardID id of the board
     * @param password password to check
     * @return board if correct pass, null otherwise
     */
    public Board removePass(Long boardID, String password){
        if(!boardDao.existsById(boardID)){
            return null;
        }
        var board = boardDao.findById(boardID).get();
        if(board.getPassword().equals(password)){
            board.setPassword("");
            return boardDao.save(board);
        } else {
            return null;
        }
    }
    /**
     * Method that removes password
     *
     * @param boardID id of the board
     * @return board if correct pass, null otherwise
     */
    public Board removePass(Long boardID){
        if(!boardDao.existsById(boardID)){
            return null;
        }
        var board = boardDao.findById(boardID).get();
        board.setPassword("");
        return boardDao.save(board);
    }
}
