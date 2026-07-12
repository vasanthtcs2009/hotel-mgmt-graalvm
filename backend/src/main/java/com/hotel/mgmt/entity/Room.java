package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 50)
    private RoomType roomType;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoomStatus status;

    @Column(name = "bed_count", nullable = false)
    private Integer bedCount;

    @Column(length = 255)
    private String amenities;

    public Room() {}

    public Room(Long id, String roomNumber, RoomType roomType, BigDecimal pricePerNight, RoomStatus status, Integer bedCount, String amenities) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.bedCount = bedCount;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Integer getBedCount() { return bedCount; }
    public void setBedCount(Integer bedCount) { this.bedCount = bedCount; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public static RoomBuilder builder() {
        return new RoomBuilder();
    }

    public static class RoomBuilder {
        private Long id;
        private String roomNumber;
        private RoomType roomType;
        private BigDecimal pricePerNight;
        private RoomStatus status;
        private Integer bedCount;
        private String amenities;

        public RoomBuilder id(Long id) { this.id = id; return this; }
        public RoomBuilder roomNumber(String roomNumber) { this.roomNumber = roomNumber; return this; }
        public RoomBuilder roomType(RoomType roomType) { this.roomType = roomType; return this; }
        public RoomBuilder pricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; return this; }
        public RoomBuilder status(RoomStatus status) { this.status = status; return this; }
        public RoomBuilder bedCount(Integer bedCount) { this.bedCount = bedCount; return this; }
        public RoomBuilder amenities(String amenities) { this.amenities = amenities; return this; }

        public Room build() {
            return new Room(id, roomNumber, roomType, pricePerNight, status, bedCount, amenities);
        }
    }
}
