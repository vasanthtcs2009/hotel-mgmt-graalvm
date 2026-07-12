package com.hotel.mgmt.repository;

import com.hotel.mgmt.entity.Staff;
import com.hotel.mgmt.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByRole(StaffRole role);
    List<Staff> findByShift(String shift);
}
