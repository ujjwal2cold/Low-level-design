package models;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class Player {
   private final String name;
   private int currPos;
   public Player(String name){
      this.name = name;
   }
}
