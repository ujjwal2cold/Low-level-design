package printers;

import enums.TeleporterType;
import models.Board;
import models.Cell;
import models.Player;
import models.Teleporter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Console-based 2D grid renderer for the Snake and Ladder board.
 * Renders the board in classic boustrophedon (zigzag) order from top to bottom,
 * displaying cell indices, teleporters (snakes and ladders), and player locations.
 */
public class ConsoleBoardPrinter implements BoardPrinter {
    private final PrintStream out;

    public ConsoleBoardPrinter() {
        this(System.out);
    }

    public ConsoleBoardPrinter(PrintStream out) {
        this.out = (out != null) ? out : System.out;
    }

    @Override
    public void print(Board board, List<Player> players) {
        if (board == null) {
            out.println("Board is null.");
            return;
        }

        int sideLen = board.getSideLen();
        int totalCells = board.getTotalCells();

        // Map positions to player names
        Map<Integer, List<String>> playersByCell = new HashMap<>();
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    playersByCell.computeIfAbsent(player.getCurrPos(), k -> new ArrayList<>()).add(player.getName());
                }
            }
        }

        // Print Overview / Legend
        printOverview(board, players, playersByCell);

        // Precompute cell contents to determine dynamic column width
        String[] cellTexts = new String[totalCells];
        int maxLen = 0;
        for (int i = 0; i < totalCells; i++) {
            cellTexts[i] = buildCellString(board.getCell(i), totalCells, playersByCell.get(i));
            if (cellTexts[i].length() > maxLen) {
                maxLen = cellTexts[i].length();
            }
        }

        int cellWidth = Math.max(maxLen + 2, 8);
        String rowDivider = buildRowDivider(sideLen, cellWidth);

        // Print Grid from top row down to bottom row
        out.println(rowDivider);
        for (int row = sideLen - 1; row >= 0; row--) {
            StringBuilder rowContent = new StringBuilder();
            rowContent.append("|");

            for (int col = 0; col < sideLen; col++) {
                int cellIndex;
                if (row % 2 == 0) {
                    // Even row: Left to Right
                    cellIndex = row * sideLen + col;
                } else {
                    // Odd row: Right to Left
                    cellIndex = row * sideLen + (sideLen - 1 - col);
                }

                String padded = padCell(cellTexts[cellIndex], cellWidth);
                rowContent.append(padded).append("|");
            }

            out.println(rowContent);
            out.println(rowDivider);
        }
        out.println();
    }

    private void printOverview(Board board, List<Player> players, Map<Integer, List<String>> playersByCell) {
        int sideLen = board.getSideLen();
        int totalCells = board.getTotalCells();

        out.println("================================================================================");
        out.println("                            SNAKE AND LADDER BOARD                              ");
        out.println("================================================================================");
        out.printf("Dimensions : %dx%d (Total cells: %d, Indices: 0 to %d)%n", sideLen, sideLen, totalCells, totalCells - 1);

        // Teleporters summary
        List<String> snakes = new ArrayList<>();
        List<String> ladders = new ArrayList<>();
        for (int i = 0; i < totalCells; i++) {
            Cell cell = board.getCell(i);
            if (cell.hasTeleporter()) {
                Teleporter t = cell.getTeleporter();
                if (t.getType() == TeleporterType.SNAKE) {
                    snakes.add(String.format("Cell %d -> Cell %d", t.getSrc(), t.getDest()));
                } else if (t.getType() == TeleporterType.LADDER) {
                    ladders.add(String.format("Cell %d -> Cell %d", t.getSrc(), t.getDest()));
                }
            }
        }

        out.println("Snakes     : " + (snakes.isEmpty() ? "None" : String.join(", ", snakes)));
        out.println("Ladders    : " + (ladders.isEmpty() ? "None" : String.join(", ", ladders)));

        if (players != null && !players.isEmpty()) {
            List<String> playerSummary = new ArrayList<>();
            for (Player p : players) {
                if (p != null) {
                    playerSummary.add(String.format("%s @ Cell %d", p.getName(), p.getCurrPos()));
                }
            }
            out.println("Players    : " + String.join(", ", playerSummary));
        }
        out.println("--------------------------------------------------------------------------------");
    }

    private String buildCellString(Cell cell, int totalCells, List<String> playerNames) {
        StringBuilder sb = new StringBuilder();
        int pos = cell.getPos();
        sb.append(pos);

        if (cell.hasTeleporter()) {
            Teleporter t = cell.getTeleporter();
            if (t.getType() == TeleporterType.SNAKE) {
                sb.append(" S(->").append(t.getDest()).append(")");
            } else if (t.getType() == TeleporterType.LADDER) {
                sb.append(" L(->").append(t.getDest()).append(")");
            }
        }

        if (playerNames != null && !playerNames.isEmpty()) {
            sb.append(" [").append(String.join(",", playerNames)).append("]");
        } else {
            if (pos == totalCells - 1) {
                sb.append(" [END]");
            } else if (pos == 0) {
                sb.append(" [START]");
            }
        }

        return sb.toString();
    }

    private String buildRowDivider(int sideLen, int cellWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sideLen; i++) {
            sb.append("+");
            sb.append("-".repeat(cellWidth));
        }
        sb.append("+");
        return sb.toString();
    }

    private String padCell(String text, int width) {
        int paddingTotal = width - text.length();
        int leftPad = paddingTotal / 2;
        int rightPad = paddingTotal - leftPad;
        return " ".repeat(Math.max(0, leftPad)) + text + " ".repeat(Math.max(0, rightPad));
    }
}
