package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Reservation() {}

    public Reservation(Long id, Room room, Customer customer, LocalDate checkInDate, LocalDate checkOutDate, BigDecimal totalAmount, ReservationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.room = room;
        this.customer = customer;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = status;
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    public static class ReservationBuilder {
        private Long id;
        private Room room;
        private Customer customer;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private BigDecimal totalAmount;
        private ReservationStatus status;
        private LocalDateTime createdAt;

        public ReservationBuilder id(Long id) { this.id = id; return this; }
        public ReservationBuilder room(Room room) { this.room = room; return this; }
        public ReservationBuilder customer(Customer customer) { this.customer = customer; return this; }
        public ReservationBuilder checkInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; return this; }
        public ReservationBuilder checkOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; return this; }
        public ReservationBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ReservationBuilder status(ReservationStatus status) { this.status = status; return this; }
        public ReservationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Reservation build() {
            return new Reservation(id, room, customer, checkInDate, checkOutDate, totalAmount, status, createdAt);
        }
    }
}
