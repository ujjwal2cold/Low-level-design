package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Location {
   private int x, y;
   public int getDistance(Location other) {
      return Math.abs(this.x - other.getX()) + Math.abs(this.y - other.getY());
   }
}
