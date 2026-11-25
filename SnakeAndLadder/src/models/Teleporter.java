package models;

import enums.TeleporterType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Teleporter {
   private final int src, dest;
   public abstract TeleporterType getType();
}
