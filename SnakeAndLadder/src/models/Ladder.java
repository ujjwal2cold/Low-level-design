package models;

import enums.TeleporterType;

public class Ladder extends Teleporter {
   public Ladder(int src, int dest) {
      super(src, dest);
   }

   @Override
   public TeleporterType getType() {
      return TeleporterType.LADDER;
   }
}
