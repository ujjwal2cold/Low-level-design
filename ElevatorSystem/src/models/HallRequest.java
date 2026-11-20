package models;

import enums.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HallRequest {
   private final int floor;
   private final Direction direction;
}
