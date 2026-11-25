import enums.TeleporterType;
import factories.TeleporterCreationFactory;
import models.Board;
import models.Player;
import models.Teleporter;
import printers.BoardPrinter;
import printers.ConsoleBoardPrinter;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Game {
   private final int boardLen;
   private final int numberOfSnakes, numberOfLadders;
   private final List<Player> players;
   private final Queue<Player> turn;
   private final Board board;
   private final Random rng = new Random();
   private BoardPrinter boardPrinter;

   public Game(int boardLen, int numberOfSnakes, int numberOfLadders, List<Player> players) {
      this(boardLen, numberOfSnakes, numberOfLadders, players, new ConsoleBoardPrinter(), true);
   }

   public Game(int boardLen, int numberOfSnakes, int numberOfLadders, List<Player> players, BoardPrinter boardPrinter) {
      this(boardLen, numberOfSnakes, numberOfLadders, players, boardPrinter, true);
   }

   public Game(int boardLen, int numberOfSnakes, int numberOfLadders, List<Player> players, BoardPrinter boardPrinter, boolean autoStart) {
      this.boardLen = boardLen;
      this.numberOfSnakes = numberOfSnakes;
      this.numberOfLadders = numberOfLadders;
      this.players = List.copyOf(players);
      this.turn = new ArrayDeque<>(this.players);
      this.board = new Board(boardLen);
      this.boardPrinter = boardPrinter != null ? boardPrinter : new ConsoleBoardPrinter();
      initTeleporters();
      if (autoStart) {
         startGame();
      }
   }

   private int rollDice() {
      return rng.nextInt(1, 7);
   }

   private void initTeleporters() {
      for (int i = 1; i <= numberOfSnakes; ) {
         int totalCells = boardLen * boardLen;
         int src = rng.nextInt(0, totalCells - 2), dest = rng.nextInt(src + 1, totalCells - 1);
         Teleporter porter = TeleporterCreationFactory.createTeleporter(TeleporterType.SNAKE, src, dest);
         if (this.board.setTeleporter(porter))
            ++i;
      }
      for (int i = 1; i <= numberOfLadders; ) {
         int totalCells = boardLen * boardLen;
         int src = rng.nextInt(0, totalCells - 2), dest = rng.nextInt(src + 1, totalCells);
         Teleporter porter = TeleporterCreationFactory.createTeleporter(TeleporterType.LADDER, src, dest);
         if (this.board.setTeleporter(porter))
            ++i;
      }
   }

   public void printBoard() {
      printBoard(this.boardPrinter);
   }

   public void printBoard(BoardPrinter printer) {
      if (printer == null) {
         throw new IllegalArgumentException("BoardPrinter cannot be null");
      }
      printer.print(this.board, this.players);
   }

   public void setBoardPrinter(BoardPrinter boardPrinter) {
      if (boardPrinter == null) {
         throw new IllegalArgumentException("BoardPrinter cannot be null");
      }
      this.boardPrinter = boardPrinter;
   }

   public Board getBoard() {
      return board;
   }

   public List<Player> getPlayers() {
      return players;
   }

   public void startGame() {
      if (turn.size() <= 1) {
         System.out.println("Game has already finished or does not have enough players.");
         return;
      }
      System.out.println("Starting Snake And Ladder Game...\n");
      printBoard();

      while (turn.size() > 1) {
         Player front = turn.poll();
         int currPos = front.getCurrPos();
         int dice = rollDice();
         int nextPos = currPos + dice;
         if (!board.isLegalPosition(nextPos)) {
            System.out.println(front.getName() + " rolled a " + dice + " but cannot move beyond the board (stays at " + currPos + ")");
            turn.add(front);
            continue;
         }
         int finalPos = board.getFinalDestinationFromCell(nextPos);
         if (finalPos > nextPos) {
            System.out.println(front.getName() + " rolled a " + dice + ", landed on " + nextPos + " and climbed a Ladder to " + finalPos);
         } else if (finalPos < nextPos) {
            System.out.println(front.getName() + " rolled a " + dice + ", landed on " + nextPos + " and was bitten by a Snake to " + finalPos);
         } else {
            System.out.println(front.getName() + " rolled a " + dice + " and moved from " + currPos + " to " + finalPos);
         }
         front.setCurrPos(finalPos);
         if (!board.reached(finalPos)) {
            turn.add(front);
         } else {
            System.out.println(">>> " + front.getName() + " reached the end cell " + finalPos + " and WON! <<<");
         }
      }
      Player loser = turn.poll();
      if (loser != null) {
         System.out.println("\n" + loser.getName() + " has lost the game.");
      }
      System.out.println("\n--- Final Board State ---");
      printBoard();
   }
}
