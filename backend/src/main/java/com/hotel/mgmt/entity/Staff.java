package com.hotel.mgmt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StaffRole role;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(nullable = false, length = 20)
    private String shift;

    public Staff() {}

    public Staff(Long id, String firstName, String lastName, StaffRole role, BigDecimal salary, String shift) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.salary = salary;
        this.shift = shift;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public static StaffBuilder builder() {
        return new StaffBuilder();
    }

    public static class StaffBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private StaffRole role;
        private BigDecimal salary;
        private String shift;

        public StaffBuilder id(Long id) { this.id = id; return this; }
        public StaffBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public StaffBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public StaffBuilder role(StaffRole role) { this.role = role; return this; }
        public StaffBuilder salary(BigDecimal salary) { this.salary = salary; return this; }
        public StaffBuilder shift(String shift) { this.shift = shift; return this; }

        public Staff build() {
            return new Staff(id, firstName, lastName, role, salary, shift);
        }
    }
}
