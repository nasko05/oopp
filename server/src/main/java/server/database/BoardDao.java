package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardDao extends JpaRepository<Board, Long> {
    /**
     * Custom method that finds board by userID
     * @param userID provided user-friendly id
     * @return found board or null if none is found
     */
    Optional<Board> findBoardByUserId(String userID);

    /**
     * Custom method that checks if current
     * user-friendly id has already been used
     * @param userID id
     * @return true or false
     */
    boolean existsBoardByUserId(String userID);
}
