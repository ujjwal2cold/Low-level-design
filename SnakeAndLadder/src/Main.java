import models.Player;

import java.util.ArrayList;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      Player p1 = new Player("WASD"), p2 = new Player("IJK");
      List<Player> players = new ArrayList<>(List.of(p1, p2));
      Game game = new Game(6, 1, 1, players);
   }
}
