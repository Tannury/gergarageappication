package com.gersgarage.controller;

import com.gersgarage.model.Booking;
import com.gersgarage.model.Cost;
import com.gersgarage.model.Mechanic;
import com.gersgarage.model.Part;
import com.gersgarage.service.BookingService;
import com.gersgarage.service.CostService;
import com.gersgarage.service.MechanicService;
import com.gersgarage.service.PartService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private MechanicService mechanicService;
    
    @Autowired
    private CostService costService;

    @Autowired
    private PartService partService;

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings()); 
        return "/bookings";
    }

    @GetMapping("/bookings/view")
    public String viewBookingsByDateOrWeek(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                           @RequestParam(required = false) String week,
                                           Model model) {
        if (date != null) {
            model.addAttribute("bookings", bookingService.getBookingsByDate(date));
        } else if (week != null) {
            model.addAttribute("bookings", bookingService.getBookingsByWeek(week));
        }
        return "admin/bookings";
    }
    
    @GetMapping("/adminDashboard")
    public String adminDashboard() {
        return "adminDashboard"; 
    }
    
 // New endpoint to allocate mechanics to bookings for a given date
    @GetMapping("/admin/allocate-mechanics/{date}")
    public String allocateMechanics(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        List<Booking> bookingsWithMechanics = bookingService.allocateMechanicsToBookings(date);
        model.addAttribute("bookingsWithMechanics", bookingsWithMechanics);
        return "admin/allocateMechanics"; // This should be the name of your HTML template
    }

    // Endpoint to download the schedule as a PDF
    @GetMapping("/admin/schedule/download/{date}")
    public ResponseEntity<byte[]> downloadSchedule(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // You would need to implement a method in your service layer to generate the PDF bytes
        byte[] pdfContent = bookingService.getScheduleAsPdf(date);
        String filename = "schedule-" + date.toString() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
    
    // Endpoint to show form for adding costs to a booking
    @GetMapping("/admin/bookings/costs/add")
    public ModelAndView showAddCostsForm() {
        ModelAndView modelAndView = new ModelAndView("admin/addCostsToBooking");
        modelAndView.addObject("parts", partService.findAllParts());
        modelAndView.addObject("bookings", bookingService.getAllBookings());
        modelAndView.addObject("cost", new Cost());
        return modelAndView;
    }

    // Endpoint to handle adding costs to a booking
    @PostMapping("/admin/bookings/costs/add")
    public String addCostsToBooking(@ModelAttribute Cost cost, @RequestParam Long bookingId, @RequestParam Long partId, @RequestParam Integer quantity) {
        Booking booking = bookingService.getBookingById(bookingId).orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        Part part = partService.findById(partId).orElseThrow(() -> new IllegalArgumentException("Invalid part ID"));
        
        // Assuming you have a method to calculate the total cost based on quantity and part price
        double totalCost = part.getPrice() * quantity;
        cost.setAmount(totalCost);
        cost.setDescription(part.getName());
        cost.setBooking(booking);
        
        costService.addCostToBooking(cost);
        return "redirect:/admin/bookings";
    }
    
    // Endpoint to show form for mechanic allocation
    @GetMapping("/admin/bookings/allocate-mechanic/{id}")
    public ModelAndView showAllocateMechanicForm(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/allocateMechanic");
        modelAndView.addObject("booking", bookingService.getBookingById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking ID")));
        modelAndView.addObject("mechanics", mechanicService.getAllMechanics());
        return modelAndView;
    }

    // Endpoint to process mechanic allocation
    @PostMapping("/admin/bookings/allocate-mechanic")
    public String allocateMechanicToBooking(@RequestParam Long bookingId, @RequestParam Long mechanicId) {
        bookingService.allocateMechanicToBooking(bookingId, mechanicId);
        return "redirect:/admin/bookings";
    }
    
    // Endpoint to show form for adding costs to a booking
    @GetMapping("/admin/bookings/add-costs/{id}")
    public ModelAndView showAddCostsForm(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/addCostsToBooking");
        modelAndView.addObject("booking", bookingService.getBookingById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking ID")));
        modelAndView.addObject("parts", partService.findAllParts());
        return modelAndView;
    }

    // Endpoint to process adding costs
    @PostMapping("/admin/bookings/add-costs")
    public String addCostsToBooking(@ModelAttribute Cost cost, @RequestParam Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid booking ID"));
        cost.setBooking(booking);
        costService.addCostToBooking(cost);
        return "redirect:/admin/bookings";
    }
    
    @GetMapping("/mechanics/manage")
    public String manageMechanics(Model model) {
        List<Mechanic> mechanics = mechanicService.getAllMechanics();
        model.addAttribute("mechanics", mechanics);
        return "manageMechanics"; // This should match the name of your mechanics management HTML file
    }
    
 // Method to add a new mechanic
    @PostMapping("/mechanics/add")
    public String addMechanic(@RequestParam String name) {
        Mechanic mechanic = new Mechanic();
        mechanic.setName(name);
        mechanicService.save(mechanic);
        return "redirect:/mechanics/manage"; // Redirect back to the mechanic management page
    }

    @PostMapping("/bookings/allocate-mechanic")
    public String allocateMechanic(@RequestParam Long bookingId, @RequestParam Long mechanicId, RedirectAttributes redirectAttributes) {
        try {
            bookingService.allocateMechanicToBooking(bookingId, mechanicId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/mechanics/manage";
        }
        return "redirect:/mechanics/manage";
    }
}