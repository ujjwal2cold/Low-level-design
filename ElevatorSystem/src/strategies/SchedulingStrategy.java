package strategies;

import models.Elevator;
import models.HallRequest;

import java.util.List;

public interface SchedulingStrategy {
   Elevator findOptimal(List<Elevator> elevators, HallRequest request);
}
