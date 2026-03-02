package com.financeiro.api.controllers;

import com.financeiro.api.dtos.CategoryRequestDTO;
import com.financeiro.api.dtos.CategoryResponseDTO;
import com.financeiro.api.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CategoryRequestDTO data) {
        CategoryResponseDTO response = categoryService.create(data);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> list() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @RequestBody @Valid CategoryRequestDTO data) {
        return ResponseEntity.ok(categoryService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}