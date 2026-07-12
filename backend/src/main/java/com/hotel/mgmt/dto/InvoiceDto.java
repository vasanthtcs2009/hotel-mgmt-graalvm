package com.hotel.mgmt.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceDto(
    Long reservationId,
    String roomNumber,
    String customerName,
    String customerEmail,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    long numberOfNights,
    BigDecimal roomCharges,
    BigDecimal restaurantCharges,
    BigDecimal taxAmount,
    BigDecimal totalAmount,
    List<OrderResponse> orders
) {
    public static InvoiceDtoBuilder builder() {
        return new InvoiceDtoBuilder();
    }

    public static class InvoiceDtoBuilder {
        private Long reservationId;
        private String roomNumber;
        private String customerName;
        private String customerEmail;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private long numberOfNights;
        private BigDecimal roomCharges;
        private BigDecimal restaurantCharges;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private List<OrderResponse> orders;

        public InvoiceDtoBuilder reservationId(Long reservationId) { this.reservationId = reservationId; return this; }
        public InvoiceDtoBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public InvoiceDtoBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public InvoiceDtoBuilder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public InvoiceDtoBuilder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public InvoiceDtoBuilder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public InvoiceDtoBuilder numberOfNights(long numberOfNights) { this.numberOfNights = numberOfNights; return this; }
        public InvoiceDtoBuilder roomCharges(BigDecimal roomCharges) { this.roomCharges = roomCharges; return this; }
        public InvoiceDtoBuilder restaurantCharges(BigDecimal restaurantCharges) { this.restaurantCharges = restaurantCharges; return this; }
        public InvoiceDtoBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public InvoiceDtoBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public InvoiceDtoBuilder orders(List<OrderResponse> orders) { this.orders = orders; return this; }

        public InvoiceDto build() {
            return new InvoiceDto(reservationId, roomNumber, customerName, customerEmail, checkInDate, checkOutDate, numberOfNights, roomCharges, restaurantCharges, taxAmount, totalAmount, orders);
        }
    }
}
