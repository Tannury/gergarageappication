package com.gersgarage.controller;

import com.gersgarage.model.User;
import com.gersgarage.model.Vehicle;
import com.gersgarage.service.UserService;
import com.gersgarage.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserService userService;  // Add UserService

    @Autowired
    public VehicleController(VehicleService vehicleService, UserService userService) {
        this.vehicleService = vehicleService;
        this.userService = userService;  // Initialize UserService in the constructor
    }

    // Display list of vehicles
    @GetMapping("/list")
    public String listVehicles(Model model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "vehicles";
    }

    // Create a new vehicle
    @GetMapping("/create")
    public String createVehicleForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        return "vehicleForm";
    }

    @PostMapping("/create")
    public String createVehicle(@ModelAttribute("vehicle") Vehicle vehicle, Principal principal, Model model) {
        // Find the logged-in user
        String username = principal.getName();
        User user = userService.findByUsername(username);
        
        // Associate the vehicle with the user
        vehicle.setUser(user);

        if (!vehicleService.isLicenseUnique(vehicle.getLicenseDetails())) {
            model.addAttribute("licenseError", "License already in use.");
            return "vehicleForm";
        }

        vehicleService.save(vehicle);
        return "redirect:/vehicles/list";
    }

    // Edit a vehicle
    @GetMapping("/edit/{id}")
    public String editVehicleForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id).orElse(null);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("isEdit", true);  // Add this line
        return "vehicleForm";
    }

    @PostMapping("/edit/{id}")
    public String editVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.updateVehicle(vehicle);
        return "redirect:/vehicles/list";
    }

    // Delete a vehicle
    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return "redirect:/vehicles/list";
    }
}