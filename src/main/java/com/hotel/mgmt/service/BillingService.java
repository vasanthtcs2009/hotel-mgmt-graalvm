package com.hotel.mgmt.service;

import com.hotel.mgmt.dto.InvoiceDto;
import com.hotel.mgmt.dto.OrderResponse;
import com.hotel.mgmt.entity.Reservation;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BillingService {

    private final ReservationRepository reservationRepository;
    private final OrderService orderService;

    public BillingService(ReservationRepository reservationRepository, OrderService orderService) {
        this.reservationRepository = reservationRepository;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public InvoiceDto generateInvoice(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        // Get all restaurant orders charged to this room
        List<OrderResponse> orders = orderService.getOrdersByRoom(reservation.getRoom().getId());

        // Calculate charges
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (nights <= 0) nights = 1; // Minimum 1 night charge

        BigDecimal roomCharges = reservation.getTotalAmount();
        
        BigDecimal restaurantCharges = orders.stream()
                .map(OrderResponse::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = roomCharges.add(restaurantCharges);
        BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
        BigDecimal totalAmount = subtotal.add(taxAmount);

        return InvoiceDto.builder()
                .reservationId(reservation.getId())
                .roomNumber(reservation.getRoom().getRoomNumber())
                .customerName(reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName())
                .customerEmail(reservation.getCustomer().getEmail())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .numberOfNights(nights)
                .roomCharges(roomCharges)
                .restaurantCharges(restaurantCharges)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .orders(orders)
                .build();
    }
}
