package strategies;

import models.Elevator;
import models.HallRequest;

import java.util.List;

public class NearestFirst implements SchedulingStrategy {
   @Override
   public Elevator findOptimal(List<Elevator> elevators, HallRequest request) {
      int minDistance = Integer.MAX_VALUE;
      Elevator bestElevator = null;
      for (Elevator each : elevators) {
         int dist = Math.abs(request.getFloor() - each.getCurrentFloor().get());
         if (dist < minDistance) {
            minDistance = dist;
            bestElevator = each;
         }
      }
      return bestElevator;
   }
}
