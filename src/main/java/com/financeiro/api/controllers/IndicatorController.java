package com.financeiro.api.controllers;

import com.financeiro.api.services.BacenIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/indicadores")
public class IndicatorController {

    @Autowired
    private BacenIndicatorService bacenService;

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getIndicators() {
        return ResponseEntity.ok(bacenService.getIndicators());
    }
}