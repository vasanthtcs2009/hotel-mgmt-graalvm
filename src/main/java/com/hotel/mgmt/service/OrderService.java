package com.hotel.mgmt.service;

import com.hotel.mgmt.dto.OrderItemRequest;
import com.hotel.mgmt.dto.OrderItemResponse;
import com.hotel.mgmt.dto.OrderRequest;
import com.hotel.mgmt.dto.OrderResponse;
import com.hotel.mgmt.entity.*;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.repository.MenuItemRepository;
import com.hotel.mgmt.repository.OrderRepository;
import com.hotel.mgmt.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RoomRepository roomRepository;
    private final MenuItemRepository menuItemRepository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository, RoomRepository roomRepository,
                        MenuItemRepository menuItemRepository, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.roomRepository = roomRepository;
        this.menuItemRepository = menuItemRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByRoom(Long roomId) {
        return orderRepository.findByRoomId(roomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Room room = null;
        if (request.roomId() != null) {
            room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.roomId()));
        }

        Order order = Order.builder()
                .room(room)
                .tableNumber(request.tableNumber())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + itemRequest.menuItemId()));

            // Deduct stock from inventory
            deductInventoryForMenuItem(menuItem, itemRequest.quantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .quantity(itemRequest.quantity())
                    .unitPrice(menuItem.getPrice())
                    .comments(itemRequest.comments())
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private void deductInventoryForMenuItem(MenuItem menuItem, int quantity) {
        // Look for ingredient matching the menu item's name
        String ingredientName = menuItem.getName() + " Ingredient";
        Optional<InventoryItem> itemOpt = inventoryService.getInventoryItemByName(ingredientName);
        if (itemOpt.isPresent()) {
            inventoryService.deductStockByName(ingredientName, BigDecimal.valueOf(quantity));
        } else {
            // Fallback: search for a generic ingredient category
            String fallbackIngredient = menuItem.getCategory().name() + " Ingredients Bulk";
            Optional<InventoryItem> fallbackOpt = inventoryService.getInventoryItemByName(fallbackIngredient);
            if (fallbackOpt.isPresent()) {
                inventoryService.deductStockByName(fallbackIngredient, BigDecimal.valueOf(quantity));
            } else {
                // If neither exists, we just log a warning and let the order proceed
                log.warn("No inventory tracking found for menu item {} (nor generic fallback {}). Proceeding without stock deduction.",
                        menuItem.getName(), fallbackIngredient);
            }
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .menuItemId(item.getMenuItem().getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .comments(item.getComments())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .roomNumber(order.getRoom() != null ? order.getRoom().getRoomNumber() : null)
                .tableNumber(order.getTableNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
