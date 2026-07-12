package com.hotel.mgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ReservationRequest(
    @NotNull(message = "Room ID is required") Long roomId,
    @NotNull(message = "Customer email is required") @Email(message = "Invalid email format") String customerEmail,
    @NotNull(message = "Customer first name is required") String customerFirstName,
    @NotNull(message = "Customer last name is required") String customerLastName,
    String customerPhone,
    String customerPassportNumber,
    @NotNull(message = "Check-in date is required") LocalDate checkInDate,
    @NotNull(message = "Check-out date is required") LocalDate checkOutDate
) {}
