package com.financeiro.api.controllers;

import com.financeiro.api.dtos.GoalRequestDTO;
import com.financeiro.api.dtos.GoalResponseDTO;
import com.financeiro.api.dtos.GoalUpdateAmountDTO;
import com.financeiro.api.services.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/caixinhas")
public class GoalController {

    @Autowired private GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponseDTO> create(@RequestBody @Valid GoalRequestDTO data) {
        return ResponseEntity.status(201).body(goalService.create(data));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> list() {
        return ResponseEntity.ok(goalService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> updateAmount(@PathVariable Long id, @RequestBody @Valid GoalUpdateAmountDTO data) {
        return ResponseEntity.ok(goalService.updateAmount(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}