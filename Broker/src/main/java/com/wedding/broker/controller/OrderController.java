package com.wedding.broker.controller;

import com.wedding.broker.model.Order;
import com.wedding.broker.model.OrderRequest; // Ensure this DTO exists
import com.wedding.broker.repository.OrderRepository;
import com.wedding.broker.client.VenueClient;
import com.wedding.broker.client.PhotographerClient;
import com.wedding.broker.client.CateringClient;
import com.wedding.broker.model.Venue;
import com.wedding.broker.model.Photographer;
import com.wedding.broker.model.Catering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime; // For setting timestamps
import java.util.List;
// import org.springframework.security.core.context.SecurityContextHolder; // For getting authenticated user

@Controller
@RequestMapping("/order") // Base path is now /order
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private VenueClient venueClient;
    @Autowired
    private PhotographerClient photographerClient;
    @Autowired
    private CateringClient cateringClient;

    // Displays the form to create a new order (if you still use /order/new)
    @GetMapping("/new") // Responds to GET /order/new
    public String showCreateOrderForm(Model model) {
        model.addAttribute("order", new Order()); // Add an empty Order object for form binding
        return "createOrder";
    }

    // This method handles the form submission for placing an order
    // It will now also save the consolidated Order to the database
    @PostMapping // Responds to POST /order
    public String placeOrder(@ModelAttribute OrderRequest orderRequest,
                             Model model, // Keep model for initial page rendering
                             RedirectAttributes redirectAttributes) { // Use RedirectAttributes for flash messages

            Venue venue = null;
            Photographer photographer = null;
            Catering catering = null;
            int totalPrice = 0;

            boolean error = false;
            String errorMessage = null;

            // Confirm venue if selected
            if (orderRequest.getVenueId() != null && !orderRequest.getVenueId().isEmpty()) {
                try {
                    venueClient.confirm(orderRequest.getVenueReservationId());
                    venue = venueClient.getVenueById(orderRequest.getVenueId());
                    totalPrice += venue.getPrice();
                }catch (Exception e){
                    error = true;
                    errorMessage = e.getMessage();
                    venue = null;
                }
            }

            // Confirm photographer if selected
            if (orderRequest.getPhotographerId() != null && !orderRequest.getPhotographerId().isEmpty()) {
                try{
                    photographerClient.confirm(orderRequest.getPhotographerReservationId());
                    photographer = photographerClient.getPhotographerById(orderRequest.getPhotographerId());
                    totalPrice += photographer.getPrice();
                }catch (Exception e){
                    error = true;
                    errorMessage = e.getMessage();
                    photographer = null;
                }
            }

            // Confirm catering if selected
            if (orderRequest.getCateringId() != null && !orderRequest.getCateringId().isEmpty()) {
                try {
                    cateringClient.confirm(orderRequest.getCateringReservationId());
                    catering = cateringClient.getCateringCompanyById(orderRequest.getCateringId());
                    totalPrice += catering.getPrice();
                }catch (Exception e){
                    error = true;
                    errorMessage = e.getMessage();
                    catering = null;
                }
            }
            if(error){
                redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                redirectAttributes.addFlashAttribute("venueReservationId",orderRequest.getVenueReservationId());
                redirectAttributes.addFlashAttribute("photographerReservationId",orderRequest.getPhotographerReservationId());
                redirectAttributes.addFlashAttribute("cateringReservationId",orderRequest.getCateringReservationId());
                return "redirect:/order/errorconfirm";
            }

            // --- Create and save the Order entity to the database ---
            Order newOrder = new Order();
            newOrder.setCreatedAt(LocalDateTime.now());
            newOrder.setUpdatedAt(LocalDateTime.now());
            newOrder.setDate(orderRequest.getDate());
            newOrder.setLocation(orderRequest.getLocation());
            newOrder.setStatus("Confirmed"); // Or "Pending", "Booked", etc.
            newOrder.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());

            // Set service IDs and Names based on confirmed services
            if (venue != null) {
                newOrder.setVenueId(orderRequest.getVenueReservationId());
                newOrder.setVenueName(venue.getName());
            }
            if (photographer != null) {
                newOrder.setPhotographerId(orderRequest.getPhotographerReservationId());
                newOrder.setPhotographerName(photographer.getName());
            }
            if (catering != null) {
                newOrder.setCateringId(orderRequest.getCateringReservationId());
                newOrder.setCateringName(catering.getName());
            }

            newOrder.setAddress(orderRequest.getAddress());
            newOrder.setPaymentDetails(orderRequest.getPaymentDetails());

            orderRepository.save(newOrder); // Save the order to your local database

            redirectAttributes.addFlashAttribute("venue", venue);
            redirectAttributes.addFlashAttribute("photographer", photographer);
            redirectAttributes.addFlashAttribute("catering", catering);

            redirectAttributes.addFlashAttribute("totalPrice", totalPrice);
            redirectAttributes.addFlashAttribute("date", orderRequest.getDate());
            redirectAttributes.addFlashAttribute("location", orderRequest.getLocation());
            redirectAttributes.addFlashAttribute("address", orderRequest.getAddress());
            redirectAttributes.addFlashAttribute("paymentDetails", orderRequest.getPaymentDetails());

            redirectAttributes.addFlashAttribute("message", "Order successfully booked and saved!");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/order/success";

    }


    @Value("${venue.api.url}") // Inject the value from application.properties
    private String appBaseUrl;

    @Value("${photographer.api.url}") // Inject the value from application.properties
    private String photographerBaseUrl;

    @Value("${catering.api.url}")
    private String cateringBaseUrl;

    // This method now serves as the final confirmation page after the order is saved
    @GetMapping("/success") // Responds to GET /order/success
    public String orderSuccess(Model model) {

        model.addAttribute("appBaseUrl", appBaseUrl);
        model.addAttribute("photographerBaseUrl", photographerBaseUrl);
        model.addAttribute("cateringBaseUrl", cateringBaseUrl);

        // Flash attributes from placeOrder will automatically be added to the model here.
        // If 'complete.html' still contains a form using th:object="${order}", add it here:
        model.addAttribute("order", new Order()); // Provide an empty Order object for form binding if needed
        return "complete"; // Refers to src/main/resources/templates/complete.html
    }

    // NEW: User's view: list orders for a specific user ID
    @GetMapping("/my-orders/{userId}") // Responds to GET /order/my-orders/{userId}
    public String getUserOrders(@PathVariable String userId, Model model, Authentication auth) {
        // IMPORTANT SECURITY NOTE: In a real application, you should NEVER trust the userId from the path variable
        // for displaying user-specific data. Instead, you should retrieve the authenticated user's ID
        // from Spring Security context (e.g., Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String authenticatedUserId = authentication.getName(); // Or whatever property holds your userId
        // Then use authenticatedUserId instead of the @PathVariable userId for the lookup.
        // This prevents users from viewing other people's orders by just changing the URL.

        List<Order> userOrders = orderRepository.findByUserId(userId);
        model.addAttribute("orders", userOrders);
        model.addAttribute("viewingUserId", userId); // Optionally pass the userId for display on the page


        if (!auth.getName().equals(userId)) {
            return "redirect:/";        // <-- home page
        }

        return "myOrders"; // Assumes you create a Thymeleaf HTML file named myOrders.html
    }

    @GetMapping("/errorconfirm")
    public String error(RedirectAttributes redirectAttributes,
                        Model model) {
        return "errorconfirm";
    }
}