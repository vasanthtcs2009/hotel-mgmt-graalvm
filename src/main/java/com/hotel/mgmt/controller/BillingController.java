package com.hotel.mgmt.controller;

import com.hotel.mgmt.dto.InvoiceDto;
import com.hotel.mgmt.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<InvoiceDto> generateInvoice(@PathVariable Long reservationId) {
        return ResponseEntity.ok(billingService.generateInvoice(reservationId));
    }
}
