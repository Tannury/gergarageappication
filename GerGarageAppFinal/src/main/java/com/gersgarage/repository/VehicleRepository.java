package com.gersgarage.repository;

import com.gersgarage.model.Vehicle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicenseDetails(String licenseDetails);
    
    // Update method to findByUser_Id
    List<Vehicle> findByUser_Id(Long userId);
}
