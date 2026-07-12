package com.hotel.mgmt.service;

import com.hotel.mgmt.entity.Room;
import com.hotel.mgmt.entity.RoomStatus;
import com.hotel.mgmt.entity.RoomType;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.repository.ReservationRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Cacheable(value = "rooms", key = "#id")
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with number: " + roomNumber));
    }

    @Cacheable(value = "roomAvailability", key = "'avail-' + #roomId + '-' + #checkIn + '-' + #checkOut")
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        // First check if the room itself is in MAINTENANCE
        Room room = getRoomById(roomId);
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            return false;
        }
        // Then check for overlapping reservations
        return !reservationRepository.hasOverlappingReservations(roomId, checkIn, checkOut);
    }

    @CachePut(value = "rooms", key = "#result.id")
    @CacheEvict(value = "roomAvailability", allEntries = true)
    @Transactional
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @CachePut(value = "rooms", key = "#id")
    @CacheEvict(value = "roomAvailability", allEntries = true)
    @Transactional
    public Room updateRoom(Long id, Room updatedDetails) {
        Room room = getRoomById(id);
        room.setRoomNumber(updatedDetails.getRoomNumber());
        room.setRoomType(updatedDetails.getRoomType());
        room.setPricePerNight(updatedDetails.getPricePerNight());
        room.setStatus(updatedDetails.getStatus());
        room.setBedCount(updatedDetails.getBedCount());
        room.setAmenities(updatedDetails.getAmenities());
        return roomRepository.save(room);
    }

    @Caching(evict = {
        @CacheEvict(value = "rooms", key = "#id"),
        @CacheEvict(value = "roomAvailability", allEntries = true)
    })
    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}
