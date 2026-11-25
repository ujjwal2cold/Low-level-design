package factories;

import enums.TeleporterType;
import models.Ladder;
import models.Snake;
import models.Teleporter;

public class TeleporterCreationFactory {
   public static Teleporter createTeleporter(TeleporterType type, int src, int dest) {
      switch (type) {
         case TeleporterType.LADDER -> {
            return new Ladder(src, dest);
         }
         case TeleporterType.SNAKE -> {
            return new Snake(src, dest);
         }
      }
      throw new IllegalArgumentException("Such a teleporter doesn't exist");
   }
}
