package com.financeiro.api.services;

import com.financeiro.api.dtos.CategoryRequestDTO;
import com.financeiro.api.dtos.CategoryResponseDTO;
import com.financeiro.api.models.Category;
import com.financeiro.api.models.User;
import com.financeiro.api.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public CategoryResponseDTO create(CategoryRequestDTO data) {
        Category category = Category.builder()
                .user(getAuthenticatedUser())
                .name(data.name())
                .type(data.type())
                .colorHex(data.colorHex())
                .isActive(true)
                .build();
                
        return new CategoryResponseDTO(categoryRepository.save(category));
    }

    public List<CategoryResponseDTO> listAll() {
        return categoryRepository.findByUserAndIsActiveTrue(getAuthenticatedUser())
                .stream()
                .map(CategoryResponseDTO::new)
                .toList();
    }

    public CategoryResponseDTO update(Long id, CategoryRequestDTO data) {
        Category category = categoryRepository.findByIdAndUserAndIsActiveTrue(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setName(data.name());
        category.setType(data.type());
        category.setColorHex(data.colorHex());

        return new CategoryResponseDTO(categoryRepository.save(category));
    }

    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndUserAndIsActiveTrue(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        category.setIsActive(false);
        categoryRepository.save(category);
    }
}