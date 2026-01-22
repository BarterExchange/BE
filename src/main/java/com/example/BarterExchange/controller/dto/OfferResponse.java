package com.example.BarterExchange.controller.dto;

import com.example.BarterExchange.domain.Offer;
import com.example.BarterExchange.domain.OfferStatus;
import lombok.Getter;

@Getter
public class OfferResponse {

    private final Long id;
    private final Long itemId;
    private final String proposer;
    private final OfferStatus status;

    public OfferResponse(Offer offer) {
        this.id = offer.getId();
        this.itemId = offer.getItem().getId();
        this.proposer = offer.getProposer().getUsername();
        this.status = offer.getStatus();
    }
}
