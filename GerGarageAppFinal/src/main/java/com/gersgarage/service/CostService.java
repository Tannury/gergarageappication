package com.gersgarage.service;

import com.gersgarage.model.Cost;
import com.gersgarage.repository.CostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CostService {

    private final CostRepository costRepository;

    public CostService(CostRepository costRepository) {
        this.costRepository = costRepository;
    }

    @Transactional
    public Cost addCostToBooking(Cost cost) {
        return costRepository.save(cost);
    }

    public List<Cost> findAllCostsByBookingId(Long bookingId) {
        return costRepository.findAll(); 
    }
}
