package com.gersgarage.repository;

import com.gersgarage.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	long countByBookingDate(LocalDate bookingDate);
	List<Booking> findByBookingDate(LocalDate date);
	
	 @Query("SELECT b FROM Booking b WHERE b.bookingDate BETWEEN ?1 AND ?2")
	  List<Booking> findBookingsBetweenDates(LocalDate start, LocalDate end);
}
