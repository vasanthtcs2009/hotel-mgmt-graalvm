package com.hotel.mgmt.controller;

import com.hotel.mgmt.entity.InventoryItem;
import com.hotel.mgmt.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryItemById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(@Valid @RequestBody InventoryItem item) {
        return new ResponseEntity<>(inventoryService.createInventoryItem(item), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<InventoryItem> updateStock(
            @PathVariable Long id,
            @RequestParam BigDecimal quantityChange) {
        return ResponseEntity.ok(inventoryService.updateStock(id, quantityChange));
    }

    @GetMapping("/reorder")
    public ResponseEntity<List<InventoryItem>> getItemsToReorder() {
        return ResponseEntity.ok(inventoryService.getItemsToReorder());
    }
}
