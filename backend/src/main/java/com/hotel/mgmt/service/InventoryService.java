package com.hotel.mgmt.service;

import com.hotel.mgmt.entity.InventoryItem;
import com.hotel.mgmt.exception.InsufficientStockException;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getAllInventory() {
        return inventoryItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemById(Long id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with id: " + id));
    }

    @Transactional
    public InventoryItem createInventoryItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    @Transactional
    public InventoryItem updateStock(Long id, BigDecimal quantityChange) {
        InventoryItem item = getInventoryItemById(id);
        BigDecimal newQuantity = item.getStockQuantity().add(quantityChange);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientStockException("Insufficient stock for " + item.getItemName() + ". Available: " + item.getStockQuantity());
        }
        item.setStockQuantity(newQuantity);
        return inventoryItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Optional<InventoryItem> getInventoryItemByName(String itemName) {
        return inventoryItemRepository.findByItemName(itemName);
    }

    @Transactional
    public void deductStockByName(String itemName, BigDecimal quantity) {
        InventoryItem item = inventoryItemRepository.findByItemName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with name: " + itemName));

        BigDecimal newQuantity = item.getStockQuantity().subtract(quantity);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientStockException("Insufficient stock for " + itemName + ". Available: " + item.getStockQuantity());
        }
        item.setStockQuantity(newQuantity);
        inventoryItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsToReorder() {
        return inventoryItemRepository.findItemsToReorder();
    }
}
