package com.gersgarage.model;

import javax.persistence.*;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private VehicleMake make; // Now using the Enum type

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(unique = true) // Ensure license details are unique
    private String licenseDetails;

    @Enumerated(EnumType.STRING)
    private EngineType engineType;
    
    @ManyToOne
    @JoinColumn(name = "user_id") 
    private User user;

    // Constructors
    public Vehicle() {
    }

    public Vehicle(VehicleMake make, VehicleType type, String licenseDetails, EngineType engineType, User user_id) {
        this.make = make;
        this.type = type;
        this.licenseDetails = licenseDetails;
        this.engineType = engineType;
        this.user = user_id;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleMake getMake() {
        return make;
    }

    public void setMake(VehicleMake make) {
        this.make = make;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getLicenseDetails() {
        return licenseDetails;
    }

    public void setLicenseDetails(String licenseDetails) {
        this.licenseDetails = licenseDetails;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
