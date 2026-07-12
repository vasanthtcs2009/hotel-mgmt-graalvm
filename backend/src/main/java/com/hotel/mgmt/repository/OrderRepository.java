package com.hotel.mgmt.repository;

import com.hotel.mgmt.entity.Order;
import com.hotel.mgmt.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRoomId(Long roomId);
    List<Order> findByStatus(OrderStatus status);
}
