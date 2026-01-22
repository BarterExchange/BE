package com.example.BarterExchange.service;

import com.example.BarterExchange.domain.Exchange;
import com.example.BarterExchange.domain.Item;
import com.example.BarterExchange.domain.ItemStatus;
import com.example.BarterExchange.domain.User;
import com.example.BarterExchange.repository.ExchangeRepository;
import com.example.BarterExchange.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Exchange confirmOwner(String ownerUsername, Long exchangeId) {
        // Transaction boundary: confirm + item status update must be atomic.
        Exchange exchange = exchangeRepository.findById(exchangeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exchange not found"));
        User owner = userRepository.findByUsername(ownerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!exchange.getOwner().getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can confirm");
        }
        exchange.setOwnerConfirmed(true);
        return finalizeIfComplete(exchange);
    }

    @Transactional
    public Exchange confirmProposer(String proposerUsername, Long exchangeId) {
        // Transaction boundary: confirm + item status update must be atomic.
        Exchange exchange = exchangeRepository.findById(exchangeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exchange not found"));
        User proposer = userRepository.findByUsername(proposerUsername)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!exchange.getProposer().getId().equals(proposer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the proposer can confirm");
        }
        exchange.setProposerConfirmed(true);
        return finalizeIfComplete(exchange);
    }

    private Exchange finalizeIfComplete(Exchange exchange) {
        if (exchange.isOwnerConfirmed() && exchange.isProposerConfirmed()) {
            Item item = exchange.getItem();
            if (item.getStatus() != ItemStatus.RESERVED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Item is not reserved");
            }
            item.setStatus(ItemStatus.EXCHANGED);
            exchange.setCompletedAt(Instant.now());
        }
        return exchange;
    }
}
