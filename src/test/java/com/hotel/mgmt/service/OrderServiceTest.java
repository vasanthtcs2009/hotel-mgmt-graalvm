package com.hotel.mgmt.service;

import com.hotel.mgmt.dto.OrderItemRequest;
import com.hotel.mgmt.dto.OrderRequest;
import com.hotel.mgmt.dto.OrderResponse;
import com.hotel.mgmt.entity.*;
import com.hotel.mgmt.repository.MenuItemRepository;
import com.hotel.mgmt.repository.OrderRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    private Room room;
    private MenuItem menuItem;
    private OrderRequest validRequest;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(1L)
                .roomNumber("202")
                .roomType(RoomType.SUITE)
                .pricePerNight(BigDecimal.valueOf(300.0))
                .status(RoomStatus.BOOKED)
                .bedCount(2)
                .build();

        menuItem = MenuItem.builder()
                .id(10L)
                .name("Ribeye Steak")
                .description("Ribeye Steak with potatoes")
                .price(BigDecimal.valueOf(45.00))
                .category(MenuCategory.MAIN_COURSE)
                .available(true)
                .prepTimeMinutes(25)
                .build();

        OrderItemRequest itemRequest = new OrderItemRequest(10L, 2, "Medium rare");
        validRequest = new OrderRequest(1L, null, List.of(itemRequest));
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(menuItemRepository.findById(10L)).thenReturn(Optional.of(menuItem));

        Order savedOrder = Order.builder()
                .id(100L)
                .room(room)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(90.00))
                .build();
        
        OrderItem savedItem = OrderItem.builder()
                .id(1L)
                .order(savedOrder)
                .menuItem(menuItem)
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(45.00))
                .comments("Medium rare")
                .build();
        savedOrder.setItems(List.of(savedItem));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.createOrder(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("202", response.roomNumber());
        assertEquals(BigDecimal.valueOf(90.00), response.totalAmount());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(1, response.items().size());
        assertEquals("Ribeye Steak", response.items().get(0).menuItemName());

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
