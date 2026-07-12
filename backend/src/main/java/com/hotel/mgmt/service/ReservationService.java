package com.hotel.mgmt.service;

import com.hotel.mgmt.dto.ReservationRequest;
import com.hotel.mgmt.dto.ReservationResponse;
import com.hotel.mgmt.entity.*;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.exception.RoomNotAvailableException;
import com.hotel.mgmt.repository.CustomerRepository;
import com.hotel.mgmt.repository.ReservationRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository,
                              CustomerRepository customerRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.customerRepository = customerRepository;
        this.roomService = roomService;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return mapToResponse(reservation);
    }

    @CacheEvict(value = "roomAvailability", allEntries = true)
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.roomId()));

        // Verify dates
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // Check availability
        boolean isAvailable = roomService.isRoomAvailable(room.getId(), request.checkInDate(), request.checkOutDate());
        if (!isAvailable) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available for the selected dates");
        }

        // Find or create customer
        Customer customer = customerRepository.findByEmail(request.customerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .email(request.customerEmail())
                            .firstName(request.customerFirstName())
                            .lastName(request.customerLastName())
                            .phone(request.customerPhone())
                            .passportNumber(request.customerPassportNumber())
                            .build();
                    return customerRepository.save(newCustomer);
                });

        // Calculate billing amount
        long nights = ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // Create reservation
        Reservation reservation = Reservation.builder()
                .room(room)
                .customer(customer)
                .checkInDate(request.checkInDate())
                .checkOutDate(request.checkOutDate())
                .totalAmount(totalAmount)
                .status(ReservationStatus.CONFIRMED)
                .build();

        // Optional: Update room status if check-in is today
        if (request.checkInDate().equals(LocalDate.now())) {
            room.setStatus(RoomStatus.BOOKED);
            roomRepository.save(room);
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    @CacheEvict(value = "roomAvailability", allEntries = true)
    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        reservation.setStatus(ReservationStatus.CANCELLED);
        
        // Reset room status if checked-in today
        Room room = reservation.getRoom();
        if (room.getStatus() == RoomStatus.BOOKED) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    @CacheEvict(value = "roomAvailability", allEntries = true)
    @Transactional
    public ReservationResponse completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        reservation.setStatus(ReservationStatus.COMPLETED);
        
        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToResponse(savedReservation);
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .roomNumber(reservation.getRoom().getRoomNumber())
                .customerEmail(reservation.getCustomer().getEmail())
                .customerName(reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .totalAmount(reservation.getTotalAmount())
                .status(reservation.getStatus())
                .build();
    }
}
