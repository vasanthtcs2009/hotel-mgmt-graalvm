package com.hotel.mgmt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    public Customer() {}

    public Customer(Long id, String firstName, String lastName, String email, String phone, String passportNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public static class CustomerBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String passportNumber;

        public CustomerBuilder id(Long id) { this.id = id; return this; }
        public CustomerBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public CustomerBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public CustomerBuilder email(String email) { this.email = email; return this; }
        public CustomerBuilder phone(String phone) { this.phone = phone; return this; }
        public CustomerBuilder passportNumber(String passportNumber) { this.passportNumber = passportNumber; return this; }

        public Customer build() {
            return new Customer(id, firstName, lastName, email, phone, passportNumber);
        }
    }
}
