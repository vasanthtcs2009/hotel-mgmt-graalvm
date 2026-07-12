package com.hotel.mgmt.repository;

import com.hotel.mgmt.entity.MenuCategory;
import com.hotel.mgmt.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Optional<MenuItem> findByName(String name);
    List<MenuItem> findByCategory(MenuCategory category);
}
