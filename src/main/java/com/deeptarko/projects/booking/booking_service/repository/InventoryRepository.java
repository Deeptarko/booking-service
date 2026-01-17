package com.deeptarko.projects.booking.booking_service.repository;

import com.deeptarko.projects.booking.booking_service.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT i FROM Inventory i
    WHERE i.hotelId = :hotelId
      AND i.roomTypeId = :roomTypeId
      AND i.date BETWEEN :startDate AND :endDate
""")
    List<Inventory> findAndLockInventory(
            Long hotelId,
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate
    );
}

