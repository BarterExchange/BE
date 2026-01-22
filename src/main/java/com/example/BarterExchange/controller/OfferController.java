package com.example.BarterExchange.controller;

import com.example.BarterExchange.controller.dto.ExchangeResponse;
import com.example.BarterExchange.controller.dto.OfferCreateRequest;
import com.example.BarterExchange.controller.dto.OfferResponse;
import com.example.BarterExchange.domain.Exchange;
import com.example.BarterExchange.domain.Offer;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public OfferResponse createOffer(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody OfferCreateRequest request
    ) {
        Offer offer = offerService.createOffer(user.getUsername(), request.getItemId());
        return new OfferResponse(offer);
    }

    @PostMapping("/{offerId}/accept")
    public ExchangeResponse acceptOffer(
        @AuthenticationPrincipal User user,
        @PathVariable Long offerId
    ) {
        Exchange exchange = offerService.acceptOffer(user.getUsername(), offerId);
        return new ExchangeResponse(exchange);
    }

    @PostMapping("/{offerId}/reject")
    public OfferResponse rejectOffer(
        @AuthenticationPrincipal User user,
        @PathVariable Long offerId
    ) {
        Offer offer = offerService.rejectOffer(user.getUsername(), offerId);
        return new OfferResponse(offer);
    }
}
