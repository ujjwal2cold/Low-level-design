package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Order {
   private final String orderId;
   private final Location loc;
   private final double volume;
   private final double weight;
   private final User owner;
}
