package com.gersgarage.service;

import com.gersgarage.exception.BookingNotAvailableException;
import com.gersgarage.model.Booking;
import com.gersgarage.model.BookingStatus;
import com.gersgarage.model.BookingType;
import com.gersgarage.model.Mechanic;
import com.gersgarage.repository.BookingRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private static final int MAX_BOOKINGS_PER_DAY = 10; // Example limit
    
    private static final int MAX_MECHANICS_PER_DAY = 4;
    private static final int MAX_BOOKINGS_PER_MECHANIC = 4;
    
    @Autowired
    private MechanicService mechanicService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, MechanicService mechanicService) {
        this.bookingRepository = bookingRepository;
        this.mechanicService = mechanicService;
    }

    public Booking createBooking(Booking booking) {
        if (isSunday(booking.getBookingDate()) || isBookingFull(booking.getBookingDate())) {
            throw new BookingNotAvailableException("Booking not available on this date");
        }
        booking.setStatus(BookingStatus.BOOKED); // Set default status
        return bookingRepository.save(booking);
    }

    private boolean isSunday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private boolean isBookingFull(LocalDate date) {
        return bookingRepository.countByBookingDate(date) >= MAX_BOOKINGS_PER_DAY;
    }
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        // Force initialization of the user for each booking
        bookings.forEach(booking -> Hibernate.initialize(booking.getUser()));
        return bookings;
    }
    
    public List<Booking> getBookingsByDate(LocalDate date) {
        return bookingRepository.findByBookingDate(date);
    }
    
    public List<Booking> getBookingsByWeek(String week) {
        LocalDate startOfWeek = LocalDate.parse(week + "-1", DateTimeFormatter.ISO_WEEK_DATE);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return bookingRepository.findBookingsBetweenDates(startOfWeek, endOfWeek);
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public Booking updateBooking(Booking updatedBooking) {
        return bookingRepository.save(updatedBooking);
    }
    
    public List<Booking> allocateMechanicsToBookings(LocalDate date) {
        List<Mechanic> mechanics = mechanicService.getAllMechanics();
        List<Booking> bookings = getBookingsByDate(date);

        // Shuffle the list of mechanics to distribute the workload randomly
        Collections.shuffle(mechanics);

        // Allocate mechanics to bookings, considering each mechanic can handle 4 bookings max per day
        // and a major repair counts as two bookings
        IntStream.range(0, bookings.size()).forEach(i -> {
            Booking booking = bookings.get(i);
            Mechanic mechanic = mechanics.get(i % mechanics.size());

            // Assume a method in the Booking model to set the mechanic (add this method if it does not exist)
            booking.setMechanic(mechanic);

            // If the booking type is major repair, consider it as two bookings for the mechanic
            if (booking.getBookingType() == BookingType.MAJOR_REPAIR) {
                i++;
            }
        });

        return bookings.stream().limit(MAX_MECHANICS_PER_DAY * MAX_BOOKINGS_PER_MECHANIC).collect(Collectors.toList());
    }
    
    public byte[] getScheduleAsPdf(LocalDate date) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
            document.add(new Paragraph("Schedule for " + date.toString(), font));
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
    
    public Booking allocateMechanicToBooking(Long bookingId, Long mechanicId) {
        Optional<Booking> bookingOptional = getBookingById(bookingId);
        Optional<Mechanic> mechanicOptional = mechanicService.findById(mechanicId);
        
        if (!bookingOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid Booking ID: " + bookingId);
        }
        
        if (!mechanicOptional.isPresent()) {
            throw new IllegalArgumentException("Invalid Mechanic ID: " + mechanicId);
        }
        
        Booking booking = bookingOptional.get();
        Mechanic mechanic = mechanicOptional.get();
        
        booking.setMechanic(mechanic);
        return save(booking);
    }
}
