import enums.Direction;
import models.Elevator;
import models.HallRequest;
import services.ElevatorDispatcher;
import strategies.NearestFirst;

import java.util.List;

public class Main {
   public static void main(String[] args) throws InterruptedException{
      Elevator e = new Elevator(1);
      ElevatorDispatcher dispatcher = new ElevatorDispatcher(new NearestFirst(), List.of(e));
      dispatcher.submitExternalRequest(new HallRequest(10, Direction.UP));
      Thread.sleep(2000);
      dispatcher.submitInternalRequest(1, 12);
      Thread.sleep(2000);
      dispatcher.submitExternalRequest(new HallRequest(15, Direction.UP));
      Thread.sleep(2000);
      dispatcher.submitInternalRequest(1, 20);
      Thread.sleep(2000);
      dispatcher.submitExternalRequest(new HallRequest(17, Direction.DOWN));
      Thread.sleep(2000);
      dispatcher.submitInternalRequest(1, 5);
   }
}
