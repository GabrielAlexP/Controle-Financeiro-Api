package com.financeiro.api.controllers;

import com.financeiro.api.dtos.CreditCardRequestDTO;
import com.financeiro.api.dtos.CreditCardResponseDTO;
import com.financeiro.api.services.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardResponseDTO> create(@RequestBody @Valid CreditCardRequestDTO data) {
        return ResponseEntity.status(201).body(creditCardService.create(data));
    }

    @GetMapping
    public ResponseEntity<List<CreditCardResponseDTO>> list() {
        return ResponseEntity.ok(creditCardService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CreditCardRequestDTO data) {
        return ResponseEntity.ok(creditCardService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creditCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}