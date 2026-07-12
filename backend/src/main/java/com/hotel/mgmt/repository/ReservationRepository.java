package com.hotel.mgmt.repository;

import com.hotel.mgmt.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    @Query("""
        SELECT COUNT(r) > 0 FROM Reservation r
        WHERE r.room.id = :roomId
          AND r.status = 'CONFIRMED'
          AND NOT (r.checkOutDate <= :checkIn OR r.checkInDate >= :checkOut)
    """)
    boolean hasOverlappingReservations(@Param("roomId") Long roomId, 
                                      @Param("checkIn") LocalDate checkIn, 
                                      @Param("checkOut") LocalDate checkOut);

    List<Reservation> findByCustomerId(Long customerId);
}
