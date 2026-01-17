package com.deeptarko.projects.booking.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"hotel_id", "room_type_id", "date"}))
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hotelId;
    private Long roomTypeId;
    private LocalDate date;
    private int availableRooms;

}

