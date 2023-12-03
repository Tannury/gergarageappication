package com.gersgarage.service;

import com.gersgarage.model.Vehicle;
import com.gersgarage.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }
    
    public boolean isLicenseUnique(String licenseDetails) {
        return !vehicleRepository.findByLicenseDetails(licenseDetails).isPresent();
    }

    // Additional CRUD methods
    public Vehicle updateVehicle(Vehicle updatedVehicle) {
        return vehicleRepository.save(updatedVehicle);
    }
    
    public List<Vehicle> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUser_Id(userId);
    }
}
