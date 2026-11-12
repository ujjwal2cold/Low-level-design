package Strategies.DriverMatching;

import Models.Ride;

import java.util.Optional;


public interface IDriverMatchingStrat {
   Optional<String> tryMatchingAndBook(Ride r);
}
