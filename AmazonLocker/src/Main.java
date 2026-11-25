import enums.CompartmentStatus;
import enums.CompartmentType;
import models.Compartment;
import models.Location;
import models.Locker;
import models.MediumCompartment;
import models.Order;
import models.SmallCompartment;
import models.User;
import services.AmazonLocker;
import services.LockerService;
import strategies.ILockerFindingStrategy;
import strategies.NearestFirst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test suite for the AmazonLocker project.
 * Tests each class written in the codebase according to its current implementation:
 *  - Location
 *  - User
 *  - Order
 *  - CompartmentType & CompartmentStatus (enums)
 *  - SmallCompartment & MediumCompartment (models)
 *  - Locker (models)
 *  - NearestFirst (strategies)
 *  - LockerService (services)
 *  - AmazonLocker (services facade)
 *  - Concurrent Booking (thread-safety verification)
 */
public class Main {
   private static int testsPassed = 0;
   private static int testsFailed = 0;

   public static void main(String[] args) throws InterruptedException {
      System.out.println("=================================================");
      System.out.println("     RUNNING AMAZON LOCKER TEST SUITE            ");
      System.out.println("=================================================\n");

      testLocation();
      testUser();
      testOrder();
      testCompartmentTypeAndStatus();
      testSmallCompartment();
      testMediumCompartment();
      testLocker();
      testNearestFirstStrategy();
      testLockerService();
      testAmazonLockerEndToEnd();
      testConcurrentBooking();

      System.out.println("\n=================================================");
      System.out.printf("TEST RESULTS: %d Passed, %d Failed, %d Total\n",
              testsPassed, testsFailed, (testsPassed + testsFailed));
      System.out.println("=================================================");

      if (testsFailed > 0) {
         System.exit(1);
      }
   }

