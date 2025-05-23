package com.wedding.broker.controller;

import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/services")
    public String services(@RequestParam String date, @RequestParam String location, Model model) {
        // Query suppliers for available services
        // Add to model
        return "services";
    }

    @PostMapping("/order")
    public String placeOrder(OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "redirect:/confirmation";
    }
}