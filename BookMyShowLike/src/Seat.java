public class Seat {
   private final String id;
   private final SEAT_TYPE type;
   private final int price;
   Seat(String id, SEAT_TYPE type, int price) {
      this.id = id;
      this.type = type;
      this.price = price;
   }
   public String getId() {
      return id;
   }
   public int getPrice(){
      return price;
   }
}