   // -------------------------------------------------------------
   // 1. Tests for Location
   // -------------------------------------------------------------
   private static void testLocation() {
      System.out.println("[TEST GROUP] Location");

      Location loc1 = new Location(0, 0);
      Location loc2 = new Location(3, 4);
      assertEquals(7, loc1.getDistance(loc2), "Manhattan distance between (0,0) and (3,4) should be 7");
      assertEquals(7, loc2.getDistance(loc1), "Distance should be symmetric");

      Location locSame = new Location(0, 0);
      assertEquals(0, loc1.getDistance(locSame), "Distance between identical coordinates should be 0");

      Location locNegative = new Location(-2, -5);
      Location locPositive = new Location(3, 5);
      // |-2 - 3| + |-5 - 5| = 5 + 10 = 15
      assertEquals(15, locNegative.getDistance(locPositive), "Distance with negative coordinates should be 15");

      assertEquals(0, loc1.getX(), "getX should return 0");
      assertEquals(0, loc1.getY(), "getY should return 0");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 2. Tests for User
   // -------------------------------------------------------------
   private static void testUser() {
      System.out.println("[TEST GROUP] User");

      User user = new User("Alice", "USER-101");
      assertEquals("Alice", user.getName(), "User name should match");
      assertEquals("USER-101", user.getId(), "User id should match");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 3. Tests for Order
   // -------------------------------------------------------------
   private static void testOrder() {
      System.out.println("[TEST GROUP] Order");

      User user = new User("Bob", "USER-102");
      Location orderLoc = new Location(10, 20);
      Order order = new Order("ORD-999", orderLoc, 25.0, 55.0, user);

      assertEquals("ORD-999", order.getOrderId(), "Order id should match");
      assertEquals(orderLoc, order.getLoc(), "Order location should match");
      assertTrue(Math.abs(order.getVolume() - 25.0) < 0.001, "Order volume should match 25.0");
      assertTrue(Math.abs(order.getWeight() - 55.0) < 0.001, "Order weight should match 55.0");
      assertEquals(user, order.getOwner(), "Order owner should match");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 4. Tests for CompartmentType and CompartmentStatus
   // -------------------------------------------------------------
   private static void testCompartmentTypeAndStatus() {
      System.out.println("[TEST GROUP] CompartmentType & CompartmentStatus");

      // Verify statuses
      assertEquals(CompartmentStatus.FREE, CompartmentStatus.valueOf("FREE"), "Status FREE should exist");
      assertEquals(CompartmentStatus.BOOKED, CompartmentStatus.valueOf("BOOKED"), "Status BOOKED should exist");

      // Verify types
      assertEquals(CompartmentType.SMALL, CompartmentType.valueOf("SMALL"), "Type SMALL should exist");
      assertEquals(CompartmentType.MEDIUM, CompartmentType.valueOf("MEDIUM"), "Type MEDIUM should exist");
      assertEquals(CompartmentType.LARGE, CompartmentType.valueOf("LARGE"), "Type LARGE should exist");

      // Test canFit behavior as currently implemented (this.vol <= v && this.wt <= wt)
      // For SMALL(20.0, 50.0):
      assertTrue(CompartmentType.SMALL.canFit(25.0, 60.0),
              "SMALL should fit order with vol=25.0, wt=60.0 (vol >= 20.0 && wt >= 50.0)");
      assertFalse(CompartmentType.SMALL.canFit(15.0, 60.0),
              "SMALL should not fit order with vol=15.0 (vol < 20.0)");
      assertFalse(CompartmentType.SMALL.canFit(25.0, 40.0),
              "SMALL should not fit order with wt=40.0 (wt < 50.0)");

      // For MEDIUM(50.0, 100.0):
      assertTrue(CompartmentType.MEDIUM.canFit(55.0, 110.0),
              "MEDIUM should fit order with vol=55.0, wt=110.0 (vol >= 50.0 && wt >= 100.0)");
      assertFalse(CompartmentType.MEDIUM.canFit(40.0, 110.0),
              "MEDIUM should not fit order with vol=40.0 (vol < 50.0)");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 5. Tests for SmallCompartment
   // -------------------------------------------------------------
   private static void testSmallCompartment() {
      System.out.println("[TEST GROUP] SmallCompartment");

      SmallCompartment sc = new SmallCompartment("LOCKER-1", "SC-101");
      assertEquals("SC-101", sc.getId(), "getId should match SC-101");
      assertEquals(CompartmentType.SMALL, sc.getType(), "getType should be SMALL");
      assertEquals(CompartmentStatus.FREE, sc.getStatus(), "Initial status should be FREE");

      User u = new User("Alice", "U1");
      Order o1 = new Order("O1", new Location(0, 0), 25.0, 60.0, u);
      Order o2 = new Order("O2", new Location(0, 0), 25.0, 60.0, u);

      // Book compartment
      boolean booked = sc.tryBooking(o1);
      assertTrue(booked, "First booking should succeed");
      assertEquals(CompartmentStatus.BOOKED, sc.getStatus(), "Status should be BOOKED after successful booking");

      // Double-booking should fail
      boolean doubleBook = sc.tryBooking(o2);
      assertFalse(doubleBook, "Second booking on already-booked compartment should fail");
      assertEquals(CompartmentStatus.BOOKED, sc.getStatus(), "Status should remain BOOKED");

      // Releasing with wrong order should fail
      boolean releaseWrong = sc.tryReleasing(o2);
      assertFalse(releaseWrong, "Releasing with a different order should fail");
      assertEquals(CompartmentStatus.BOOKED, sc.getStatus(), "Status should remain BOOKED after failed release");

      // Releasing with correct order should succeed
      boolean releaseCorrect = sc.tryReleasing(o1);
      assertTrue(releaseCorrect, "Releasing with the original order should succeed");
      assertEquals(CompartmentStatus.FREE, sc.getStatus(), "Status should become FREE after release");

      // Releasing again when already free should fail
      boolean releaseAgain = sc.tryReleasing(o1);
      assertFalse(releaseAgain, "Releasing an already free compartment should fail");

      // Verify toString contains id and type
      assertTrue(sc.toString().contains("SC-101"), "toString should include compartment ID");
      assertTrue(sc.toString().contains("SMALL"), "toString should include type SMALL");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 6. Tests for MediumCompartment
   // -------------------------------------------------------------
   private static void testMediumCompartment() {
      System.out.println("[TEST GROUP] MediumCompartment");

      MediumCompartment mc = new MediumCompartment("LOCKER-1", "MC-201");
      assertEquals("MC-201", mc.getId(), "getId should match MC-201");
      assertEquals(CompartmentType.MEDIUM, mc.getType(), "getType should be MEDIUM");
      assertEquals(CompartmentStatus.FREE, mc.getStatus(), "Initial status should be FREE");

      User u = new User("Bob", "U2");
      Order order = new Order("O-MED", new Location(1, 1), 55.0, 110.0, u);

      assertTrue(mc.tryBooking(order), "Booking MediumCompartment should succeed");
      assertEquals(CompartmentStatus.BOOKED, mc.getStatus(), "Status should be BOOKED");
      assertTrue(mc.tryReleasing(order), "Releasing MediumCompartment should succeed");
      assertEquals(CompartmentStatus.FREE, mc.getStatus(), "Status should be FREE");

      assertTrue(mc.toString().contains("MC-201"), "toString should include compartment ID");
      assertTrue(mc.toString().contains("MEDIUM"), "toString should include type MEDIUM");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 7. Tests for Locker
   // -------------------------------------------------------------
   private static void testLocker() {
      System.out.println("[TEST GROUP] Locker");

      Location lockerLoc = new Location(5, 5);
      Locker locker = new Locker("LCK-1", lockerLoc);

      assertEquals("LCK-1", locker.getLockerId(), "Locker ID should match LCK-1");
      assertEquals(lockerLoc, locker.getLoc(), "Locker location should match");

      SmallCompartment sc1 = new SmallCompartment("LCK-1", "SC-1");
      locker.addCompartment(sc1);

      User user = new User("Charlie", "U3");
      Order order1 = new Order("ORD-1", new Location(5, 5), 25.0, 60.0, user);
      Order order2 = new Order("ORD-2", new Location(5, 5), 25.0, 60.0, user);

      // Booking when compartment is available
      Optional<Compartment> bookedComp = locker.tryBooking(order1);
      assertTrue(bookedComp.isPresent(), "Locker should successfully book an available compartment");
      assertEquals("SC-1", getCompartmentId(bookedComp.get()), "Booked compartment should have ID SC-1");

      // Booking when locker is full
      Optional<Compartment> secondBooking = locker.tryBooking(order2);
      assertFalse(secondBooking.isPresent(), "Locker should return empty when all compartments are booked");

      // After release, booking should succeed again
      boolean released = sc1.tryReleasing(order1);
      assertTrue(released, "Compartment release should succeed");

      Optional<Compartment> rebooking = locker.tryBooking(order2);
      assertTrue(rebooking.isPresent(), "Locker should successfully re-book after compartment was released");
      assertEquals("SC-1", getCompartmentId(rebooking.get()), "Re-booked compartment should have ID SC-1");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 8. Tests for NearestFirst Strategy
   // -------------------------------------------------------------
   private static void testNearestFirstStrategy() {
      System.out.println("[TEST GROUP] NearestFirst Strategy");

      ILockerFindingStrategy strategy = new NearestFirst();

      // Setup 3 lockers at different distances from (0, 0):
      // Locker Far: (20, 20) -> distance = 40
      // Locker Mid: (5, 5)   -> distance = 10
      // Locker Near: (1, 1)  -> distance = 2
      Locker lockerFar = new Locker("LCK-FAR", new Location(20, 20));
      Locker lockerMid = new Locker("LCK-MID", new Location(5, 5));
      Locker lockerNear = new Locker("LCK-NEAR", new Location(1, 1));

      SmallCompartment compFar = new SmallCompartment("LCK-FAR", "C-FAR");
      SmallCompartment compMid = new SmallCompartment("LCK-MID", "C-MID");
      SmallCompartment compNear = new SmallCompartment("LCK-NEAR", "C-NEAR");

      lockerFar.addCompartment(compFar);
      lockerMid.addCompartment(compMid);
      lockerNear.addCompartment(compNear);

      List<Locker> allLockers = new ArrayList<>();
      // Add in non-sorted order
      allLockers.add(lockerFar);
      allLockers.add(lockerNear);
      allLockers.add(lockerMid);

      User user = new User("David", "U4");
      Order order1 = new Order("ORD-1", new Location(0, 0), 25.0, 60.0, user);

      // 1. Should select nearest locker (LCK-NEAR)
      Optional<Compartment> res1 = strategy.tryBooking(allLockers, order1);
      assertTrue(res1.isPresent(), "Strategy should find a compartment");
      assertEquals("C-NEAR", getCompartmentId(res1.get()), "Strategy should pick compartment from nearest locker (LCK-NEAR)");

      // 2. Next order should fall back to next nearest (LCK-MID) because LCK-NEAR is full
      Order order2 = new Order("ORD-2", new Location(0, 0), 25.0, 60.0, user);
      Optional<Compartment> res2 = strategy.tryBooking(allLockers, order2);
      assertTrue(res2.isPresent(), "Strategy should find next available compartment");
      assertEquals("C-MID", getCompartmentId(res2.get()), "Strategy should pick compartment from next nearest locker (LCK-MID)");

      // 3. Third order should go to LCK-FAR
      Order order3 = new Order("ORD-3", new Location(0, 0), 25.0, 60.0, user);
      Optional<Compartment> res3 = strategy.tryBooking(allLockers, order3);
      assertTrue(res3.isPresent(), "Strategy should find third compartment");
      assertEquals("C-FAR", getCompartmentId(res3.get()), "Strategy should pick compartment from farthest locker (LCK-FAR)");

      // 4. Fourth order should fail because all are now booked
      Order order4 = new Order("ORD-4", new Location(0, 0), 25.0, 60.0, user);
      Optional<Compartment> res4 = strategy.tryBooking(allLockers, order4);
      assertFalse(res4.isPresent(), "Strategy should return empty when all lockers are full");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 9. Tests for LockerService
   // -------------------------------------------------------------
   private static void testLockerService() {
      System.out.println("[TEST GROUP] LockerService");

      ILockerFindingStrategy strat = new NearestFirst();
      LockerService svc = new LockerService(strat);

      // Adding lockers
      svc.addLocker("SVC-L1", new Location(2, 2));
      svc.addLocker("SVC-L2", new Location(8, 8));

      assertEquals(2, svc.getLockers().size(), "LockerService should have 2 lockers registered");
      assertEquals("SVC-L1", svc.getLockers().get(0).getLockerId(), "First locker ID should match");

      // Adding compartment
      SmallCompartment sc = new SmallCompartment("SVC-L1", "SC-SVC-1");
      svc.addCompartment("SVC-L1", sc);

      User user = new User("Eva", "U5");
      Order order = new Order("O-SVC", new Location(2, 2), 25.0, 60.0, user);

      // Direct assignment by locker ID
      Optional<Compartment> assigned = svc.tryAssigningOrder("SVC-L1", order);
      assertTrue(assigned.isPresent(), "Should assign order to SVC-L1");
      assertEquals("SC-SVC-1", getCompartmentId(assigned.get()), "Assigned compartment ID should match");

      // Non-existent locker
      Optional<Compartment> nonExistent = svc.tryAssigningOrder("SVC-UNKNOWN", order);
      assertFalse(nonExistent.isPresent(), "Should return empty for unknown locker ID");

      // Release compartment via service
      svc.releaseCompartment(assigned.get(), order);
      assertEquals(CompartmentStatus.FREE, sc.getStatus(), "Compartment should be FREE after service release");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 10. Tests for AmazonLocker End-to-End Facade
   // -------------------------------------------------------------
   private static void testAmazonLockerEndToEnd() {
      System.out.println("[TEST GROUP] AmazonLocker End-to-End");

      ILockerFindingStrategy strategy = new NearestFirst();
      LockerService service = new LockerService(strategy);
      AmazonLocker amazonLocker = new AmazonLocker(service, strategy);

      // Register Lockers at different locations
      amazonLocker.addLocker("LOCKER-DOWNTOWN", new Location(5, 5));
      amazonLocker.addLocker("LOCKER-SUBURB", new Location(25, 25));

      // Add Compartments
      SmallCompartment cDowntown = new SmallCompartment("LOCKER-DOWNTOWN", "DOWNTOWN-1");
      SmallCompartment cSuburb = new SmallCompartment("LOCKER-SUBURB", "SUBURB-1");
      amazonLocker.addCompartment("LOCKER-DOWNTOWN", cDowntown);
      amazonLocker.addCompartment("LOCKER-SUBURB", cSuburb);

      // Customer places order near Downtown
      User customer1 = new User("Grace", "CUST-1");
      Order order1 = new Order("ORD-DOWNTOWN-1", new Location(6, 6), 25.0, 60.0, customer1);

      // Try booking
      Optional<Compartment> booked1 = amazonLocker.tryBooking(order1);
      assertTrue(booked1.isPresent(), "AmazonLocker should book a compartment for customer 1");
      assertEquals("DOWNTOWN-1", getCompartmentId(booked1.get()), "Customer 1 should get Downtown locker compartment");

      // Second customer also near Downtown
      User customer2 = new User("Hank", "CUST-2");
      Order order2 = new Order("ORD-DOWNTOWN-2", new Location(6, 6), 25.0, 60.0, customer2);

      // Downtown is full, so should be routed to Suburb
      Optional<Compartment> booked2 = amazonLocker.tryBooking(order2);
      assertTrue(booked2.isPresent(), "AmazonLocker should fallback to Suburb locker");
      assertEquals("SUBURB-1", getCompartmentId(booked2.get()), "Customer 2 should get Suburb locker compartment");

      // Customer 1 picks up package -> releases compartment
      amazonLocker.release(booked1.get(), order1);
      assertEquals(CompartmentStatus.FREE, cDowntown.getStatus(), "Downtown compartment should now be FREE");

      // Third customer near Downtown can now get the newly freed Downtown locker
      User customer3 = new User("Ivy", "CUST-3");
      Order order3 = new Order("ORD-DOWNTOWN-3", new Location(4, 4), 25.0, 60.0, customer3);
      Optional<Compartment> booked3 = amazonLocker.tryBooking(order3);
      assertTrue(booked3.isPresent(), "Customer 3 should successfully book the released compartment");
      assertEquals("DOWNTOWN-1", getCompartmentId(booked3.get()), "Customer 3 should get Downtown locker");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // 11. Tests for Concurrent Booking (Thread-Safety)
   // -------------------------------------------------------------
   private static void testConcurrentBooking() throws InterruptedException {
      System.out.println("[TEST GROUP] Concurrent Booking (Thread-Safety)");

      final SmallCompartment compartment = new SmallCompartment("CONC-LOCKER", "COMP-CONC-1");
      final int threadCount = 10;
      final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      final CountDownLatch readyLatch = new CountDownLatch(threadCount);
      final CountDownLatch startLatch = new CountDownLatch(1);
      final CountDownLatch doneLatch = new CountDownLatch(threadCount);

      final AtomicInteger successCount = new AtomicInteger(0);
      final AtomicInteger failureCount = new AtomicInteger(0);

      for (int i = 0; i < threadCount; i++) {
         final int id = i;
         executor.submit(() -> {
            readyLatch.countDown();
            try {
               startLatch.await(); // Wait for all threads to align
               User user = new User("User-" + id, "U-" + id);
               Order order = new Order("ORD-" + id, new Location(0, 0), 25.0, 60.0, user);

               boolean booked = compartment.tryBooking(order);
               if (booked) {
                  successCount.incrementAndGet();
               } else {
                  failureCount.incrementAndGet();
               }
            } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
            } finally {
               doneLatch.countDown();
            }
         });
      }

      readyLatch.await(); // Wait for all threads to be created and ready
      startLatch.countDown(); // Release threads at the exact same instant
      doneLatch.await(); // Wait for all threads to complete
      executor.shutdown();

      assertEquals(1, successCount.get(), "Exactly ONE thread should successfully book the compartment");
      assertEquals(threadCount - 1, failureCount.get(), "Remaining (threadCount - 1) threads should fail to book");
      assertEquals(CompartmentStatus.BOOKED, compartment.getStatus(), "Compartment should remain in BOOKED status");
      System.out.println("-------------------------------------------------");
   }

   // -------------------------------------------------------------
   // Helper assertion methods
   // -------------------------------------------------------------
   private static String getCompartmentId(Compartment c) {
      if (c instanceof SmallCompartment) {
         return ((SmallCompartment) c).getId();
      } else if (c instanceof MediumCompartment) {
         return ((MediumCompartment) c).getId();
      }
      return c != null ? c.toString() : null;
   }

   private static void assertTrue(boolean condition, String description) {
      if (condition) {
         System.out.println("  [PASS] " + description);
         testsPassed++;
      } else {
         System.err.println("  [FAIL] " + description);
         testsFailed++;
      }
   }

   private static void assertFalse(boolean condition, String description) {
      assertTrue(!condition, description);
   }

   private static void assertEquals(Object expected, Object actual, String description) {
      boolean equals = (expected == null && actual == null) || (expected != null && expected.equals(actual));
      if (equals) {
         System.out.println("  [PASS] " + description);
         testsPassed++;
      } else {
         System.err.println("  [FAIL] " + description + " (Expected: " + expected + ", Actual: " + actual + ")");
         testsFailed++;
      }
   }
}
