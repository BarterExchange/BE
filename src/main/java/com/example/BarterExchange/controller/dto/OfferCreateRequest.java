package com.example.BarterExchange.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferCreateRequest {

    @NotNull
    private Long itemId;
}
