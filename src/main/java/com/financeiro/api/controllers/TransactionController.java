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
    public ResponseEntity<List<TransactionResponseDTO>> create(@RequestBody @Valid TransactionRequestDTO data) {
        List<TransactionResponseDTO> responses = transactionService.create(data);
        return ResponseEntity.status(201).body(responses);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> list() {
        return ResponseEntity.ok(transactionService.listAll());
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<TransactionResponseDTO> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.markAsPaid(id));
    }

    @PutMapping("/{id}/antecipar")
    public ResponseEntity<TransactionResponseDTO> advanceTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.advanceTransaction(id));
    }

    @PostMapping("/pagar-fatura/{cardId}")
    public ResponseEntity<Void> payCreditCardBill(@PathVariable Long cardId, @RequestParam String month) {
        transactionService.payCreditCardBill(cardId, month);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}