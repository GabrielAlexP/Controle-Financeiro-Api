package com.financeiro.api.controllers;

import com.financeiro.api.dtos.TransactionRequestDTO;
import com.financeiro.api.dtos.TransactionResponseDTO;
import com.financeiro.api.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody @Valid TransactionRequestDTO data) {
        TransactionResponseDTO response = transactionService.create(data);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> list() {
        return ResponseEntity.ok(transactionService.listAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}