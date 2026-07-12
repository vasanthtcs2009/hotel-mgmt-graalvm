package com.hotel.mgmt.service;

import com.hotel.mgmt.dto.ReservationRequest;
import com.hotel.mgmt.dto.ReservationResponse;
import com.hotel.mgmt.entity.*;
import com.hotel.mgmt.exception.RoomNotAvailableException;
import com.hotel.mgmt.repository.CustomerRepository;
import com.hotel.mgmt.repository.ReservationRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationService reservationService;

    private Room availableRoom;
    private Customer existingCustomer;
    private ReservationRequest validRequest;

    @BeforeEach
    void setUp() {
        availableRoom = Room.builder()
                .id(1L)
                .roomNumber("101")
                .roomType(RoomType.DELUXE)
                .pricePerNight(BigDecimal.valueOf(200.0))
                .status(RoomStatus.AVAILABLE)
                .bedCount(2)
                .build();

        existingCustomer = Customer.builder()
                .id(10L)
                .email("jane.doe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        validRequest = new ReservationRequest(
                1L,
                "jane.doe@example.com",
                "Jane",
                "Doe",
                "+1234567890",
                "PP987654",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3)
        );
    }

    @Test
    void testCreateReservation_Success() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(availableRoom));
        when(roomService.isRoomAvailable(1L, validRequest.checkInDate(), validRequest.checkOutDate())).thenReturn(true);
        when(customerRepository.findByEmail(validRequest.customerEmail())).thenReturn(Optional.of(existingCustomer));

        Reservation savedReservation = Reservation.builder()
                .id(50L)
                .room(availableRoom)
                .customer(existingCustomer)
                .checkInDate(validRequest.checkInDate())
                .checkOutDate(validRequest.checkOutDate())
                .totalAmount(BigDecimal.valueOf(400.0))
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // Act
        ReservationResponse response = reservationService.createReservation(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(50L, response.id());
        assertEquals("101", response.roomNumber());
        assertEquals("jane.doe@example.com", response.customerEmail());
        assertEquals("Jane Doe", response.customerName());
        assertEquals(BigDecimal.valueOf(400.0), response.totalAmount());
        assertEquals(ReservationStatus.CONFIRMED, response.status());

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCreateReservation_RoomNotAvailable() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(availableRoom));
        when(roomService.isRoomAvailable(1L, validRequest.checkInDate(), validRequest.checkOutDate())).thenReturn(false);

        // Act & Assert
        assertThrows(RoomNotAvailableException.class, () -> reservationService.createReservation(validRequest));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
