package com.financeiro.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "O nome de usuário é obrigatório") 
        String username,
        
        @NotBlank(message = "A senha é obrigatória") 
        String password,
        
        String profilePictureUrl
) {}