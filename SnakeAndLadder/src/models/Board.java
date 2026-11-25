package models;

import printers.BoardPrinter;
import printers.ConsoleBoardPrinter;

public class Board {
   private final int sideLen, totalCells;
   private final Cell[] cells;

   public Board(int sideLen) {
      this.sideLen = sideLen;
      this.totalCells = sideLen * sideLen;
      this.cells = new Cell[totalCells];
      for (int i = 0; i < totalCells; i++) {
         this.cells[i] = new Cell(i);
      }
   }

   public boolean setTeleporter(Teleporter teleporter) {
      int src = teleporter.getSrc(), dest = teleporter.getDest();
      if (src >= totalCells || dest >= totalCells)
         return false;
      return cells[src].setTeleporter(teleporter);
   }

   public boolean isLegalPosition(int pos) {
      return pos < totalCells;
   }

   public int getFinalDestinationFromCell(int cell) {
      return cells[cell].getDestinationPos();
   }

   public boolean reached(int cell) {
      return cell == totalCells - 1;
   }

   public int getSideLen() {
      return sideLen;
   }

   public int getTotalCells() {
      return totalCells;
   }

   public Cell getCell(int pos) {
      if (pos < 0 || pos >= totalCells) {
         throw new IndexOutOfBoundsException("Invalid cell position: " + pos);
      }
      return cells[pos];
   }

   public Cell[] getCells() {
      return cells.clone();
   }

   public void printBoard() {
      printBoard(new ConsoleBoardPrinter());
   }

   public void printBoard(BoardPrinter printer) {
      if (printer == null) {
         throw new IllegalArgumentException("BoardPrinter cannot be null");
      }
      printer.print(this);
   }
}
