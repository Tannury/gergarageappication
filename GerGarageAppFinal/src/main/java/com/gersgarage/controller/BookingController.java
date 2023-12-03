package com.gersgarage.controller;

import com.gersgarage.exception.BookingNotAvailableException;
import com.gersgarage.model.Booking;
import com.gersgarage.model.User;
import com.gersgarage.model.Vehicle;
import com.gersgarage.service.BookingService;
import com.gersgarage.service.UserService;
import com.gersgarage.service.VehicleService;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService, VehicleService vehicleService, UserService userService) {
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }


    // Display list of bookings
    @GetMapping("/list")
    public String listBookings(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        // Force initialization of the user for each booking
        bookings.forEach(booking -> Hibernate.initialize(booking.getUser()));
        model.addAttribute("bookings", bookings);
        return "bookings";
    }

    // Create a new booking form
    @GetMapping("/create")
    public String createBookingForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Vehicle> userVehicles = vehicleService.getVehiclesByUserId(user.getId());

        model.addAttribute("booking", new Booking());
        model.addAttribute("userVehicles", userVehicles);
        return "bookingForm";
    }

    @PostMapping("/create")
    public String createBooking(@ModelAttribute("booking") Booking booking, Model model, RedirectAttributes redirectAttributes) {
        try {
            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("successMessage", "Booking successfully created!");
        } catch (BookingNotAvailableException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "bookingForm";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Something went wrong. Try again.");
            return "bookingForm";
        }
        return "redirect:/bookings/list";
    }
    
    // Edit a booking
    @GetMapping("/edit/{id}")
    public String editBookingForm(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id).orElse(null);
        model.addAttribute("booking", booking);
        return "bookingForm";
    }

    @PostMapping("/edit/{id}")
    public String editBooking(@ModelAttribute("booking") Booking booking) {
        bookingService.updateBooking(booking);
        return "redirect:/bookings/list";
    }

    // Delete a booking
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return "redirect:/bookings/list";
    }
    
    @GetMapping("/schedule/{date}")
    public ResponseEntity<byte[]> getScheduleForDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        document.add(new Paragraph("Schedule for " + date.toString(), font));
        document.close();
        byte[] bytes = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Ensure the browser downloads the PDF rather than opening it
        headers.setContentDispositionFormData("filename", "schedule-" + date.toString() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
