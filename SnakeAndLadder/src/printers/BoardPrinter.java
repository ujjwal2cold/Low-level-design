package printers;

import models.Board;
import models.Player;

import java.util.Collections;
import java.util.List;

/**
 * Interface for printing/rendering a Snake and Ladder board.
 * Adheres to:
 * - Single Responsibility Principle (SRP): Isolates presentation logic from domain models (Board, Game).
 * - Open/Closed Principle (OCP): New display formats (GUI, 2D grid, compact text, JSON) can be added without altering Board/Game.
 * - Interface Segregation Principle (ISP): Exposes only essential print operations.
 * - Dependency Inversion Principle (DIP): Board and Game depend upon this abstraction rather than concrete printers.
 */
public interface BoardPrinter {

    /**
     * Prints the board state without player positions.
     *
     * @param board the board to print
     */
    default void print(Board board) {
        print(board, Collections.emptyList());
    }

    /**
     * Prints the board state including player positions.
     *
     * @param board   the board to print
     * @param players the list of active players on the board
     */
    void print(Board board, List<Player> players);
}
