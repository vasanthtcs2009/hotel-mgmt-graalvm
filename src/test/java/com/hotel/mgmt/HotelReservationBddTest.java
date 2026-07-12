package com.hotel.mgmt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.mgmt.dto.*;
import com.hotel.mgmt.entity.*;
import com.hotel.mgmt.repository.InventoryItemRepository;
import com.hotel.mgmt.repository.MenuItemRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class HotelReservationBddTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    @DisplayName("BDD Scenario: Customer searches room, books it, orders room service, and is billed on checkout")
    void testEndToEndReservationAndBillingFlow() throws Exception {
        // ==========================================
        // GIVEN
        // ==========================================
        
        // 1. A Deluxe room "404" is registered in the hotel catalog
        Room room = roomRepository.save(Room.builder()
                .roomNumber("R404")
                .roomType(RoomType.DELUXE)
                .pricePerNight(BigDecimal.valueOf(150.00))
                .status(RoomStatus.AVAILABLE)
                .bedCount(1)
                .amenities("WiFi, Mini Fridge")
                .build());

        // 2. A Menu Item "Classic Cheeseburger" is registered in the restaurant catalog
        MenuItem burger = menuItemRepository.save(MenuItem.builder()
                .name("Classic Cheeseburger")
                .description("Cheeseburger with fries")
                .price(BigDecimal.valueOf(18.50))
                .category(MenuCategory.MAIN_COURSE)
                .available(true)
                .prepTimeMinutes(15)
                .build());

        // 3. Bulks ingredients for main course are tracked in the inventory
        InventoryItem ingredients = inventoryItemRepository.save(InventoryItem.builder()
                .itemName("MAIN_COURSE Ingredients Bulk")
                .category(InventoryCategory.FOOD)
                .stockQuantity(BigDecimal.valueOf(100.00))
                .unit("KG")
                .reorderLevel(BigDecimal.valueOf(10.00))
                .build());

        // ==========================================
        // WHEN
        // ==========================================

        // 1. Customer checks if room is available for next weekend
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(7); // 2 nights stay
        
        mockMvc.perform(get("/api/rooms/{id}/availability", room.getId())
                        .param("checkIn", checkIn.toString())
                        .param("checkOut", checkOut.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // 2. Customer creates a reservation for that room
        ReservationRequest resRequest = new ReservationRequest(
                room.getId(),
                "guest.smith@example.com",
                "John",
                "Smith",
                "+19998887766",
                "PASSPORT404",
                checkIn,
                checkOut
        );

        MvcResult resResult = mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.roomNumber", is("R404")))
                .andExpect(jsonPath("$.totalAmount", is(300.0))) // 150.00 * 2 nights
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andReturn();

        ReservationResponse resResponse = objectMapper.readValue(
                resResult.getResponse().getContentAsString(),
                ReservationResponse.class
        );

        // 3. Customer orders a cheeseburger to their room
        OrderItemRequest orderItem = new OrderItemRequest(burger.getId(), 2, "Extra cheese");
        OrderRequest orderRequest = new OrderRequest(room.getId(), null, List.of(orderItem));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.roomNumber", is("R404")))
                .andExpect(jsonPath("$.totalAmount", is(37.0))) // 18.50 * 2
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.items[0].menuItemName", is("Classic Cheeseburger")));

        // ==========================================
        // THEN
        // ==========================================

        // 1. Stock quantity in inventory should be decremented by 2
        InventoryItem updatedIngredients = inventoryItemRepository.findById(ingredients.getId()).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(98.00).compareTo(updatedIngredients.getStockQuantity()));

        // 2. Front desk generates the checkout invoice
        mockMvc.perform(get("/api/billing/reservation/{reservationId}", resResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomCharges", is(300.0)))      // 2 nights * 150.00
                .andExpect(jsonPath("$.restaurantCharges", is(37.0))) // 2 * 18.50
                .andExpect(jsonPath("$.taxAmount", is(33.7)))         // (300 + 37) * 10% = 33.70
                .andExpect(jsonPath("$.totalAmount", is(370.7)));     // 337.00 + 33.70
    }
}
