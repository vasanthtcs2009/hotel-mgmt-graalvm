package com.hotel.mgmt.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequest(
    Long roomId, // Nullable if table order
    Integer tableNumber, // Nullable if room order
    @NotEmpty(message = "Order must contain at least one item") List<OrderItemRequest> items
) {}
