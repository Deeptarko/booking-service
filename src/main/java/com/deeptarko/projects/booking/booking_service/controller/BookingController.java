package com.deeptarko.projects.booking.booking_service.controller;

import com.deeptarko.projects.booking.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public String test(){
        return "Testing!!!";
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveRooms(
            @RequestParam Long hotelId,
            @RequestParam Long roomTypeId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int quantity
    ) {

        bookingService.reserveRooms(
                hotelId,
                roomTypeId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate),
                quantity
        );

        return ResponseEntity.ok("Booking reserved successfully");
    }
}
