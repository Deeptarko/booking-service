package com.deeptarko.projects.booking.booking_service.service;

import com.deeptarko.projects.booking.booking_service.entity.Inventory;
import com.deeptarko.projects.booking.booking_service.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class BookingService {

    private final InventoryRepository inventoryRepository;

    public BookingService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void reserveRooms(
            Long hotelId,
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate,
            int quantity
    ) {

        log.info("Attempting to reserve {} rooms for Hotel ID: {} from {} to {}",
                quantity, hotelId, startDate, endDate);

        // 1️⃣ Lock inventory rows
        List<Inventory> inventoryList =
                inventoryRepository.findAndLockInventory(
                        hotelId, roomTypeId, startDate, endDate
                );

        // 2️⃣ Validate availability
        for (Inventory inventory : inventoryList) {
            if (inventory.getAvailableRooms() < quantity) {
                log.error("CONCURRENCY FAILURE: Only {} rooms left, but {} requested",
                        inventory.getAvailableRooms(), quantity);
                throw new RuntimeException("Not enough rooms available");
            }
        }

        // 3️⃣ Update inventory
        for (Inventory inventory : inventoryList) {
            inventory.setAvailableRooms(
                    inventory.getAvailableRooms() - quantity
            );
        }

        // 4️⃣ Save changes
        inventoryRepository.saveAll(inventoryList);

        // Booking row would be saved here (PENDING)
    }
}
