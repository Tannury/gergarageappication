package com.gersgarage.service;

import com.gersgarage.model.Part;
import com.gersgarage.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part savePart(Part part) {
        return partRepository.save(part);
    }

    public Optional<Part> findById(Long id) {
        return partRepository.findById(id);
    }

    public List<Part> findAllParts() {
        return partRepository.findAll();
    }

    public void deletePart(Long id) {
        partRepository.deleteById(id);
    }

}
