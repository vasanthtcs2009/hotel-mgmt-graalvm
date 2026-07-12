package com.hotel.mgmt.dto;

import com.hotel.mgmt.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservationResponse(
    Long id,
    String roomNumber,
    String customerEmail,
    String customerName,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    BigDecimal totalAmount,
    ReservationStatus status
) {
    public static ReservationResponseBuilder builder() {
        return new ReservationResponseBuilder();
    }

    public static class ReservationResponseBuilder {
        private Long id;
        private String roomNumber;
        private String customerEmail;
        private String customerName;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private BigDecimal totalAmount;
        private ReservationStatus status;

        public ReservationResponseBuilder id(Long id) { this.id = id; return this; }
        public ReservationResponseBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public ReservationResponseBuilder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public ReservationResponseBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public ReservationResponseBuilder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public ReservationResponseBuilder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public ReservationResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ReservationResponseBuilder status(ReservationStatus status) { this.status = status; return this; }

        public ReservationResponse build() {
            return new ReservationResponse(id, roomNumber, customerEmail, customerName, checkInDate, checkOutDate, totalAmount, status);
        }
    }
}
