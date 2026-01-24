package com.deeptarko.projects.booking.booking_service.integration;

import com.deeptarko.projects.booking.booking_service.entity.Inventory;
import com.deeptarko.projects.booking.booking_service.repository.InventoryRepository;
import com.deeptarko.projects.booking.booking_service.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.jpa.show-sql=true"
})
@Testcontainers
class BookingServiceIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private BookingService bookingService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setup() {
        inventoryRepository.deleteAll();
        inventoryRepository.save(Inventory.builder()
                .hotelId(1L)
                .roomTypeId(101L)
                .date(LocalDate.of(2026, 1, 20))
                .availableRooms(5)
                .build());
    }

    @Test
    void shouldPreventDoubleBooking() throws Exception {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Boolean> task = () -> {
            try {
                latch.await();
                bookingService.reserveRooms(
                        1L, 101L,
                        LocalDate.of(2026, 1, 20),
                        LocalDate.of(2026, 1, 21),
                        4
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Future<Boolean> f1 = executor.submit(task);
        Future<Boolean> f2 = executor.submit(task);

        latch.countDown();

        int successCount = 0;
        if (f1.get()) successCount++;
        if (f2.get()) successCount++;

        assertEquals(1, successCount, "Only one thread should have secured the 4 rooms");
        Inventory finalInv = inventoryRepository.findAll().get(0);
        assertEquals(1, finalInv.getAvailableRooms(), "Inventory should be exactly 1");
    }

    @Test
    void shouldPreventDoubleBookingUsingCompletableFuture() throws Exception {

        CountDownLatch latch=new CountDownLatch(1);

        CompletableFuture<Boolean> task1=CompletableFuture.supplyAsync(()->{
            try{
                latch.await();
                bookingService.reserveRooms(
                        1L, 101L,
                        LocalDate.of(2026, 1, 20),
                        LocalDate.of(2026, 1, 21),
                        4
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        CompletableFuture<Boolean>task2=CompletableFuture.supplyAsync(()->{
            try{
                latch.await();
                bookingService.reserveRooms(
                        1L, 101L,
                        LocalDate.of(2026, 1, 20),
                        LocalDate.of(2026, 1, 21),
                        4
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        latch.countDown();
        CompletableFuture.allOf(task1,task2).join();

        int successCount = (task1.get() ? 1 : 0) + (task2.get() ? 1 : 0);
        assertEquals(1, successCount, "Pessimistic lock failed: multiple threads booked the same room");
    }
    
}