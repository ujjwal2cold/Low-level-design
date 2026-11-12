package Models;

public class Location {
   private int x, y;
   public Location(int x, int y){
      this.x = x;
      this.y = y;
   }
   public int getDistanceFrom(Location other){
      return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
   }
   public boolean withInRadius(Location other, int radius){
      return getDistanceFrom(other) <= radius;
   }
}
