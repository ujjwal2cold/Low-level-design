package models;

import enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Order {
   private final Stock s;
   private final int quantity;
   private final OrderType order;
   private final User u;
}
