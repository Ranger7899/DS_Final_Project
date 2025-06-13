package com.wedding.broker.controller;

import com.wedding.broker.client.CateringClient;
import com.wedding.broker.client.PhotographerClient;
import com.wedding.broker.client.VenueClient;
import com.wedding.broker.messaging.OrderEventPublisher;     // ✨ NEW
import com.wedding.broker.model.*;
import com.wedding.broker.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired private OrderRepository       orderRepository;
    @Autowired private VenueClient           venueClient;
    @Autowired private PhotographerClient    photographerClient;
    @Autowired private CateringClient        cateringClient;
    @Autowired private OrderEventPublisher   orderEventPublisher;   // ✨ NEW

    /* -----------------------------------------------------------
       STEP 1: user hits “Pay” → create Order + enqueue message
       ----------------------------------------------------------- */
    @PostMapping
    public String placeOrder(@ModelAttribute OrderRequest orderRequest,
                             Model model,
                             RedirectAttributes redirect) {

        Venue        venue        = null;
        Photographer photographer = null;
        Catering     catering     = null;
        int totalPrice = 0;

        // ── venue look-up (optional) ────────────────────────────────────────────
        if (orderRequest.getVenueId() != null && !orderRequest.getVenueId().isEmpty()) {
            try {
                venue = venueClient.getVenueById(orderRequest.getVenueId());
                totalPrice += venue.getPrice();
            } catch (Exception ex) {
                logger.warn("Venue service offline – proceeding without details", ex);
            }
        }

        // ── photographer look-up (optional) ─────────────────────────────────────
        if (orderRequest.getPhotographerId() != null && !orderRequest.getPhotographerId().isEmpty()) {
            try {
                photographer = photographerClient.getPhotographerById(orderRequest.getPhotographerId());
                totalPrice += photographer.getPrice();
            } catch (Exception ex) {
                logger.warn("Photographer service offline – proceeding without details", ex);
            }
        }

        // ── catering look-up (optional) ─────────────────────────────────────────
        if (orderRequest.getCateringId() != null && !orderRequest.getCateringId().isEmpty()) {
            try {
                catering = cateringClient.getCateringCompanyById(orderRequest.getCateringId());
                totalPrice += catering.getPrice();
            } catch (Exception ex) {
                logger.warn("Catering service offline – proceeding without details", ex);
            }
        }

        // ── build & save order row ──────────────────────────────────────────────
        Order newOrder = new Order();
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setUpdatedAt(LocalDateTime.now());
        newOrder.setDate(orderRequest.getDate());
        newOrder.setLocation(orderRequest.getLocation());
        newOrder.setStatus("PENDING");
        newOrder.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());

        newOrder.setVenueId(orderRequest.getVenueReservationId());
        newOrder.setPhotographerId(orderRequest.getPhotographerReservationId());
        newOrder.setCateringId(orderRequest.getCateringReservationId());

        if (venue != null)         newOrder.setVenueName(venue.getName());
        if (photographer != null)  newOrder.setPhotographerName(photographer.getName());
        if (catering != null)      newOrder.setCateringName(catering.getName());

        newOrder.setAddress(orderRequest.getAddress());
        newOrder.setPaymentDetails(orderRequest.getPaymentDetails());

        orderRepository.save(newOrder);

        // ── enqueue for background confirmation ────────────────────────────────
        orderEventPublisher.publish(newOrder.getId());

        // ── data for “complete” page ────────────────────────────────────────────
        redirect.addFlashAttribute("venue",        venue);
        redirect.addFlashAttribute("photographer", photographer);
        redirect.addFlashAttribute("catering",     catering);
        redirect.addFlashAttribute("totalPrice",   totalPrice);
        redirect.addFlashAttribute("date",         orderRequest.getDate());
        redirect.addFlashAttribute("location",     orderRequest.getLocation());
        redirect.addFlashAttribute("address",      orderRequest.getAddress());
        redirect.addFlashAttribute("paymentDetails", orderRequest.getPaymentDetails());
        redirect.addFlashAttribute("message",
                "Thanks! We’re finalising your booking now – you’ll receive confirmation within 15 minutes.");

        return "redirect:/order/success";
    }

    /* -----------------------------------------------------------
       STEP 2: user lands on success page while worker is running
       ----------------------------------------------------------- */
    @GetMapping("/success")
    public String orderSuccess(Model model) {
        model.addAttribute("appBaseUrl",             appBaseUrl);
        model.addAttribute("photographerBaseUrl",    photographerBaseUrl);
        model.addAttribute("cateringBaseUrl",        cateringBaseUrl);
        model.addAttribute("order", new Order());            // keeps template happy
        return "complete";
    }

    /* -------------- existing endpoints left unchanged -------------- */

    @GetMapping("/my-orders/{userId}")
    public String getUserOrders(@PathVariable String userId,
                                Model model,
                                Authentication auth) {
        List<Order> userOrders = orderRepository.findByUserId(userId);
        model.addAttribute("orders", userOrders);
        model.addAttribute("viewingUserId", userId);

        if (!auth.getName().equals(userId)) {
            return "redirect:/";
        }
        return "myOrders";
    }

    @GetMapping("/errorconfirm")
    public String error(RedirectAttributes redirect, Model model) {
        return "errorconfirm";
    }

    /* -------- base-URL helpers injected from application.properties -------- */
    @Value("${venue.api.url}")        private String appBaseUrl;
    @Value("${photographer.api.url}") private String photographerBaseUrl;
    @Value("${catering.api.url}")     private String cateringBaseUrl;
}
