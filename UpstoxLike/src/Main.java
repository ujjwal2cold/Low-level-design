import enums.OrderType;
import factories.ExecutingStratFactory;
import factories.FallbackStratFactory;
import factories.ValidationStratFactory;
import models.Order;
import models.Portfolio;
import models.Stock;
import models.User;
import repositories.StockRepo;
import repositories.UserRepo;
import services.StockService;
import services.UpstoxService;
import services.UserService;
import strategies.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final AtomicInteger totalTests = new AtomicInteger(0);
    private static final AtomicInteger passedTests = new AtomicInteger(0);
    private static final AtomicInteger failedTests = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=======================================================================");
        System.out.println("               UPSTOX-LIKE SYSTEM TEST SUITE RUNNER                   ");
        System.out.println("=======================================================================");

        long startTime = System.currentTimeMillis();

        // 1. Model Tests
        runTestGroup("1. MODELS UNIT TESTS", () -> {
            testStockSingleThreaded();
            testOrderModel();
            testPortfolioModel();
            testUserModel();
        });

        // 2. Multithreaded Model Tests
        runTestGroup("2. MODELS CONCURRENCY TESTS", () -> {
            testStockMultithreaded();
            testUserConcurrency();
        });

        // 3. Strategy Tests
        runTestGroup("3. STRATEGIES & FACTORIES TESTS", () -> {
            testValidationStrategies();
            testValidationStratFactory();
            testExecutingStrategies();
            testExecutingStratFactory();
            testFallbackStrategies();
            testFallbackStratFactory();
        });

        // 4. Repository Tests
        runTestGroup("4. REPOSITORIES TESTS", () -> {
            testStockRepo();
            testStockRepoMultithreaded();
            testUserRepo();
            testUserRepoMultithreaded();
        });

        // 5. Service Tests
        runTestGroup("5. SERVICES UNIT TESTS", () -> {
            testStockService();
            testUserService();
            testUpstoxServiceEndToEnd();
        });

        // 6. Comprehensive Multithreading & Race Condition Stress Tests
        runTestGroup("6. MULTITHREADED HIGH-CONCURRENCY STRESS TESTS", () -> {
            testUpstoxServiceConcurrentBuyingExhaustion();
            testUpstoxServiceConcurrentTradingAndShareConservation();
            testConcurrentPriceUpdatesDuringTrading();
        });

        long duration = System.currentTimeMillis() - startTime;

        System.out.println("\n=======================================================================");
        System.out.println("                          TEST SUMMARY                                 ");
        System.out.println("=======================================================================");
        System.out.println(" Total Tests Run : " + totalTests.get());
        System.out.println(" Passed          : " + passedTests.get());
        System.out.println(" Failed          : " + failedTests.get());
        System.out.println(" Execution Time  : " + duration + " ms");
        System.out.println("=======================================================================");

        if (failedTests.get() == 0) {
            System.out.println(" ALL TESTS PASSED SUCCESSFULLY! UpstoxLike system is robust and thread-safe.");
        } else {
            System.out.println(" SOME TESTS FAILED! Please review the failures above.");
        }
    }

    // =========================================================================
    // 1. MODELS UNIT TESTS
    // =========================================================================

    private static void testStockSingleThreaded() {
        Stock stock = new Stock("Apple Inc.", "AAPL", 150, 50);
        assertEquals("AAPL", stock.getId(), "Stock ID should match");
        assertEquals("Apple Inc.", stock.getName(), "Stock Name should match");
        assertEquals(150, stock.getCurrentPrice(), "Stock Price should match");

        // Price update
        stock.setCurrentPrice(155);
        assertEquals(155, stock.getCurrentPrice(), "Updated stock price should match");

        // Try buying within inventory
        boolean buy1 = stock.tryBuying(20);
        assertTrue(buy1, "Buying 20 shares from 50 should succeed");

        // Try buying exact remaining
        boolean buy2 = stock.tryBuying(30);
        assertTrue(buy2, "Buying remaining 30 shares should succeed");

        // Try buying when depleted
        boolean buy3 = stock.tryBuying(1);
        assertFalse(buy3, "Buying from depleted stock should fail");

        // Sell (replenish)
        stock.sell(25);
        boolean buy4 = stock.tryBuying(25);
        assertTrue(buy4, "Buying replenished 25 shares should succeed");
        assertFalse(stock.tryBuying(1), "Stock should now be depleted again");

        // toString check
        assertTrue(stock.toString().contains("AAPL"), "toString should contain stock ID");
    }

    private static void testOrderModel() {
        Stock stock = new Stock("Google LLC", "GOOGL", 2800, 100);
        User user = new User("Alice", "USR_1");
        Order order = new Order(stock, 5, OrderType.BUY_ORDER, user);

        assertEquals(stock, order.getS(), "Order stock should match");
        assertEquals(5, order.getQuantity(), "Order quantity should match");
        assertEquals(OrderType.BUY_ORDER, order.getOrder(), "Order type should match");
        assertEquals(user, order.getU(), "Order user should match");
    }

    private static void testPortfolioModel() {
        Portfolio portfolio = new Portfolio("USR_1");
        Stock reliance = new Stock("Reliance Industries", "RELIANCE", 2500, 100);
        Stock tcs = new Stock("Tata Consultancy Services", "TCS", 3500, 100);
        User dummyUser = new User("Alice", "USR_1");

        assertEquals(0, portfolio.getQuantity(reliance), "Initial stock quantity in portfolio should be 0");
        assertEquals(0L, portfolio.getMarketValue(), "Initial market value should be 0");

        // Execute BUY order
        Order buyReliance = new Order(reliance, 10, OrderType.BUY_ORDER, dummyUser);
        portfolio.executeOrder(buyReliance);
        assertEquals(10, portfolio.getQuantity(reliance), "Portfolio should have 10 shares of RELIANCE");
        assertEquals(10 * 2500L, portfolio.getMarketValue(), "Market value should be 25,000");

        // Buy another stock
        Order buyTcs = new Order(tcs, 5, OrderType.BUY_ORDER, dummyUser);
        portfolio.executeOrder(buyTcs);
        assertEquals(5, portfolio.getQuantity(tcs), "Portfolio should have 5 shares of TCS");
        assertEquals(10 * 2500L + 5 * 3500L, portfolio.getMarketValue(), "Market value should be 42,500");

        // Partial SELL order
        Order sellReliance = new Order(reliance, 4, OrderType.SELL_ORDER, dummyUser);
        portfolio.executeOrder(sellReliance);
        assertEquals(6, portfolio.getQuantity(reliance), "Remaining RELIANCE shares should be 6");

        // Complete SELL order
        Order sellRemainingReliance = new Order(reliance, 6, OrderType.SELL_ORDER, dummyUser);
        portfolio.executeOrder(sellRemainingReliance);
        assertEquals(0, portfolio.getQuantity(reliance), "RELIANCE shares should be 0 after selling all");
        assertEquals(5 * 3500L, portfolio.getMarketValue(), "Market value should now only be TCS shares");

        // Print portfolio test (ensure no exceptions)
        portfolio.printPortFolio();
    }

    private static void testUserModel() {
        User user = new User("Bob", "USR_2");
        assertEquals("USR_2", user.getId(), "User ID should match");
        assertEquals(0, user.getWallet().get(), "Initial wallet balance should be 0");

        // Add money
        user.addMoney(10000);
        assertEquals(10000, user.getWallet().get(), "Wallet should have 10,000");

        Stock stock = new Stock("Infosys", "INFY", 1500, 50);

        // Buy order with sufficient funds
        Order buyOrder = new Order(stock, 4, OrderType.BUY_ORDER, user); // 4 * 1500 = 6000 <= 10000
        boolean executed = user.executeOrder(buyOrder);
        assertTrue(executed, "User should be able to execute buy order with sufficient funds");
        assertEquals(4, user.getQuantityOfStock(stock), "User should hold 4 shares of INFY");
        assertEquals(4 * 1500L, user.getMarketValue(), "User portfolio market value should be 6,000");
        assertEquals(1, user.getOrderHistory().size(), "Order history should have 1 record");

        // Buy order with insufficient funds
        Order expensiveOrder = new Order(stock, 10, OrderType.BUY_ORDER, user); // 10 * 1500 = 15000 > 10000
        boolean failedBuy = user.executeOrder(expensiveOrder);
        assertFalse(failedBuy, "Buy order exceeding wallet balance must fail");
        assertEquals(4, user.getQuantityOfStock(stock), "Shares must remain 4 after failed buy");
        assertEquals(1, user.getOrderHistory().size(), "Failed order should not be added to order history");

        // Sell order with sufficient shares
        Order sellOrder = new Order(stock, 2, OrderType.SELL_ORDER, user);
        boolean executedSell = user.executeOrder(sellOrder);
        assertTrue(executedSell, "User should be able to sell shares they hold");
        assertEquals(2, user.getQuantityOfStock(stock), "User should now hold 2 shares");
        assertEquals(2, user.getOrderHistory().size(), "Order history should have 2 records");

        // Sell order with insufficient shares
        Order overSellOrder = new Order(stock, 5, OrderType.SELL_ORDER, user);
        boolean failedSell = user.executeOrder(overSellOrder);
        assertFalse(failedSell, "Selling more shares than owned must fail");
        assertEquals(2, user.getQuantityOfStock(stock), "Shares must remain 2 after failed sell");

        // Print portfolio execution check
        user.printPortFolio();
    }

    // =========================================================================
    // 2. MODELS CONCURRENCY TESTS
    // =========================================================================

    private static void testStockMultithreaded() {
        // Test atomic quantity decrement under high contention
        // 100 total shares, 20 threads each trying to buy 10 shares. Exactly 10 should succeed, 10 should fail.
        final int initialSupply = 100;
        final int buyQty = 10;
        final int threadCount = 20;

        Stock stock = new Stock("Tesla Inc.", "TSLA", 200, initialSupply);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successfulPurchases = new AtomicInteger(0);
        AtomicInteger failedPurchases = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    if (stock.tryBuying(buyQty)) {
                        successfulPurchases.incrementAndGet();
                    } else {
                        failedPurchases.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        try {
            assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "Concurrent buying should complete within timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        assertEquals(10, successfulPurchases.get(), "Exactly 10 buyers should succeed for 100 shares");
        assertEquals(10, failedPurchases.get(), "Exactly 10 buyers should fail");
        assertFalse(stock.tryBuying(1), "Stock should be completely depleted");

        // Concurrent replenishments and purchases
        ExecutorService mixedExecutor = Executors.newFixedThreadPool(16);
        CountDownLatch mixedStart = new CountDownLatch(1);
        CountDownLatch mixedDone = new CountDownLatch(16);

        for (int i = 0; i < 8; i++) {
            mixedExecutor.submit(() -> {
                try {
                    mixedStart.await();
                    for (int j = 0; j < 100; j++) {
                        stock.sell(1);
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    mixedDone.countDown();
                }
            });
        }

        AtomicInteger totalBought = new AtomicInteger(0);
        for (int i = 0; i < 8; i++) {
            mixedExecutor.submit(() -> {
                try {
                    mixedStart.await();
                    for (int j = 0; j < 100; j++) {
                        if (stock.tryBuying(1)) {
                            totalBought.incrementAndGet();
                        }
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    mixedDone.countDown();
                }
            });
        }

        mixedStart.countDown();
        try {
            assertTrue(mixedDone.await(5, TimeUnit.SECONDS), "Mixed concurrent buy/sell should complete");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mixedExecutor.shutdown();

        // 800 shares were deposited. Total bought + remaining shares must equal 800
        int remaining = 0;
        while (stock.tryBuying(1)) {
            remaining++;
        }
        assertEquals(800, totalBought.get() + remaining, "Total bought + remaining shares must strictly equal total sold");
    }

    private static void testUserConcurrency() {
        // Test concurrent wallet deposits and concurrent order history reads with ReentrantReadWriteLock
        User user = new User("Charlie", "USR_3");
        int threadCount = 20;
        int depositPerThread = 500;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    user.addMoney(depositPerThread);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), "Concurrent deposits should finish");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        assertEquals(threadCount * depositPerThread, user.getWallet().get(),
                "User wallet should reflect all concurrent deposits accurately");
    }

    // =========================================================================
    // 3. STRATEGIES & FACTORIES TESTS
    // =========================================================================

    private static void testValidationStrategies() {
        Stock stock = new Stock("Microsoft", "MSFT", 300, 100);
        User user = new User("David", "USR_4");
        user.addMoney(1000);

        IValidationStrat buyingStrat = new BuyingValidationStrat();
        Order validBuy = new Order(stock, 3, OrderType.BUY_ORDER, user); // 3 * 300 = 900 <= 1000
        Order invalidBuy = new Order(stock, 4, OrderType.BUY_ORDER, user); // 4 * 300 = 1200 > 1000

        assertTrue(buyingStrat.validateOrder(validBuy), "BuyingValidationStrat should pass when funds are sufficient");
        assertFalse(buyingStrat.validateOrder(invalidBuy), "BuyingValidationStrat should fail when funds are insufficient");

        IValidationStrat sellingStrat = new SellingValidationStrat();
        // User has 0 shares initially
        Order invalidSell = new Order(stock, 1, OrderType.SELL_ORDER, user);
        assertFalse(sellingStrat.validateOrder(invalidSell), "SellingValidationStrat should fail when user has 0 shares");

        // Give user 5 shares via buy order execution
        user.executeOrder(new Order(stock, 5, OrderType.BUY_ORDER, user)); // wallet has 1000, 5*300 needs 1500 => wait, add money first
        user.addMoney(1000); // wallet now 2000
        user.executeOrder(new Order(stock, 5, OrderType.BUY_ORDER, user));

        Order validSell = new Order(stock, 5, OrderType.SELL_ORDER, user);
        Order overSell = new Order(stock, 6, OrderType.SELL_ORDER, user);
        assertTrue(sellingStrat.validateOrder(validSell), "SellingValidationStrat should pass when user holds exact shares");
        assertFalse(sellingStrat.validateOrder(overSell), "SellingValidationStrat should fail when user holds fewer shares");
    }

    private static void testValidationStratFactory() {
        Stock stock = new Stock("Stock", "STK", 100, 10);
        User user = new User("User", "U1");

        Order buy = new Order(stock, 1, OrderType.BUY_ORDER, user);
        Order sell = new Order(stock, 1, OrderType.SELL_ORDER, user);

        IValidationStrat v1 = ValidationStratFactory.createValidationStrat(buy);
        assertTrue(v1 instanceof BuyingValidationStrat, "Factory should return BuyingValidationStrat for BUY_ORDER");

        IValidationStrat v2 = ValidationStratFactory.createValidationStrat(sell);
        assertTrue(v2 instanceof SellingValidationStrat, "Factory should return SellingValidationStrat for SELL_ORDER");

        assertThrows(IllegalArgumentException.class, () -> {
            ValidationStratFactory.createValidationStrat(new Order(stock, 1, null, user));
        }, "Factory should throw on null/unsupported OrderType");
    }

    private static void testExecutingStrategies() {
        Map<Stock, Integer> map = new HashMap<>();
        Stock stock = new Stock("Amazon", "AMZN", 100, 100);

        IExecutingStrategy buyingStrat = new BuyingStrat();
        buyingStrat.executeOrder(map, stock, 15);
        assertEquals(15, (int) map.get(stock), "BuyingStrat should add 15 shares to map");

        buyingStrat.executeOrder(map, stock, 10);
        assertEquals(25, (int) map.get(stock), "BuyingStrat should accumulate to 25 shares");

        IExecutingStrategy sellingStrat = new SellingStrat();
        sellingStrat.executeOrder(map, stock, 10);
        assertEquals(15, (int) map.get(stock), "SellingStrat should reduce to 15 shares");

        sellingStrat.executeOrder(map, stock, 15);
        assertFalse(map.containsKey(stock), "SellingStrat should remove stock entry when count reaches 0");
    }

    private static void testExecutingStratFactory() {
        Stock stock = new Stock("Stock", "STK", 100, 10);
        User user = new User("User", "U1");

        Order buy = new Order(stock, 1, OrderType.BUY_ORDER, user);
        Order sell = new Order(stock, 1, OrderType.SELL_ORDER, user);

        assertTrue(ExecutingStratFactory.strategy(buy) instanceof BuyingStrat,
                "Factory should return BuyingStrat for BUY_ORDER");
        assertTrue(ExecutingStratFactory.strategy(sell) instanceof SellingStrat,
                "Factory should return SellingStrat for SELL_ORDER");

        assertThrows(IllegalArgumentException.class, () -> {
            ExecutingStratFactory.strategy(new Order(stock, 1, null, user));
        }, "Factory should throw on unsupported OrderType");
    }

    private static void testFallbackStrategies() {
        StockRepo sRepo = new StockRepo();
        StockService sService = new StockService(sRepo);
        Stock stock = new Stock("Meta", "META", 200, 10);
        sService.addStock(stock);

        User richUser = new User("Rich", "U_RICH");
        richUser.addMoney(5000);

        User brokeUser = new User("Broke", "U_BROKE");
        // brokeUser has 0 money

        // BuyingFallbackStrat - successful case
        IFallBackStrat buyingFallback = new BuyingFallbackStrat();
        Order richOrder = new Order(stock, 5, OrderType.BUY_ORDER, richUser);
        boolean successBuy = buyingFallback.execute(sService, richOrder);
        assertTrue(successBuy, "BuyingFallbackStrat should succeed for rich user");
        assertEquals(5, richUser.getQuantityOfStock(stock), "Rich user should now have 5 shares");

        // BuyingFallbackStrat - rollback case
        // Stock service has 5 shares remaining. Broke user tries to buy 5 shares.
        // svc.tryBuying(stockId, 5) will succeed at stock service level, but u.executeOrder(o) will fail!
        // The fallback must call svc.sell(stockId, 5) to roll back!
        Order brokeOrder = new Order(stock, 5, OrderType.BUY_ORDER, brokeUser);
        boolean failedBuy = buyingFallback.execute(sService, brokeOrder);
        assertFalse(failedBuy, "BuyingFallbackStrat should fail for broke user");
        assertEquals(0, brokeUser.getQuantityOfStock(stock), "Broke user should have 0 shares");

        // Verify that stock was indeed rolled back to stock service!
        // If rolled back, richUser should still be able to buy remaining 5 shares!
        Order richOrder2 = new Order(stock, 5, OrderType.BUY_ORDER, richUser);
        boolean buyAgain = buyingFallback.execute(sService, richOrder2);
        assertTrue(buyAgain, "Stock must have been rolled back so richUser can purchase it");

        // SellingFallbackStrat - successful case
        IFallBackStrat sellingFallback = new SellingFallbackStrat();
        Order sellOrder = new Order(stock, 4, OrderType.SELL_ORDER, richUser);
        boolean sellSuccess = sellingFallback.execute(sService, sellOrder);
        assertTrue(sellSuccess, "SellingFallbackStrat should succeed when user has shares");
        assertEquals(6, richUser.getQuantityOfStock(stock), "Rich user should have 6 shares left (10 - 4)");

        // SellingFallbackStrat - failure case (selling shares not owned)
        Order overSellOrder = new Order(stock, 20, OrderType.SELL_ORDER, richUser);
        boolean sellFailed = sellingFallback.execute(sService, overSellOrder);
        assertFalse(sellFailed, "SellingFallbackStrat should fail when user does not own enough shares");
    }

    private static void testFallbackStratFactory() {
        Stock stock = new Stock("Stock", "STK", 100, 10);
        User user = new User("User", "U1");

        Order buy = new Order(stock, 1, OrderType.BUY_ORDER, user);
        Order sell = new Order(stock, 1, OrderType.SELL_ORDER, user);

        assertTrue(FallbackStratFactory.createFallback(buy) instanceof BuyingFallbackStrat,
                "Factory should return BuyingFallbackStrat for BUY_ORDER");
        assertTrue(FallbackStratFactory.createFallback(sell) instanceof SellingFallbackStrat,
                "Factory should return SellingFallbackStrat for SELL_ORDER");

        assertThrows(IllegalArgumentException.class, () -> {
            FallbackStratFactory.createFallback(new Order(stock, 1, null, user));
        }, "Factory should throw on unsupported OrderType");
    }

    // =========================================================================
    // 4. REPOSITORIES TESTS
    // =========================================================================

    private static void testStockRepo() {
        StockRepo repo = new StockRepo();
        Stock stock = new Stock("Wipro", "WIPRO", 400, 100);

        assertFalse(repo.findById("WIPRO").isPresent(), "Initial repo should not contain WIPRO");
        repo.addStock(stock);
        assertTrue(repo.findById("WIPRO").isPresent(), "Repo should contain WIPRO after addStock");
        assertEquals(stock, repo.findById("WIPRO").get(), "Retrieved stock should match added stock");
    }

    private static void testStockRepoMultithreaded() {
        StockRepo repo = new StockRepo();
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    repo.addStock(new Stock("Stock" + id, "STK_" + id, 100 + id, 50));
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), "Concurrent StockRepo insertions should finish");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        for (int i = 0; i < threadCount; i++) {
            assertTrue(repo.findById("STK_" + i).isPresent(), "Stock STK_" + i + " must be present in repo");
        }
    }

    private static void testUserRepo() {
        UserRepo repo = new UserRepo();
        User user = new User("Edward", "USR_ED");

        assertFalse(repo.findById("USR_ED").isPresent(), "Initial repo should not contain USR_ED");
        repo.addUser(user);
        assertTrue(repo.findById("USR_ED").isPresent(), "Repo should contain USR_ED after addUser");
        assertEquals(user, repo.findById("USR_ED").get(), "Retrieved user should match added user");
    }

    private static void testUserRepoMultithreaded() {
        UserRepo repo = new UserRepo();
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    repo.addUser(new User("User" + id, "U_" + id));
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS), "Concurrent UserRepo insertions should finish");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        for (int i = 0; i < threadCount; i++) {
            assertTrue(repo.findById("U_" + i).isPresent(), "User U_" + i + " must be present in repo");
        }
    }

    // =========================================================================
    // 5. SERVICES UNIT TESTS
    // =========================================================================

    private static void testStockService() {
        StockRepo repo = new StockRepo();
        StockService svc = new StockService(repo);

        Stock stock = new Stock("Netflix", "NFLX", 400, 20);
        svc.addStock(stock);

        assertTrue(svc.exists("NFLX"), "NFLX should exist in service");
        assertFalse(svc.exists("UNKNOWN"), "UNKNOWN stock should not exist");

        // Price update
        svc.setCurrentPrice("NFLX", 420);
        assertEquals(420, stock.getCurrentPrice(), "Stock price should be updated via service");

        // Try buying
        assertTrue(svc.tryBuying("NFLX", 15), "Should successfully buy 15 shares of NFLX");
        assertFalse(svc.tryBuying("NFLX", 10), "Should fail to buy 10 shares when only 5 remain");
        assertTrue(svc.tryBuying("NFLX", 5), "Should successfully buy remaining 5 shares");
        assertFalse(svc.tryBuying("NFLX", 1), "Should fail when 0 remain");

        // Replenish
        svc.sell("NFLX", 10);
        assertTrue(svc.tryBuying("NFLX", 10), "Should be able to buy replenished 10 shares");
    }

    private static void testUserService() {
        UserRepo repo = new UserRepo();
        UserService svc = new UserService(repo);

        svc.createUser("USR_5", "Frank");
        assertTrue(svc.exists("USR_5"), "User USR_5 should exist");
        assertFalse(svc.exists("USR_999"), "User USR_999 should not exist");

        Optional<User> userOpt = svc.findById("USR_5");
        assertTrue(userOpt.isPresent(), "User should be found by ID");
        User user = userOpt.get();

        // Initial holdings worth
        assertEquals(0L, svc.getHoldingsWorth("USR_5"), "Initial holdings worth should be 0");

        // Add money & stock to user
        user.addMoney(10000);
        Stock stock = new Stock("Nvidia", "NVDA", 500, 50);
        user.executeOrder(new Order(stock, 4, OrderType.BUY_ORDER, user));

        assertEquals(4 * 500L, svc.getHoldingsWorth("USR_5"), "Holdings worth should be 2,000");
        assertEquals(1, svc.getOrderHistory("USR_5").size(), "Order history should have 1 item");
        assertTrue(svc.getOrderHistory("USR_999").isEmpty(), "Non-existent user should return empty history");

        // Print portfolio execution test
        svc.printPortFolio("USR_5");

        // Non-existent user holdings worth should throw
        assertThrows(IllegalArgumentException.class, () -> {
            svc.getHoldingsWorth("INVALID_USER");
        }, "getHoldingsWorth for non-existent user should throw IllegalArgumentException");
    }

    private static void testUpstoxServiceEndToEnd() {
        StockRepo sRepo = new StockRepo();
        UserRepo uRepo = new UserRepo();
        StockService sService = new StockService(sRepo);
        UserService uService = new UserService(uRepo);
        UpstoxService upstox = new UpstoxService(sService, uService);

        // Setup stocks and users
        Stock apple = new Stock("Apple", "AAPL", 150, 100);
        Stock google = new Stock("Google", "GOOGL", 200, 50);
        upstox.addStock(apple);
        upstox.addStock(google);

        upstox.createUser("U1", "Grace");
        upstox.createUser("U2", "Heidi");

        User u1 = uService.findById("U1").get();
        User u2 = uService.findById("U2").get();

        u1.addMoney(10000);
        u2.addMoney(500);

        // 1. Valid BUY order
        Order o1 = new Order(apple, 10, OrderType.BUY_ORDER, u1); // 10 * 150 = 1500 <= 10000
        boolean buySuccess = upstox.tryExecutingOrder(o1);
        assertTrue(buySuccess, "UpstoxService should execute valid buy order");
        assertEquals(10, u1.getQuantityOfStock(apple), "User U1 should have 10 AAPL shares");
        assertEquals(10 * 150L, upstox.getHoldingsWorth("U1"), "Holdings worth should be 1,500");

        // 2. BUY order with insufficient funds
        Order o2 = new Order(google, 5, OrderType.BUY_ORDER, u2); // 5 * 200 = 1000 > 500
        boolean buyFailedFunds = upstox.tryExecutingOrder(o2);
        assertFalse(buyFailedFunds, "BUY order should fail when user has insufficient wallet");

        // 3. BUY order for non-existent stock or user
        Stock phantomStock = new Stock("Phantom", "PHNTM", 50, 100);
        Order oPhantomStock = new Order(phantomStock, 1, OrderType.BUY_ORDER, u1);
        assertFalse(upstox.tryExecutingOrder(oPhantomStock), "Order for unregistered stock must fail");

        User phantomUser = new User("Ghost", "GHOST");
        Order oPhantomUser = new Order(apple, 1, OrderType.BUY_ORDER, phantomUser);
        assertFalse(upstox.tryExecutingOrder(oPhantomUser), "Order for unregistered user must fail");

        // 4. Valid SELL order
        Order sellO = new Order(apple, 4, OrderType.SELL_ORDER, u1);
        boolean sellSuccess = upstox.tryExecutingOrder(sellO);
        assertTrue(sellSuccess, "UpstoxService should execute valid sell order");
        assertEquals(6, u1.getQuantityOfStock(apple), "User U1 should have 6 AAPL shares remaining");
        assertEquals(6 * 150L, upstox.getHoldingsWorth("U1"), "Holdings worth should update to 900");

        // 5. Invalid SELL order (selling shares user doesn't own)
        Order invalidSell = new Order(google, 1, OrderType.SELL_ORDER, u1);
        assertFalse(upstox.tryExecutingOrder(invalidSell), "Selling shares user does not own must fail");

        // 6. Price change affects portfolio market value
        upstox.setCurrentPrice("AAPL", 200);
        assertEquals(6 * 200L, upstox.getHoldingsWorth("U1"), "Market value should recalculate at new price 200");

        // 7. Order history verification
        List<Order> history = upstox.getOrderHistory("U1");
        assertEquals(2, history.size(), "U1 should have 2 executed orders in history (1 buy, 1 sell)");

        // 8. Print portfolio execution test
        upstox.printPortFolio("U1");
    }

    // =========================================================================
    // 6. MULTITHREADED HIGH-CONCURRENCY STRESS TESTS
    // =========================================================================

    /**
     * Test concurrent buying from multiple threads competing for limited stock inventory.
     * Guarantees that stock inventory never oversells and exact number of successful buys occur.
     */
    private static void testUpstoxServiceConcurrentBuyingExhaustion() {
        StockRepo sRepo = new StockRepo();
        UserRepo uRepo = new UserRepo();
        StockService sService = new StockService(sRepo);
        UserService uService = new UserService(uRepo);
        UpstoxService upstox = new UpstoxService(sService, uService);

        final int initialStock = 50;
        final int buyAmountPerOrder = 5;
        final int numThreads = 20; // 20 threads requesting 5 each => 100 requests for 50 shares
        final int expectedSuccessfulOrders = initialStock / buyAmountPerOrder; // exactly 10

        Stock stock = new Stock("Tata Motors", "TATAMOTORS", 100, initialStock);
        upstox.addStock(stock);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final String userId = "USER_" + i;
            upstox.createUser(userId, "Trader_" + i);
            User user = uService.findById(userId).get();
            user.addMoney(5000); // Plenty of cash

            final Order order = new Order(stock, buyAmountPerOrder, OrderType.BUY_ORDER, user);

            executor.submit(() -> {
                try {
                    startGate.await();
                    if (upstox.tryExecutingOrder(order)) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        try {
            assertTrue(endGate.await(5, TimeUnit.SECONDS), "Concurrent buying exhaustion should finish within timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        assertEquals(expectedSuccessfulOrders, successCount.get(),
                "Exactly " + expectedSuccessfulOrders + " orders should succeed under high concurrency");
        assertEquals(numThreads - expectedSuccessfulOrders, failCount.get(),
                "Remaining " + (numThreads - expectedSuccessfulOrders) + " orders should fail due to stock depletion");

        // Calculate total shares owned across all users
        int totalSharesOwned = 0;
        for (int i = 0; i < numThreads; i++) {
            User u = uService.findById("USER_" + i).get();
            totalSharesOwned += u.getQuantityOfStock(stock);
        }
        assertEquals(initialStock, totalSharesOwned,
                "Total shares distributed across all users must strictly equal initial stock supply");
        assertFalse(upstox.tryBuying("TATAMOTORS", 1), "Stock inventory must be completely depleted");
    }

    /**
     * Test concurrent trading where multiple threads perform BUY and SELL operations simultaneously.
     * Verifies the Conservation Law: (Shares in Exchange Pool) + (Total Shares owned by all Users) == Constant.
     */
    private static void testUpstoxServiceConcurrentTradingAndShareConservation() {
        StockRepo sRepo = new StockRepo();
        UserRepo uRepo = new UserRepo();
        StockService sService = new StockService(sRepo);
        UserService uService = new UserService(uRepo);
        UpstoxService upstox = new UpstoxService(sService, uService);

        final int initialPoolShares = 100;
        final int sharePrice = 50;
        Stock stock = new Stock("State Bank of India", "SBIN", sharePrice, initialPoolShares);
        upstox.addStock(stock);

        final int numUsers = 8;
        for (int i = 0; i < numUsers; i++) {
            upstox.createUser("U_" + i, "Trader_" + i);
            User u = uService.findById("U_" + i).get();
            u.addMoney(50000); // Each user has abundant funds
        }

        final int operationsPerThread = 50;
        final int numThreads = numUsers;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int userIndex = i;
            executor.submit(() -> {
                try {
                    startGate.await();
                    User user = uService.findById("U_" + userIndex).get();
                    Random rnd = new Random(userIndex * 31L);

                    for (int op = 0; op < operationsPerThread; op++) {
                        boolean wantToBuy = rnd.nextBoolean();
                        int qty = rnd.nextInt(3) + 1; // 1 to 3 shares

                        if (wantToBuy) {
                            Order buyOrder = new Order(stock, qty, OrderType.BUY_ORDER, user);
                            upstox.tryExecutingOrder(buyOrder);
                        } else {
                            // Only try to sell if user has shares
                            int have = user.getQuantityOfStock(stock);
                            if (have > 0) {
                                int sellQty = Math.min(qty, have);
                                Order sellOrder = new Order(stock, sellQty, OrderType.SELL_ORDER, user);
                                upstox.tryExecutingOrder(sellOrder);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown();
        try {
            assertTrue(endGate.await(10, TimeUnit.SECONDS), "Concurrent trading stress test should finish within timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();

        // Count total shares owned by all users
        int totalSharesInUsersPortfolios = 0;
        for (int i = 0; i < numUsers; i++) {
            User u = uService.findById("U_" + i).get();
            int qty = u.getQuantityOfStock(stock);
            assertTrue(qty >= 0, "User share balance cannot be negative");
            totalSharesInUsersPortfolios += qty;
        }

        // Count remaining shares in the stock service exchange pool
        int remainingInPool = 0;
        while (upstox.tryBuying("SBIN", 1)) {
            remainingInPool++;
        }

        System.out.println("   [Conservation Check] User Shares: " + totalSharesInUsersPortfolios
                + " | Pool Shares: " + remainingInPool
                + " | Total: " + (totalSharesInUsersPortfolios + remainingInPool)
                + " | Expected: " + initialPoolShares);

        assertEquals(initialPoolShares, totalSharesInUsersPortfolios + remainingInPool,
                "Conservation Law must hold: User Shares + Exchange Pool Shares must equal Initial Shares exactly");
    }

    /**
     * Test concurrent price updates while threads are actively reading portfolio market values.
     * Verifies thread safety and volatility without deadlocks.
     */
    private static void testConcurrentPriceUpdatesDuringTrading() {
        StockRepo sRepo = new StockRepo();
        UserRepo uRepo = new UserRepo();
        StockService sService = new StockService(sRepo);
        UserService uService = new UserService(uRepo);
        UpstoxService upstox = new UpstoxService(sService, uService);

        Stock stock = new Stock("HDFC Bank", "HDFCBANK", 1400, 1000);
        upstox.addStock(stock);

        upstox.createUser("INVESTOR", "Warren");
        User investor = uService.findById("INVESTOR").get();
        investor.addMoney(500000);

        // Buy 100 shares
        upstox.tryExecutingOrder(new Order(stock, 100, OrderType.BUY_ORDER, investor));

        final int durationMs = 1000;
        final AtomicBoolean running = new AtomicBoolean(true);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Thread 1: Constantly updates prices
        executor.submit(() -> {
            int price = 1400;
            while (running.get()) {
                price = 1400 + (int) (Math.random() * 200);
                upstox.setCurrentPrice("HDFCBANK", price);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ignored) {
                }
            }
        });

        // Threads 2, 3, 4: Concurrently read holdings worth and print portfolios
        AtomicInteger readsCompleted = new AtomicInteger(0);
        AtomicBoolean anyReadFailed = new AtomicBoolean(false);
        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                while (running.get()) {
                    long worth = upstox.getHoldingsWorth("INVESTOR");
                    if (worth < 100 * 1400L) {
                        anyReadFailed.set(true);
                    }
                    readsCompleted.incrementAndGet();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
            });
        }

        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException ignored) {
        }

        running.set(false);
        executor.shutdown();
        try {
            assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS), "Threads should terminate cleanly");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(anyReadFailed.get(), "Market value should always reflect at least base price across all concurrent reads");
        assertTrue(readsCompleted.get() > 50, "Should have performed dozens of concurrent price reads smoothly");
    }

    // =========================================================================
    // ASSERTION & TEST FRAMEWORK HELPERS
    // =========================================================================

    private static void runTestGroup(String groupName, Runnable tests) {
        System.out.println("\n--- RUNNING " + groupName + " ---");
        try {
            tests.run();
        } catch (Throwable t) {
            System.err.println("Unexpected fatal error in test group " + groupName + ": " + t.getMessage());
            t.printStackTrace();
        }
    }

    private static void assertTrue(boolean condition, String message) {
        totalTests.incrementAndGet();
        if (condition) {
            passedTests.incrementAndGet();
            System.out.println("  [PASS] " + message);
        } else {
            failedTests.incrementAndGet();
            System.err.println("  [FAIL] Assertion failed: " + message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        totalTests.incrementAndGet();
        if (Objects.equals(expected, actual)) {
            passedTests.incrementAndGet();
            System.out.println("  [PASS] " + message + " (Expected: " + expected + ", Got: " + actual + ")");
        } else {
            failedTests.incrementAndGet();
            System.err.println("  [FAIL] " + message + " (Expected: " + expected + ", Got: " + actual + ")");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedException, Runnable executable, String message) {
        totalTests.incrementAndGet();
        try {
            executable.run();
            failedTests.incrementAndGet();
            System.err.println("  [FAIL] Expected " + expectedException.getSimpleName() + " was NOT thrown: " + message);
        } catch (Throwable actual) {
            if (expectedException.isInstance(actual)) {
                passedTests.incrementAndGet();
                System.out.println("  [PASS] Correctly threw " + expectedException.getSimpleName() + ": " + message);
            } else {
                failedTests.incrementAndGet();
                System.err.println("  [FAIL] Expected " + expectedException.getSimpleName() + " but caught "
                        + actual.getClass().getSimpleName() + ": " + message);
            }
        }
    }
}
