package com.deeptarko.projects.booking.booking_service.repository;

import com.deeptarko.projects.booking.booking_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByHotelIdAndRoomTypeIdAndDateBetween(
            Long hotelId,
            Long roomTypeId,
            LocalDate startDate,
            LocalDate endDate
    );
}

