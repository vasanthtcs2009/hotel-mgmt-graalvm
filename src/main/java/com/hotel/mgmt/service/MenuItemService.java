package com.hotel.mgmt.service;

import com.hotel.mgmt.entity.MenuItem;
import com.hotel.mgmt.entity.MenuCategory;
import com.hotel.mgmt.exception.ResourceNotFoundException;
import com.hotel.mgmt.repository.MenuItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Cacheable(value = "menuItems", key = "#id")
    @Transactional(readOnly = true)
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        return menuItemRepository.findByCategory(category);
    }

    @CachePut(value = "menuItems", key = "#result.id")
    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @CachePut(value = "menuItems", key = "#id")
    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItem updatedDetails) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.setName(updatedDetails.getName());
        menuItem.setDescription(updatedDetails.getDescription());
        menuItem.setPrice(updatedDetails.getPrice());
        menuItem.setCategory(updatedDetails.getCategory());
        menuItem.setAvailable(updatedDetails.getAvailable());
        menuItem.setPrepTimeMinutes(updatedDetails.getPrepTimeMinutes());
        return menuItemRepository.save(menuItem);
    }

    @CacheEvict(value = "menuItems", key = "#id")
    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = getMenuItemById(id);
        menuItemRepository.delete(menuItem);
    }
}
