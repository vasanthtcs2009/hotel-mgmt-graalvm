package com.hotel.mgmt.service;

import com.hotel.mgmt.BaseIntegrationTest;
import com.hotel.mgmt.entity.Room;
import com.hotel.mgmt.entity.RoomStatus;
import com.hotel.mgmt.entity.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class RoomServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testRoomLifecycleAndCaching() {
        // Given
        Room room = Room.builder()
                .roomNumber("ROOM-303")
                .roomType(RoomType.DELUXE)
                .pricePerNight(BigDecimal.valueOf(250.0))
                .status(RoomStatus.AVAILABLE)
                .bedCount(2)
                .amenities("WiFi, Ocean View")
                .build();

        // When: Save room
        Room savedRoom = roomService.createRoom(room);
        Long roomId = savedRoom.getId();
        assertNotNull(roomId);

        // Clear cache first to ensure a clean state
        Objects.requireNonNull(cacheManager.getCache("rooms")).evict(roomId);
        assertNull(Objects.requireNonNull(cacheManager.getCache("rooms")).get(roomId));

        // When: Get room (first time - should load from DB and populate cache)
        Room roomFromDb = roomService.getRoomById(roomId);
        assertNotNull(roomFromDb);
        assertEquals("ROOM-303", roomFromDb.getRoomNumber());

        // Then: Cache should contain the room
        assertNotNull(Objects.requireNonNull(cacheManager.getCache("rooms")).get(roomId));

        // When: Update room status
        roomFromDb.setStatus(RoomStatus.MAINTENANCE);
        Room updatedRoom = roomService.updateRoom(roomId, roomFromDb);
        assertEquals(RoomStatus.MAINTENANCE, updatedRoom.getStatus());

        // Then: Cached room is updated/evicted
        Room cachedAfterUpdate = Objects.requireNonNull(cacheManager.getCache("rooms")).get(roomId, Room.class);
        assertNotNull(cachedAfterUpdate);
        assertEquals(RoomStatus.MAINTENANCE, cachedAfterUpdate.getStatus());

        // When: Delete room
        roomService.deleteRoom(roomId);

        // Then: Cache should be evicted and room should be deleted from DB
        assertNull(Objects.requireNonNull(cacheManager.getCache("rooms")).get(roomId));
        assertThrows(RuntimeException.class, () -> roomService.getRoomById(roomId));
    }
}
