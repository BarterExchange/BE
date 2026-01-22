package com.example.BarterExchange.controller;

import com.example.BarterExchange.controller.dto.ExchangeResponse;
import com.example.BarterExchange.domain.Exchange;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping("/{exchangeId}/confirm/owner")
    public ExchangeResponse confirmOwner(
        @AuthenticationPrincipal User user,
        @PathVariable Long exchangeId
    ) {
        Exchange exchange = exchangeService.confirmOwner(user.getUsername(), exchangeId);
        return new ExchangeResponse(exchange);
    }

    @PostMapping("/{exchangeId}/confirm/proposer")
    public ExchangeResponse confirmProposer(
        @AuthenticationPrincipal User user,
        @PathVariable Long exchangeId
    ) {
        Exchange exchange = exchangeService.confirmProposer(user.getUsername(), exchangeId);
        return new ExchangeResponse(exchange);
    }
}
