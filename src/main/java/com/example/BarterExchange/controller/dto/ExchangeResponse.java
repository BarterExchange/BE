package com.example.BarterExchange.controller.dto;

import com.example.BarterExchange.domain.Exchange;
import java.time.Instant;
import lombok.Getter;

@Getter
public class ExchangeResponse {

    private final Long id;
    private final Long offerId;
    private final Long itemId;
    private final boolean ownerConfirmed;
    private final boolean proposerConfirmed;
    private final Instant completedAt;

    public ExchangeResponse(Exchange exchange) {
        this.id = exchange.getId();
        this.offerId = exchange.getOffer().getId();
        this.itemId = exchange.getItem().getId();
        this.ownerConfirmed = exchange.isOwnerConfirmed();
        this.proposerConfirmed = exchange.isProposerConfirmed();
        this.completedAt = exchange.getCompletedAt();
    }
}
