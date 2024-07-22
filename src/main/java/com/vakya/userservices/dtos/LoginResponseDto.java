package com.vakya.userservices.dtos;

import com.vakya.userservices.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private Token token;
}
