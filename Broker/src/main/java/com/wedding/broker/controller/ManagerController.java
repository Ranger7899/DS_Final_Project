package com.wedding.broker.controller;

import com.wedding.broker.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "manager/orders";
    }


    @Value("${venue.service.api.base-url}")
    private String venueServiceApiBaseUrl;

    @Value("${photographer.service.api.base-url}")
    private String photographerServiceApiBaseUrl;

    @Value("${catering.service.api.base-url}")
    private String cateringServiceApiBaseUrl;

    @Value("${venue.api.url}")
    private String appBaseUrl;
}