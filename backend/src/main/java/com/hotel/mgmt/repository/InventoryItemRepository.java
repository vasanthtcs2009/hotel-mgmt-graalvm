package com.hotel.mgmt.repository;

import com.hotel.mgmt.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByItemName(String itemName);

    @Query("SELECT i FROM InventoryItem i WHERE i.stockQuantity <= i.reorderLevel")
    List<InventoryItem> findItemsToReorder();
}
