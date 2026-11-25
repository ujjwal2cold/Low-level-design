package models;

import enums.TeleporterType;

public class Snake extends Teleporter {
   public Snake(int src, int dest) {
      super(dest, src);
   }

   @Override
   public TeleporterType getType() {
      return TeleporterType.SNAKE;
   }
}
