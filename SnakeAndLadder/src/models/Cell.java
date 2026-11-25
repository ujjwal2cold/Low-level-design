package models;

import org.jetbrains.annotations.NotNull;

public class Cell {
   private final int pos;
   private Teleporter teleporter = null;

   public Cell(int pos) {
      this.pos = pos;
   }

   public boolean setTeleporter(@NotNull Teleporter teleporter) {
      if (this.teleporter == null) {
         this.teleporter = teleporter;
         return true;
      }
      return false;
   }

   public int getPos() {
      return pos;
   }

   public Teleporter getTeleporter() {
      return teleporter;
   }

   public boolean hasTeleporter() {
      return teleporter != null;
   }

   public int getDestinationPos() {
      if (teleporter == null)
         return pos;
      return teleporter.getDest();
   }
}
