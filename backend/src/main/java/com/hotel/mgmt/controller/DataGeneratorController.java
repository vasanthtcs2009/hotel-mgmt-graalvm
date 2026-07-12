package com.hotel.mgmt.controller;

import com.hotel.mgmt.service.DataGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generator")
public class DataGeneratorController {

    private final DataGeneratorService dataGeneratorService;

    public DataGeneratorController(DataGeneratorService dataGeneratorService) {
        this.dataGeneratorService = dataGeneratorService;
    }

    @PostMapping("/setup")
    public ResponseEntity<String> setupBaseData() {
        dataGeneratorService.generateBaseData();
        return ResponseEntity.ok("Base catalog data loaded successfully.");
    }

    @PostMapping("/millions")
    public ResponseEntity<String> generateMillions(
            @RequestParam(defaultValue = "20000") int customersCount,
            @RequestParam(defaultValue = "50000") int reservationsCount,
            @RequestParam(defaultValue = "100000") int ordersCount,
            @RequestParam(defaultValue = "400000") int orderItemsCount) {
        
        long start = System.currentTimeMillis();
        dataGeneratorService.generateMillionsOfRecords(customersCount, reservationsCount, ordersCount, orderItemsCount);
        long end = System.currentTimeMillis();
        
        return ResponseEntity.ok(String.format("Generated %d records across tables in %.2f seconds.", 
                customersCount + reservationsCount + ordersCount + orderItemsCount, 
                (end - start) / 1000.0));
    }
}
