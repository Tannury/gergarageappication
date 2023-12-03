package com.gersgarage.service;

import com.gersgarage.model.Mechanic;
import com.gersgarage.repository.MechanicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MechanicService {
    private final MechanicRepository mechanicRepository;

    @Autowired
    public MechanicService(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    public List<Mechanic> getAllMechanics() {
        return mechanicRepository.findAll();
    }
    
    public Optional<Mechanic> findById(Long id) {
        return mechanicRepository.findById(id);
    }
    
    public Mechanic save(Mechanic mechanic) {
        return mechanicRepository.save(mechanic);
    }
}
