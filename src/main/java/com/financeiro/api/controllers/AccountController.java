package com.financeiro.api.controllers;

import com.financeiro.api.dtos.AccountRequestDTO;
import com.financeiro.api.dtos.AccountResponseDTO;
import com.financeiro.api.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@RequestBody @Valid AccountRequestDTO data) {
        AccountResponseDTO response = accountService.create(data);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> list() {
        return ResponseEntity.ok(accountService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> update(@PathVariable Long id, @RequestBody @Valid AccountRequestDTO data) {
        return ResponseEntity.ok(accountService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}