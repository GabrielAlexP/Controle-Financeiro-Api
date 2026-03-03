package com.financeiro.api.dtos;

import com.financeiro.api.enums.TransactionType;
import com.financeiro.api.models.Category;

public record CategoryResponseDTO(
        Long id, 
        String name, 
        TransactionType type, 
        String icon,
        String colorHex, 
        Boolean isActive
) {
    public CategoryResponseDTO(Category category) {
        this(
            category.getId(), 
            category.getName(), 
            category.getType(), 
            category.getIcon(),
            category.getColorHex(), 
            category.getIsActive()
        );
    }
}