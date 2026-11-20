package services;

import models.Elevator;
import models.HallRequest;
import strategies.SchedulingStrategy;

import java.util.List;
import java.util.Optional;

public class ElevatorDispatcher {
   private SchedulingStrategy strat;
   private final List<Elevator> elevators;

   public ElevatorDispatcher(SchedulingStrategy strat, List<Elevator> elevators) {

      this.strat = strat;
      this.elevators = List.copyOf(elevators);
      for (Elevator each : this.elevators)
         new Thread(each).start();
   }

   public void submitExternalRequest(HallRequest r) {
      Elevator best = strat.findOptimal(elevators, r);
      best.addExternalRequest(r);
   }

   public void submitInternalRequest(int elevatorId, int destination) {
      Optional<Elevator> e = elevators.stream().filter(x -> x.getId() == elevatorId).findFirst();
      e.ifPresent(elevator -> elevator.addInternalRequest(destination));
   }
}
