package com.gersgarage.model;

import javax.persistence.*;
import java.time.LocalDate; // Import LocalDate from java.time package
import java.util.List;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic; 

    private LocalDate bookingDate; 

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;
    private String customerComments;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cost> costs;

    // Constructors, Getters, Setters
    public Booking() {
    }

    public Booking(User user, Vehicle vehicle, LocalDate bookingDate, BookingType bookingType, String customerComments, BookingStatus status, List<Cost> cost_id) {
        this.user = user;
        this.vehicle = vehicle;
        this.bookingDate = bookingDate;
        this.bookingType = bookingType;
        this.customerComments = customerComments;
        this.status = status;
        this.costs = cost_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getBookingDate() { // Change the return type to LocalDate
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) { // Change the parameter type to LocalDate
        this.bookingDate = bookingDate;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public String getCustomerComments() {
        return customerComments;
    }

    public void setCustomerComments(String customerComments) {
        this.customerComments = customerComments;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public List<Cost> getCosts() {
        return costs;
    }

    public void setCosts(List<Cost> costs) {
        this.costs = costs;
    }
    
    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }
}
